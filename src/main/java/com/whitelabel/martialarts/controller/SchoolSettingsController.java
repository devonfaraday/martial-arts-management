package com.whitelabel.martialarts.controller;

import com.stripe.exception.StripeException;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.repository.SchoolRepository;
import com.whitelabel.martialarts.service.service.StripeService;
import com.whitelabel.martialarts.config.StripeConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/school")
public class SchoolSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SchoolSettingsController.class);

    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private StripeService stripeService;
    
    @Autowired
    private StripeConfig stripeConfig;
    
    @Value("${app.base.url}")
    private String baseUrl;
    
    /**
     * Display the school settings page
     */
    @GetMapping("/settings")
    public String showSettings(Model model, Principal principal) {
        School school = getSchoolForCurrentUser(principal);
        
        if (school == null) {
            return "redirect:/dashboard?error=School+not+found";
        }
        
        model.addAttribute("school", school);
        
        // Handle potential Stripe exceptions
        boolean stripeConnectEnabled = false;
        try {
            stripeConnectEnabled = stripeService.isConnectAccountEnabled(school);
        } catch (StripeException e) {
            // Log the error
            logger.error("Error checking Stripe Connect status: " + e.getMessage());
            // Add error message to the model
            model.addAttribute("stripeError", "Unable to verify Stripe Connect status: " + e.getMessage());
        }
        
        model.addAttribute("stripeConnectEnabled", stripeConnectEnabled);
        model.addAttribute("stripePublishableKey", stripeConfig.getPublishableKey());
        return "school/settings_with_error";
    }
    
    /**
     * Update school information
     */
    @PostMapping("/update")
    public String updateSchool(@ModelAttribute School updatedSchool, RedirectAttributes redirectAttributes) {
        School school = schoolRepository.findById(updatedSchool.getId())
                .orElseThrow(() -> new IllegalArgumentException("School not found with ID: " + updatedSchool.getId()));
        
        // Update school properties
        school.setName(updatedSchool.getName());
        school.setAddress(updatedSchool.getAddress());
        school.setEmail(updatedSchool.getEmail());
        school.setPhone(updatedSchool.getPhone());
        school.setWebsite(updatedSchool.getWebsite());
        
        schoolRepository.save(school);
        
        redirectAttributes.addFlashAttribute("success", "School information updated successfully");
        return "redirect:/school/settings";
    }
    
    /**
     * Initiate the Stripe Connect onboarding process
     */
    @PostMapping("/connect-stripe")
    public String connectStripe(Principal principal, RedirectAttributes redirectAttributes) {
        try {
            School school = getSchoolForCurrentUser(principal);
            
            if (school == null) {
                redirectAttributes.addFlashAttribute("error", "School not found");
                return "redirect:/school/settings";
            }
            
            // Create a Stripe Connect account for the school if it doesn't exist
            if (school.getStripeConnectAccountId() == null || school.getStripeConnectAccountId().isEmpty()) {
                String accountId = stripeService.createConnectAccount(school);
            }
            
            // Generate the onboarding URL
            String returnUrl = baseUrl + "/school/stripe-return";
            String accountLinkUrl = stripeService.createConnectAccountLink(school, returnUrl);
            
            // Redirect to Stripe's hosted onboarding
            return "redirect:" + accountLinkUrl;
            
        } catch (StripeException e) {
            logger.error("Failed to set up Stripe Connect: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                    "Failed to set up Stripe Connect: " + e.getMessage());
            return "redirect:/school/settings";
        }
    }
    
    /**
     * Handle the return from Stripe Connect onboarding
     */
    @GetMapping("/stripe-return")
    public String handleStripeReturn(Principal principal, RedirectAttributes redirectAttributes) {
        School school = getSchoolForCurrentUser(principal);
        
        if (school == null) {
            redirectAttributes.addFlashAttribute("error", "School not found");
            return "redirect:/dashboard";
        }
        
        try {
            boolean isEnabled = stripeService.isConnectAccountEnabled(school);
            
            if (isEnabled) {
                school.setStripeConnectEnabled(true);
                schoolRepository.save(school);
                redirectAttributes.addFlashAttribute("success", 
                        "Your Stripe account is now set up and ready to accept payments!");
            } else {
                redirectAttributes.addFlashAttribute("warning", 
                        "Your Stripe account setup is not complete. Please finish the onboarding process.");
            }
        } catch (StripeException e) {
            logger.error("Failed to verify Stripe account status: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                    "Failed to verify Stripe account status: " + e.getMessage());
        }
        
        return "redirect:/school/settings";
    }
    
    /**
     * Refresh the Stripe Connect account status
     */
    @PostMapping("/refresh-stripe-account")
    public String refreshStripeAccount(Principal principal, RedirectAttributes redirectAttributes, 
                                  @RequestHeader(value = "Referer", required = false) String referer) {
        School school = getSchoolForCurrentUser(principal);
        
        if (school == null) {
            redirectAttributes.addFlashAttribute("error", "School not found");
            return "redirect:/dashboard";
        }
        
        try {
            boolean isEnabled = stripeService.isConnectAccountEnabled(school);
            
            if (isEnabled) {
                school.setStripeConnectEnabled(true);
                schoolRepository.save(school);
                redirectAttributes.addFlashAttribute("success", "Stripe account status refreshed successfully");
            } else {
                school.setStripeConnectEnabled(false);
                schoolRepository.save(school);
                redirectAttributes.addFlashAttribute("warning", 
                        "Your Stripe account is not fully set up. Please complete the onboarding process.");
            }
        } catch (Exception e) {
            logger.error("Failed to refresh Stripe account status: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                    "Failed to refresh Stripe account status: " + e.getMessage());
        }
        
        // Redirect back to the referring page if available, otherwise go to settings
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/school/settings";
    }
    
    /**
     * Import an existing Stripe Connect account
     */
    @PostMapping("/import-stripe-account")
    public String importStripeAccount(@RequestParam String accountId, 
                                     Principal principal, 
                                     RedirectAttributes redirectAttributes) {
        School school = getSchoolForCurrentUser(principal);
        
        if (school == null) {
            redirectAttributes.addFlashAttribute("error", "School not found");
            return "redirect:/dashboard";
        }
        
        try {
            stripeService.importConnectAccount(school, accountId);
            redirectAttributes.addFlashAttribute("success", 
                    "Stripe account imported successfully. Your account is now connected.");
        } catch (StripeException e) {
            logger.error("Failed to import Stripe account: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                    "Failed to import Stripe account: " + e.getMessage());
        }
        
        return "redirect:/school/settings";
    }
    
    /**
     * Save payment settings
     */
    @PostMapping("/payment-settings")
    public String savePaymentSettings(@RequestParam String defaultCurrency, 
                                     @RequestParam(required = false) boolean automaticReceipts,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        // In a real application, you would save these settings to the database
        redirectAttributes.addFlashAttribute("success", "Payment settings saved successfully");
        return "redirect:/school/settings";
    }
    
    /**
     * Save notification settings
     */
    @PostMapping("/notification-settings")
    public String saveNotificationSettings(@RequestParam(required = false) boolean emailNotifications,
                                          @RequestParam(required = false) boolean paymentNotifications,
                                          @RequestParam(required = false) boolean studentNotifications,
                                          @RequestParam(required = false) boolean marketingNotifications,
                                          Principal principal,
                                          RedirectAttributes redirectAttributes) {
        // In a real application, you would save these settings to the database
        redirectAttributes.addFlashAttribute("success", "Notification preferences saved successfully");
        return "redirect:/school/settings";
    }
    
    /**
     * Redirect to the streamlined Stripe onboarding page
     */
    @GetMapping("/streamlined-onboarding")
    public String redirectToStreamlinedOnboarding(Principal principal) {
        School school = getSchoolForCurrentUser(principal);
        
        if (school == null) {
            return "redirect:/dashboard";
        }
        
        return "redirect:/schools/connect/" + school.getId() + "/embedded-onboard";
    }
    
    /**
     * Helper method to get the school for the current user
     */
    private School getSchoolForCurrentUser(Principal principal) {
        // For demo purposes, we're using the first school in the database
        // In a real application, you would get the school associated with the logged-in user
        return schoolRepository.findAll().stream().findFirst().orElse(null);
    }
}
