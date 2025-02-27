package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.AppUser;
import com.whitelabel.martialarts.service.service.AppUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private AppUserService appUserService;

    /**
     * Handle the root URL and redirect to the appropriate page based on authentication status
     */
    @GetMapping("/")
    public String home() {
        // Check if user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // User is logged in, redirect to dashboard
            return "redirect:/dashboard";
        }
        
        // User is not logged in, redirect to login page
        return "redirect:/login";
    }
    
    /**
     * Redirect old signup URL to the new registration URL
     */
    @GetMapping("/signup")
    public String redirectToRegister() {
        return "redirect:/register";
    }
}
