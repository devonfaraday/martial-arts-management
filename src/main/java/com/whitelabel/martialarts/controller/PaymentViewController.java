package com.whitelabel.martialarts.controller;

import com.stripe.exception.StripeException;
import com.whitelabel.martialarts.model.Payment;
import com.whitelabel.martialarts.service.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/payment")
public class PaymentViewController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/checkout")
    public String showCheckoutPage() {
        return "payment/checkout";
    }

    @GetMapping("/success")
    public String showSuccessPage(@RequestParam("session_id") String sessionId, Model model) {
        try {
            Payment payment = paymentService.processStripePayment(sessionId);
            model.addAttribute("paymentId", payment.getId());
            return "payment/success";
        } catch (StripeException e) {
            // Log the error
            e.printStackTrace();
            return "redirect:/payment/error";
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
