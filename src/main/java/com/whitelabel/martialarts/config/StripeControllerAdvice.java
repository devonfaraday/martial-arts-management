package com.whitelabel.martialarts.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

/**
 * Controller advice to add Stripe-related attributes to all models
 */
@ControllerAdvice
public class StripeControllerAdvice {
    
    @Autowired
    private StripeConfig stripeConfig;
    
    /**
     * Add Stripe publishable key to all models
     */
    @ModelAttribute
    public void addStripeAttributes(Model model) {
        model.addAttribute("stripePublishableKey", stripeConfig.getPublishableKey());
    }
}
