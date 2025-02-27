package com.whitelabel.martialarts.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.checkout.Session;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Price;
import com.stripe.net.ApiResource;
import com.stripe.net.OAuth;
import com.stripe.net.RequestOptions;
import com.stripe.net.StripeResponse;
import com.stripe.model.oauth.TokenResponse;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.whitelabel.martialarts.model.Payment;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.model.Subscription;
import com.whitelabel.martialarts.model.SubscriptionInterval;
import com.whitelabel.martialarts.repository.SchoolRepository;
import com.whitelabel.martialarts.repository.SubscriptionRepository;
import com.whitelabel.martialarts.service.service.StripeService;
import com.whitelabel.martialarts.service.service.StudentService;
import com.whitelabel.martialarts.config.StripeConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeServiceImpl.class);

    @Autowired
    private StripeConfig stripeConfig;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    public StripeServiceImpl(StripeConfig stripeConfig) {
        // Stripe.apiKey is already set in StripeConfig's @PostConstruct method
        // No need to set it again here
    }

    @Override
    public Map<String, String> createCheckoutSession(Student student, BigDecimal amount, String description) throws StripeException {
        // Get the school's Stripe Connect account ID
        School school = student.getSchool();
        if (school == null || school.getStripeConnectAccountId() == null) {
            throw new IllegalStateException("Student's school does not have a valid Stripe Connect account");
        }
        
        // Check if the Connect account is enabled
        if (!isConnectAccountEnabled(school)) {
            throw new IllegalStateException("School's Stripe Connect account is not fully enabled");
        }
        
        // Convert amount to cents (Stripe uses smallest currency unit)
        long amountInCents = amount.multiply(new BigDecimal("100")).longValue();
        
        // Calculate platform fee (e.g., 10% of the amount)
        long applicationFeeAmount = (amountInCents * stripeConfig.getPlatformFeePercentage()) / 100;

        // Create metadata to store the connected account ID
        Map<String, String> metadata = new HashMap<>();
        metadata.put("description", description);
        metadata.put("connected_account", school.getStripeConnectAccountId());

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripeConfig.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(stripeConfig.getCancelUrl())
                .setCustomerEmail(student.getEmail())
                .setClientReferenceId(student.getId().toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(description)
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                )
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .setApplicationFeeAmount(applicationFeeAmount)
                                .build()
                )
                .putAllMetadata(metadata)
                .build();

        // Create the session with the connected account
        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(school.getStripeConnectAccountId())
                .build();
        
        Session session = Session.create(params, requestOptions);
        
        Map<String, String> responseData = new HashMap<>();
        responseData.put("sessionId", session.getId());
        responseData.put("url", session.getUrl());
        
        return responseData;
    }

    @Override
    public Payment createPaymentFromSession(Session session, Student student) {
        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setAmount(new BigDecimal(session.getAmountTotal()).divide(new BigDecimal("100")));
        payment.setCurrency(session.getCurrency());
        payment.setPaymentIntentId(session.getPaymentIntent());
        payment.setStatus(session.getPaymentStatus());
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        
        // Set description from metadata if available
        Map<String, String> metadata = session.getMetadata();
        if (metadata != null && metadata.containsKey("description")) {
            payment.setDescription(metadata.get("description"));
        }
        
        return payment;
    }

    @Override
    public Session retrieveSession(String sessionId) throws StripeException {
        // Simple implementation - assumes we're retrieving from the platform account
        try {
            return Session.retrieve(sessionId);
        } catch (StripeException e) {
            // If we can't find the session on the platform account, it might be on a connected account
            // We would need to know which connected account to check
            // For now, just rethrow the exception
            throw e;
        }
    }
    
    /**
     * Retrieve a session from a specific connected account
     */
    public Session retrieveConnectedAccountSession(String sessionId, String connectedAccountId) throws StripeException {
        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(connectedAccountId)
                .build();
        
        return Session.retrieve(sessionId, requestOptions);
    }
    
    @Override
    @Transactional
    public String createConnectAccount(School school) throws StripeException {
        // Create a Standard Connect account
        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.STANDARD)
                .setEmail(school.getEmail())
                .setBusinessType(AccountCreateParams.BusinessType.COMPANY)
                .setBusinessProfile(
                        AccountCreateParams.BusinessProfile.builder()
                                .setName(school.getName())
                                .setUrl(school.getWebsite())
                                .build()
                )
                .setMetadata(Map.of("school_id", school.getId().toString()))
                .build();
        
        Account account = Account.create(params);
        
        // Update the school with the new account ID
        school.setStripeConnectAccountId(account.getId());
        school.setStripeConnectEnabled(false); // Will be enabled after onboarding
        schoolRepository.save(school);
        
        return account.getId();
    }
    
    @Override
    public String createConnectAccountLink(School school, String returnUrl) throws StripeException {
        if (school.getStripeConnectAccountId() == null) {
            throw new IllegalStateException("School does not have a Stripe Connect account ID");
        }
        
        AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                .setAccount(school.getStripeConnectAccountId())
                .setRefreshUrl(returnUrl)
                .setReturnUrl(returnUrl)
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();
        
        AccountLink accountLink = AccountLink.create(params);
        
        return accountLink.getUrl();
    }
    
    @Override
    @Transactional
    public School handleConnectOAuthCallback(String code, Long schoolId) throws StripeException {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("School not found with ID: " + schoolId));
        
        // Instead of using TokenResponse which doesn't have all the fields we need,
        // let's use a Map to get the raw response
        Map<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("grant_type", "authorization_code");
        tokenParams.put("code", code);
        tokenParams.put("client_id", stripeConfig.getConnectClientId());
        
        try {
            // Use the OAuth.token method to get the TokenResponse
            TokenResponse tokenResponse = OAuth.token(tokenParams, null);
            String accountId = tokenResponse.getStripeUserId();
            
            // Since we can't directly access access_token and refresh_token,
            // we'll set them to empty strings for now
            // In a production environment, you might want to implement a different OAuth flow
            // or use a different version of the Stripe SDK that provides these fields
            String accessToken = "";
            String refreshToken = "";
            
            // Log for debugging
            logger.info("TokenResponse: " + tokenResponse);
            
            // Update the school with the Connect account details
            school.setStripeConnectAccountId(accountId);
            school.setStripeConnectAccessToken(accessToken);
            school.setStripeConnectRefreshToken(refreshToken);
            school.setStripeConnectEnabled(true);
            
            return schoolRepository.save(school);
        } catch (Exception e) {
            throw new StripeException("Error processing OAuth token: " + e.getMessage(), null, null, 0) {
                private static final long serialVersionUID = 1L;
            };
        }
    }
    
    @Override
    public boolean isConnectAccountEnabled(School school) throws StripeException {
        if (school.getStripeConnectAccountId() == null) {
            return false;
        }
        
        // Retrieve the account to check its status
        Account account = Account.retrieve(school.getStripeConnectAccountId());
        
        // Check if the account is active and has completed the necessary requirements
        return account.getChargesEnabled() && account.getPayoutsEnabled();
    }
    
    @Override
    @Transactional
    public School importConnectAccount(School school, String accountId) throws StripeException {
        // Verify that the account exists and is valid
        Account account = Account.retrieve(accountId);
        
        if (account == null) {
            throw new StripeException("Account not found", null, null, 0) {
                private static final long serialVersionUID = 1L;
            };
        }
        
        // Update the school with the Connect account details
        school.setStripeConnectAccountId(accountId);
        school.setStripeConnectEnabled(account.getChargesEnabled() && account.getPayoutsEnabled());
        
        return schoolRepository.save(school);
    }

    @Override
    public String createCustomer(Student student, String paymentMethodId) throws StripeException {
        // Get the school's Stripe Connect account ID
        School school = student.getSchool();
        if (school == null || school.getStripeConnectAccountId() == null) {
            throw new IllegalStateException("Student's school does not have a valid Stripe Connect account");
        }
        
        // Create customer parameters
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(student.getEmail())
                .setName(student.getFirstName() + " " + student.getLastName())
                .setDescription("Student ID: " + student.getId())
                .build();
        
        // Create the customer with the connected account
        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(school.getStripeConnectAccountId())
                .build();
        
        Customer customer = Customer.create(params, requestOptions);
        
        // Attach the payment method to the customer if provided
        if (paymentMethodId != null && !paymentMethodId.isEmpty()) {
            Map<String, Object> paymentMethodParams = new HashMap<>();
            paymentMethodParams.put("customer", customer.getId());
            
            PaymentMethod paymentMethod = 
                PaymentMethod.retrieve(paymentMethodId, requestOptions);
            paymentMethod.attach(paymentMethodParams, requestOptions);
            
            // Set as default payment method
            Map<String, Object> customerParams = new HashMap<>();
            customerParams.put("invoice_settings", 
                Map.of("default_payment_method", paymentMethodId));
            
            customer.update(customerParams, requestOptions);
        }
        
        return customer.getId();
    }

    @Override
    public Subscription createSubscription(Student student, String customerId, String paymentMethodId, 
                                         BigDecimal amount, SubscriptionInterval interval) throws StripeException {
        // Get the school's Stripe Connect account ID
        School school = student.getSchool();
        if (school == null || school.getStripeConnectAccountId() == null) {
            throw new IllegalStateException("Student's school does not have a valid Stripe Connect account");
        }
        
        // Convert amount to cents
        long amountInCents = amount.multiply(new BigDecimal("100")).longValue();
        
        // Calculate platform fee
        long applicationFeePercent = stripeConfig.getPlatformFeePercentage();
        
        // Create request options for the connected account
        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(school.getStripeConnectAccountId())
                .build();
        
        // Create a price for the subscription
        Map<String, Object> priceParams = new HashMap<>();
        priceParams.put("unit_amount", amountInCents);
        priceParams.put("currency", "usd");
        priceParams.put("recurring", Map.of(
            "interval", interval == SubscriptionInterval.MONTHLY ? "month" : "week",
            "interval_count", interval == SubscriptionInterval.MONTHLY ? 1 : 2
        ));
        priceParams.put("product_data", Map.of(
            "name", "Martial Arts Training - " + interval.getDisplayName()
        ));
        
        Price price = Price.create(priceParams, requestOptions);
        
        // Create the subscription
        Map<String, Object> subscriptionParams = new HashMap<>();
        subscriptionParams.put("customer", customerId);
        subscriptionParams.put("items", Map.of(
            "0", Map.of("price", price.getId())
        ));
        subscriptionParams.put("application_fee_percent", applicationFeePercent);
        subscriptionParams.put("default_payment_method", paymentMethodId);
        
        com.stripe.model.Subscription stripeSubscription = 
            com.stripe.model.Subscription.create(subscriptionParams, requestOptions);
        
        // Create and save the subscription entity
        Subscription subscription = new Subscription();
        subscription.setStudent(student);
        subscription.setAmount(amount);
        subscription.setInterval(interval);
        subscription.setStripeCustomerId(customerId);
        subscription.setStripeSubscriptionId(stripeSubscription.getId());
        subscription.setActive(true);
        
        // Calculate next billing date based on interval
        LocalDate nextBillingDate;
        if (interval == SubscriptionInterval.MONTHLY) {
            nextBillingDate = LocalDate.now().plusMonths(1);
        } else {
            nextBillingDate = LocalDate.now().plusWeeks(2);
        }
        subscription.setNextBillingDate(nextBillingDate);
        
        return subscriptionRepository.save(subscription);
    }

    @Override
    public com.stripe.model.checkout.Session createSubscriptionCheckoutSession(
            Student student, BigDecimal amount, SubscriptionInterval interval) throws StripeException {
        
        // Get the school's Stripe Connect account ID
        School school = student.getSchool();
        boolean useDirectCheckout = false;
        RequestOptions requestOptions = null;
        
        // Check if school has Stripe Connect set up
        if (school == null || school.getStripeConnectAccountId() == null) {
            // For testing: Use direct checkout without Connect
            useDirectCheckout = true;
            this.logger.warn("School does not have Stripe Connect account. Using direct checkout for testing.");
        } else {
            // Create request options for the connected account
            requestOptions = RequestOptions.builder()
                    .setStripeAccount(school.getStripeConnectAccountId())
                    .build();
        }
        
        // Convert amount to cents
        long amountInCents = amount.multiply(new BigDecimal("100")).longValue();
        
        // Calculate platform fee
        long applicationFeePercent = stripeConfig.getPlatformFeePercentage();
        
        // Create a price for the subscription
        Map<String, Object> priceParams = new HashMap<>();
        priceParams.put("unit_amount", amountInCents);
        priceParams.put("currency", "usd");
        priceParams.put("recurring", Map.of(
            "interval", interval == SubscriptionInterval.MONTHLY ? "month" : "week",
            "interval_count", interval == SubscriptionInterval.MONTHLY ? 1 : 2
        ));
        priceParams.put("product_data", Map.of(
            "name", "Martial Arts Training - " + interval.getDisplayName()
        ));
        
        com.stripe.model.Price price;
        if (useDirectCheckout) {
            price = com.stripe.model.Price.create(priceParams);
        } else {
            price = com.stripe.model.Price.create(priceParams, requestOptions);
        }
        
        // Create the checkout session
        Map<String, Object> sessionParams = new HashMap<>();
        sessionParams.put("mode", "subscription");
        sessionParams.put("success_url", stripeConfig.getBaseUrl() + "/students/payment/success?session_id={CHECKOUT_SESSION_ID}");
        sessionParams.put("cancel_url", stripeConfig.getBaseUrl() + "/students/payment/cancel?session_id={CHECKOUT_SESSION_ID}");
        sessionParams.put("customer_email", student.getEmail());
        sessionParams.put("client_reference_id", student.getId().toString());
        sessionParams.put("line_items", List.of(
            Map.of(
                "price", price.getId(),
                "quantity", 1
            )
        ));
        
        // Only add application fee for Connect accounts
        if (!useDirectCheckout) {
            sessionParams.put("subscription_data", Map.of(
                "application_fee_percent", applicationFeePercent
            ));
        }
        
        // Add metadata to track the subscription details
        Map<String, String> metadata = new HashMap<>();
        metadata.put("student_id", student.getId().toString());
        metadata.put("amount", amount.toString());
        metadata.put("interval", interval.toString());
        if (school != null) {
            metadata.put("school_id", school.getId().toString());
            metadata.put("school_name", school.getName());
        }
        sessionParams.put("metadata", metadata);
        
        // Create the session with or without Connect account
        if (useDirectCheckout) {
            return com.stripe.model.checkout.Session.create(sessionParams);
        } else {
            return com.stripe.model.checkout.Session.create(sessionParams, requestOptions);
        }
    }
    
    @Override
    public Subscription processSuccessfulSubscriptionCheckout(String sessionId) throws StripeException {
        // Retrieve the session
        com.stripe.model.checkout.Session session = com.stripe.model.checkout.Session.retrieve(sessionId);
        
        if (session == null || session.getSubscription() == null) {
            throw new IllegalStateException("No subscription found in checkout session");
        }
        
        // Get the student ID from the client reference ID
        Long studentId = Long.parseLong(session.getClientReferenceId());
        Student student = studentService.getStudentById(studentId);
        
        if (student == null) {
            throw new IllegalStateException("Student not found for ID: " + studentId);
        }
        
        // Check if we need to use a connected account
        School school = student.getSchool();
        boolean useDirectCheckout = (school == null || school.getStripeConnectAccountId() == null);
        RequestOptions requestOptions = null;
        
        if (!useDirectCheckout) {
            // Create request options for the connected account
            requestOptions = RequestOptions.builder()
                    .setStripeAccount(school.getStripeConnectAccountId())
                    .build();
        }
        
        // Get the subscription details from Stripe
        com.stripe.model.Subscription stripeSubscription;
        if (useDirectCheckout) {
            stripeSubscription = com.stripe.model.Subscription.retrieve(session.getSubscription());
        } else {
            stripeSubscription = com.stripe.model.Subscription.retrieve(
                session.getSubscription(), requestOptions);
        }
        
        // Get the subscription details from metadata
        Map<String, String> metadata = session.getMetadata();
        BigDecimal amount = new BigDecimal(metadata.get("amount"));
        SubscriptionInterval interval = SubscriptionInterval.valueOf(metadata.get("interval"));
        
        // Create and save the subscription entity
        Subscription subscription = new Subscription();
        subscription.setStudent(student);
        subscription.setAmount(amount);
        subscription.setInterval(interval);
        subscription.setStripeCustomerId(session.getCustomer());
        subscription.setStripeSubscriptionId(session.getSubscription());
        subscription.setActive(true);
        
        // Calculate next billing date based on interval
        LocalDate nextBillingDate;
        if (interval == SubscriptionInterval.MONTHLY) {
            nextBillingDate = LocalDate.now().plusMonths(1);
        } else {
            nextBillingDate = LocalDate.now().plusWeeks(2);
        }
        subscription.setNextBillingDate(nextBillingDate);
        
        return subscriptionRepository.save(subscription);
    }
    
    @Override
    public Student getStudentFromCheckoutSession(String sessionId) throws StripeException {
        // Retrieve the session
        com.stripe.model.checkout.Session session = com.stripe.model.checkout.Session.retrieve(sessionId);
        
        if (session == null || session.getClientReferenceId() == null) {
            return null;
        }
        
        // Get the student ID from the client reference ID
        Long studentId = Long.parseLong(session.getClientReferenceId());
        return studentService.getStudentById(studentId);
    }

    @Override
    public String generateAccountLink(String accountId, String returnUrl) throws StripeException {
        Stripe.apiKey = stripeConfig.getApiKey();
        
        AccountLinkCreateParams params = AccountLinkCreateParams.builder()
            .setAccount(accountId)
            .setRefreshUrl(returnUrl)
            .setReturnUrl(returnUrl)
            .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
            .build();
            
        AccountLink accountLink = AccountLink.create(params);
        return accountLink.getUrl();
    }
}
