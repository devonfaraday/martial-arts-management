package com.whitelabel.martialarts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.whitelabel.martialarts.model.AppUser;
import com.whitelabel.martialarts.model.Organization;
import com.whitelabel.martialarts.model.RegistrationForm;

import org.springframework.ui.Model;
import java.security.Principal;

@Controller
public class WelcomeController {

    @GetMapping("/welcome")
    public String welcomePage(Principal principal) {
        // If already logged in, forward to dashboard
        if (principal != null) {
            return "redirect:/dashboard";
        }
        // Otherwise, show your public welcome page; you could rename the view from "home" to "welcome"
        return "welcome";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
