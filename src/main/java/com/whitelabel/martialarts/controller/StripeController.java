package com.whitelabel.martialarts.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.whitelabel.martialarts.model.Payment;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.repository.SchoolRepository;
import com.whitelabel.martialarts.repository.StudentRepository;
import com.whitelabel.martialarts.service.service.PaymentService;
import com.whitelabel.martialarts.service.service.StripeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    private static final Logger logger = LoggerFactory.getLogger(StripeController.class);

    @Autowired
    private StripeService stripeService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @RequestParam Long studentId,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

            Map<String, String> sessionData = stripeService.createCheckoutSession(student, amount, description);
            return ResponseEntity.ok(sessionData);
        } catch (Exception e) {
            logger.error("Error creating checkout session", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Verify the webhook signature
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            
            // Get the account ID if this is from a connected account
            String connectedAccountId = event.getAccount();
            
            // Process the event based on its type
            switch (event.getType()) {
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(event, connectedAccountId);
                    break;
                case "account.updated":
                    handleAccountUpdated(event);
                    break;
                default:
                    logger.info("Unhandled event type: {}", event.getType());
            }

            return ResponseEntity.ok("Webhook received");
        } catch (SignatureVerificationException e) {
            logger.error("Invalid signature", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            logger.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook: " + e.getMessage());
        }
    }

    private void handleCheckoutSessionCompleted(Event event, String connectedAccountId) throws StripeException {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        
        if (dataObjectDeserializer.getObject().isPresent()) {
            StripeObject stripeObject = dataObjectDeserializer.getObject().get();
            
            if (stripeObject instanceof Session) {
                Session session = (Session) stripeObject;
                
                // Get the student ID from the client_reference_id
                String clientReferenceId = session.getClientReferenceId();
                if (clientReferenceId != null) {
                    Long studentId = Long.parseLong(clientReferenceId);
                    Student student = studentRepository.findById(studentId)
                            .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));
                    
                    // Process the payment
                    Payment payment = stripeService.createPaymentFromSession(session, student);
                    
                    // If this is from a connected account, set the school
                    if (connectedAccountId != null) {
                        Optional<School> school = schoolRepository.findByStripeConnectAccountId(connectedAccountId);
                        school.ifPresent(s -> {
                            payment.setSchool(s);
                            payment.setConnectedAccountId(connectedAccountId);
                        });
                    } else {
                        // Check if there's a connected account ID in the metadata
                        Map<String, String> metadata = session.getMetadata();
                        if (metadata != null && metadata.containsKey("connected_account")) {
                            String accountId = metadata.get("connected_account");
                            Optional<School> school = schoolRepository.findByStripeConnectAccountId(accountId);
                            school.ifPresent(s -> {
                                payment.setSchool(s);
                                payment.setConnectedAccountId(accountId);
                            });
                        }
                    }
                    
                    paymentService.savePayment(payment);
                    
                    logger.info("Payment processed successfully for student ID: {}", studentId);
                } else {
                    logger.warn("No client reference ID found in session");
                }
            } else {
                logger.warn("Unexpected Stripe object type");
            }
        } else {
            logger.warn("Could not deserialize event data object");
        }
    }
    
    private void handleAccountUpdated(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        
        if (dataObjectDeserializer.getObject().isPresent()) {
            StripeObject stripeObject = dataObjectDeserializer.getObject().get();
            
            if (stripeObject instanceof com.stripe.model.Account) {
                com.stripe.model.Account account = (com.stripe.model.Account) stripeObject;
                String accountId = account.getId();
                
                // Update the school's account status if needed
                Optional<School> school = schoolRepository.findByStripeConnectAccountId(accountId);
                school.ifPresent(s -> {
                    boolean wasEnabled = s.isStripeConnectEnabled();
                    boolean isNowEnabled = account.getChargesEnabled() && account.getPayoutsEnabled();
                    
                    if (wasEnabled != isNowEnabled) {
                        s.setStripeConnectEnabled(isNowEnabled);
                        schoolRepository.save(s);
                        logger.info("Updated school {} Stripe Connect enabled status to: {}", s.getId(), isNowEnabled);
                    }
                });
            }
        }
    }

    @GetMapping("/payment/success")
    public ResponseEntity<Map<String, String>> paymentSuccess(@RequestParam("session_id") String sessionId) {
        try {
            Payment payment = paymentService.processStripePayment(sessionId);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("paymentId", payment.getId().toString());
            
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
