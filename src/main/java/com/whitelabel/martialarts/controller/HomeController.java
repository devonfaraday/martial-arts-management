package com.whitelabel.martialarts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.whitelabel.martialarts.model.AppUser;
import com.whitelabel.martialarts.model.Organization;
import com.whitelabel.martialarts.model.RegistrationForm;

import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

   @GetMapping("/signup")
    public String signup(Model model) {
        // Add a RegistrationForm object to the model
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setOrganization(new Organization());
        registrationForm.setAppUser(new AppUser());
        model.addAttribute("registrationForm", registrationForm);

        return "signup";
    }
}
