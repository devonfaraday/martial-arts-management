package com.whitelabel.martialarts.controller;

import com.stripe.exception.StripeException;
import com.whitelabel.martialarts.model.AppUser;
import com.whitelabel.martialarts.model.Organization;
import com.whitelabel.martialarts.model.RegistrationForm;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.repository.OrganizationRepository;
import com.whitelabel.martialarts.repository.SchoolRepository;
import com.whitelabel.martialarts.service.service.AppUserService;
import com.whitelabel.martialarts.service.service.StripeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class SchoolRegistrationController {

    @Autowired
    private AppUserService appUserService;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private StripeService stripeService;
    
    @Value("${app.base.url}")
    private String baseUrl;
    
    /**
     * Display the first step of school registration
     */
    @GetMapping
    public String showRegistrationForm(Model model) {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setOrganization(new Organization());
        registrationForm.setAppUser(new AppUser());
        model.addAttribute("registrationForm", registrationForm);
        
        return "signup";
    }
    
    /**
     * Process the first step of registration and proceed to Stripe Connect setup
     */
    @PostMapping
    public String processRegistration(@ModelAttribute("registrationForm") RegistrationForm form,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "signup";
        }
        
        // Create organization and admin user
        Organization organization = form.getOrganization();
        organization = organizationRepository.save(organization);
        
        AppUser adminUser = form.getAppUser();
        adminUser.setOrganization(organization);
        adminUser.setRole("ADMIN");
        appUserService.createAppUser(adminUser);
        
        // Create a school for the organization
        School school = new School();
        school.setName(organization.getName());
        school.setEmail(adminUser.getEmail());
        school.setOrganization(organization);
        school = schoolRepository.save(school);
        
        // Automatically create a Stripe Connect account for the school
        try {
            String accountId = stripeService.createConnectAccount(school);
            school.setStripeConnectEnabled(true);
            schoolRepository.save(school);
            
            // Add success message
            redirectAttributes.addFlashAttribute("success", 
                    "Registration complete! Your school is now set up with Stripe Connect for payment processing.");
            
            return "redirect:/login?registered=true";
        } catch (StripeException e) {
            // If Stripe Connect setup fails, continue without it but show a warning
            redirectAttributes.addFlashAttribute("warning", 
                    "Basic registration complete, but Stripe Connect setup failed: " + e.getMessage() + 
                    " You can set up payment processing later in school settings.");
            
            return "redirect:/login?registered=true";
        }
    }
    
    /**
     * Display the Stripe Connect setup page
     */
    @GetMapping("/{schoolId}/payment-setup")
    public String showPaymentSetup(@PathVariable Long schoolId, Model model) {
        School school = schoolRepository.findById(schoolId).orElse(null);
        if (school == null) {
            return "redirect:/register";
        }
        
        model.addAttribute("school", school);
        return "register/payment-setup";
    }
    
    /**
     * Initiate the Stripe Connect onboarding process
     */
    @PostMapping("/{schoolId}/connect-stripe")
    public String connectStripe(@PathVariable Long schoolId, RedirectAttributes redirectAttributes) {
        School school = schoolRepository.findById(schoolId).orElse(null);
        if (school == null) {
            redirectAttributes.addFlashAttribute("error", "School not found");
            return "redirect:/register";
        }
        
        try {
            String accountId = stripeService.createConnectAccount(school);
            String url = stripeService.createConnectAccountLink(school, baseUrl + "/register/" + schoolId + "/stripe-return");
            
            return "redirect:" + url;
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to connect with Stripe: " + e.getMessage());
            return "redirect:/register/" + schoolId + "/payment-setup";
        }
    }
    
    /**
     * Handle the return from Stripe Connect onboarding
     */
    @GetMapping("/{schoolId}/stripe-return")
    public String handleStripeReturn(@PathVariable Long schoolId, RedirectAttributes redirectAttributes) {
        School school = schoolRepository.findById(schoolId).orElse(null);
        if (school == null) {
            redirectAttributes.addFlashAttribute("error", "School not found");
            return "redirect:/register";
        }
        
        school.setStripeConnectEnabled(true);
        schoolRepository.save(school);
        
        redirectAttributes.addFlashAttribute("success", "Your Stripe Connect account has been set up successfully!");
        return "redirect:/register/" + schoolId + "/complete";
    }
    
    /**
     * Display the registration completion page
     */
    @GetMapping("/{schoolId}/complete")
    public String showCompletionPage(@PathVariable Long schoolId, Model model) {
        School school = schoolRepository.findById(schoolId).orElse(null);
        if (school == null) {
            return "redirect:/register";
        }
        
        model.addAttribute("school", school);
        return "register/complete";
    }
    
    /**
     * Handle the simplified registration form and redirect to login page
     */
    @PostMapping("/simplified")
    public String simplifiedRegistration(@ModelAttribute("registrationForm") RegistrationForm form,
                                         BindingResult result,
                                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "signup";
        }
        
        // Create organization and admin user
        Organization organization = form.getOrganization();
        organization = organizationRepository.save(organization);
        
        AppUser adminUser = form.getAppUser();
        adminUser.setOrganization(organization);
        adminUser.setRole("ADMIN");
        appUserService.createAppUser(adminUser);
        
        // Create a school for the organization
        School school = new School();
        school.setName(organization.getName());
        school.setEmail(adminUser.getEmail());
        school.setOrganization(organization);
        school = schoolRepository.save(school);
        
        redirectAttributes.addFlashAttribute("success", "Registration complete! Please login to continue.");
        return "redirect:/login";
    }
}
