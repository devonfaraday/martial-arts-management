package com.whitelabel.martialarts.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.whitelabel.martialarts.config.StripeConfig;
import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.model.Subscription;
import com.whitelabel.martialarts.model.SubscriptionInterval;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.service.service.StudentService;
import com.whitelabel.martialarts.service.service.StripeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/students")
public class StudentPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentPaymentController.class);

    @Autowired
    private StudentService studentService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private StripeConfig stripeConfig;

    /**
     * Show the payment setup form
     */
    @GetMapping("/{id}/setup-payment")
    public String showPaymentSetupForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        
        if (student == null) {
            return "redirect:/students?error=Student+not+found";
        }
        
        model.addAttribute("student", student);
        
        return "students/setup_payment";
    }

    /**
     * Process the payment setup by creating a Stripe Checkout session
     */
    @PostMapping("/{id}/process-payment")
    public String processPayment(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam SubscriptionInterval interval,
            RedirectAttributes redirectAttributes) {
        
        Student student = studentService.getStudentById(id);
        
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Student not found");
            return "redirect:/students";
        }
        
        try {
            // Create a Stripe Checkout session for subscription
            logger.info("Creating checkout session for student ID: {}, amount: {}, interval: {}", 
                id, amount, interval);
                
            // Log school information
            if (student.getSchool() == null) {
                logger.error("Student's school is null");
                redirectAttributes.addFlashAttribute("error", "Student's school information is missing");
                return "redirect:/students/edit/" + id;
            }
            
            // We'll still log this warning but allow the payment to proceed
            if (student.getSchool().getStripeConnectAccountId() == null) {
                logger.warn("School does not have a Stripe Connect account set up. Using direct checkout for testing.");
            }
            
            Session checkoutSession = stripeService.createSubscriptionCheckoutSession(
                student, amount, interval);
            
            // Redirect to Stripe Checkout
            logger.info("Redirecting to Stripe Checkout URL: {}", checkoutSession.getUrl());
            return "redirect:" + checkoutSession.getUrl();
                
        } catch (StripeException e) {
            logger.error("Error setting up payment: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                "Failed to set up payment: " + e.getMessage());
            return "redirect:/students/edit/" + id;
        } catch (IllegalStateException e) {
            logger.error("Error setting up payment: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/students/edit/" + id;
        }
    }
    
    /**
     * Handle successful payment setup
     */
    @GetMapping("/payment/success")
    public String handlePaymentSuccess(
            @RequestParam("session_id") String sessionId,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Process the successful checkout session
            Subscription subscription = stripeService.processSuccessfulSubscriptionCheckout(sessionId);
            
            if (subscription != null) {
                redirectAttributes.addFlashAttribute("success", 
                    "Payment setup successful! The student is now enrolled in a " + 
                    subscription.getInterval().getDisplayName().toLowerCase() + 
                    " subscription of $" + subscription.getAmount());
                
                return "redirect:/students/edit/" + subscription.getStudent().getId();
            } else {
                redirectAttributes.addFlashAttribute("error", "Could not process payment");
                return "redirect:/students";
            }
            
        } catch (StripeException e) {
            logger.error("Error processing successful payment: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                "Failed to complete payment setup: " + e.getMessage());
            return "redirect:/students";
        }
    }
    
    /**
     * Handle cancelled payment
     */
    @GetMapping("/payment/cancel")
    public String handlePaymentCancel(
            @RequestParam("session_id") String sessionId,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Get the student ID from the session
            Student student = stripeService.getStudentFromCheckoutSession(sessionId);
            
            if (student != null) {
                redirectAttributes.addFlashAttribute("warning", "Payment setup was cancelled");
                return "redirect:/students/edit/" + student.getId();
            } else {
                redirectAttributes.addFlashAttribute("warning", "Payment setup was cancelled");
                return "redirect:/students";
            }
            
        } catch (StripeException e) {
            logger.error("Error processing cancelled payment: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("warning", "Payment setup was cancelled");
            return "redirect:/students";
        }
    }
    
    /**
     * Test endpoint to check Stripe Connect account setup
     */
    @GetMapping("/{id}/check-stripe-setup")
    @ResponseBody
    public Map<String, Object> checkStripeSetup(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Student student = studentService.getStudentById(id);
            
            if (student == null) {
                result.put("error", "Student not found");
                return result;
            }
            
            result.put("student_id", student.getId());
            result.put("student_name", student.getFirstName() + " " + student.getLastName());
            
            School school = student.getSchool();
            if (school == null) {
                result.put("school", "null");
                return result;
            }
            
            result.put("school_id", school.getId());
            result.put("school_name", school.getName());
            result.put("stripe_connect_account_id", school.getStripeConnectAccountId());
            result.put("stripe_connect_enabled", school.isStripeConnectEnabled());
            
            return result;
        } catch (Exception e) {
            result.put("error", e.getMessage());
            return result;
        }
    }
}
