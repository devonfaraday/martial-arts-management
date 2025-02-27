package com.whitelabel.martialarts.controller;

import com.stripe.exception.StripeException;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.repository.SchoolRepository;
import com.whitelabel.martialarts.service.service.StripeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/schools/connect")
public class SchoolConnectController {

    @Autowired
    private StripeService stripeService;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Value("${stripe.connect.client.id}")
    private String connectClientId;
    
    @Value("${stripe.connect.oauth.url}")
    private String connectOAuthUrl;
    
    /**
     * Displays the Stripe Connect onboarding page for a school
     */
    @GetMapping("/{schoolId}/onboard")
    public String showOnboardingPage(@PathVariable Long schoolId, Model model) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("School not found with ID: " + schoolId));
        
        model.addAttribute("school", school);
        model.addAttribute("clientId", connectClientId);
        model.addAttribute("oauthUrl", connectOAuthUrl);
        
        return "schools/connect/onboard";
    }
    
    /**
     * Initiates the Stripe Connect Standard onboarding process
     */
    @PostMapping("/{schoolId}/create-account")
    public String createConnectAccount(@PathVariable Long schoolId, RedirectAttributes redirectAttributes) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new IllegalArgumentException("School not found with ID: " + schoolId));
            
            // Create a new Stripe Connect account
            String accountId = stripeService.createConnectAccount(school);
            
            // Generate an account link for onboarding
            String returnUrl = "/schools/connect/" + schoolId + "/return";
            String accountLinkUrl = stripeService.createConnectAccountLink(school, returnUrl);
            
            // Redirect to the Stripe onboarding flow
            return "redirect:" + accountLinkUrl;
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create Stripe Connect account: " + e.getMessage());
            return "redirect:/schools/connect/" + schoolId + "/onboard";
        }
    }
    
    /**
     * Handles the OAuth redirect from Stripe
     */
    @GetMapping("/oauth/callback")
    public String handleOAuthCallback(@RequestParam("code") String code, 
                                      @RequestParam("state") String schoolId,
                                      RedirectAttributes redirectAttributes) {
        try {
            School school = stripeService.handleConnectOAuthCallback(code, Long.parseLong(schoolId));
            redirectAttributes.addFlashAttribute("success", "Successfully connected Stripe account for " + school.getName());
            return "redirect:/schools/" + schoolId;
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to connect Stripe account: " + e.getMessage());
            return "redirect:/schools/connect/" + schoolId + "/onboard";
        }
    }
    
    /**
     * Return endpoint after account link flow
     */
    @GetMapping("/{schoolId}/return")
    public String handleAccountLinkReturn(@PathVariable Long schoolId, RedirectAttributes redirectAttributes) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("School not found with ID: " + schoolId));
        
        try {
            boolean isEnabled = stripeService.isConnectAccountEnabled(school);
            if (isEnabled) {
                school.setStripeConnectEnabled(true);
                schoolRepository.save(school);
                redirectAttributes.addFlashAttribute("success", "Your Stripe account is now ready to accept payments!");
            } else {
                redirectAttributes.addFlashAttribute("warning", "Your Stripe account setup is not complete. Please finish the onboarding process.");
            }
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to verify Stripe account status: " + e.getMessage());
        }
        
        return "redirect:/schools/" + schoolId;
    }
    
    /**
     * REST endpoint to check if a school's Stripe Connect account is enabled
     */
    @GetMapping("/api/{schoolId}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getConnectAccountStatus(@PathVariable Long schoolId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new IllegalArgumentException("School not found with ID: " + schoolId));
            
            boolean isEnabled = stripeService.isConnectAccountEnabled(school);
            
            response.put("schoolId", schoolId);
            response.put("connected", school.getStripeConnectAccountId() != null);
            response.put("enabled", isEnabled);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
