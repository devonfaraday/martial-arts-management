package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.AppUser;
import com.whitelabel.martialarts.model.Organization;
import com.whitelabel.martialarts.model.RegistrationForm;
import com.whitelabel.martialarts.service.service.AppUserService;
import com.whitelabel.martialarts.service.service.OrganizationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AppUserService appUserService;

    @GetMapping("/view")
    public String getCurrentUserOrganization(Model model) {
        AppUser currentUser = appUserService.getCurrentUser(); // Fetch the current user
        Long organizationId = currentUser.getOrganizationId(); // Get the organization ID
        return "redirect:/organization/view/" + organizationId; // Redirect to the specific organization view
    }

    @GetMapping("/view/{id}")
    public String getOrganization(@PathVariable Long id, Model model) {
        Organization organization = organizationService.getOrganization(id);
        model.addAttribute("organization", organization);
        return "organization/organization"; // Path to your Thymeleaf template
    }

    // @GetMapping("/organization/view/{id}")
    // public String viewOrganization(@PathVariable Long id, Model model) {
    //     Organization organization = organizationService.getOrganization(id);
    //     model.addAttribute("organization", organization);
    //     return "organization/organization";
    // }

    @GetMapping("/edit/{id}")
    public String editOrganization(@PathVariable Long id, Model model) {
        Organization organization = organizationService.getOrganization(id);
        model.addAttribute("organization", organization);
        return "organization/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateOrganization(@PathVariable Long id, @ModelAttribute Organization organization) {
        organization.setId(id);
        organizationService.updateOrganization(organization);
        return "redirect:/organization/" + id;
    }

    @GetMapping("/create")
    public String createOrganizationForm(Model model) {
        model.addAttribute("organization", new Organization());
        return "organization/create";
    }

    @PostMapping("/create")
    public String createOrganization(@ModelAttribute Organization organization) {
        Organization createdOrganization = organizationService.createOrganization(organization);
        return "redirect:/organization/" + createdOrganization.getId();
    }

    @DeleteMapping("/delete/{id}")
    public String deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return "redirect:/organization";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute RegistrationForm registrationForm) {
        Organization organization = registrationForm.getOrganization();
        AppUser appUser = registrationForm.getAppUser();

        Organization createdOrganization = organizationService.createOrganization(organization);
        appUser.setOrganization(createdOrganization);
        appUser.setRole("ADMIN");
        appUserService.createAppUser(appUser);
        return "redirect:/login";
    }
}
