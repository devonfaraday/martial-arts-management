package com.whitelabel.martialarts.controller;

import com.stripe.exception.StripeException;
import com.whitelabel.martialarts.model.Payment;
import com.whitelabel.martialarts.service.service.PaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/payment")
public class PaymentViewController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentViewController.class);

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/checkout")
    public String showCheckoutPage() {
        return "payment/checkout";
    }

    @GetMapping("/success")
    public String showSuccessPage(@RequestParam("session_id") String sessionId, Model model) {
        try {
            // Check if the payment was already processed (to avoid duplicate processing)
            Payment payment = paymentService.processStripePayment(sessionId);
            model.addAttribute("paymentId", payment.getId());
            model.addAttribute("amount", payment.getAmount());
            model.addAttribute("description", payment.getDescription());
            return "payment/success";
        } catch (StripeException e) {
            // Log the error
            logger.error("Error processing Stripe payment: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "There was an issue retrieving your payment information.");
            model.addAttribute("errorDetails", "Your payment may have been processed successfully, but we couldn't retrieve the details. Please check your account or contact support.");
            model.addAttribute("sessionId", sessionId);
            return "payment/error";
        } catch (Exception e) {
            // Log any other unexpected errors
            logger.error("Unexpected error processing payment: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "An unexpected error occurred while processing your payment.");
            return "payment/error";
        }
    }

    @GetMapping("/cancel")
    public String showCancelPage() {
        return "payment/cancel";
    }

    @GetMapping("/error")
    public String showErrorPage() {
        return "payment/error";
    }
}
