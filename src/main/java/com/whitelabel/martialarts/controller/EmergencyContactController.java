package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.EmergencyContact;
import com.whitelabel.martialarts.service.service.EmergencyContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/emergency-contacts")
public class EmergencyContactController {

    @Autowired
    private EmergencyContactService emergencyContactService;

    @GetMapping
    public String listEmergencyContacts(Model model) {
        List<EmergencyContact> emergencyContacts = emergencyContactService.getAllEmergencyContacts();
        model.addAttribute("emergencyContacts", emergencyContacts);
        return "emergency_contacts/list_emergency_contacts"; // Thymeleaf template
    }

    @GetMapping("/{id}")
    public String getEmergencyContactById(@PathVariable Long id, Model model) {
        EmergencyContact emergencyContact = emergencyContactService.getEmergencyContactById(id);
        model.addAttribute("emergencyContact", emergencyContact);
        return "emergency_contacts/view_emergency_contact"; // Thymeleaf template
    }

    @GetMapping("/add")
    public String addEmergencyContactForm(Model model) {
        model.addAttribute("emergencyContact", new EmergencyContact());
        return "emergency_contacts/add_emergency_contact"; // Thymeleaf template
    }

    @PostMapping
    public String createEmergencyContact(@ModelAttribute EmergencyContact emergencyContact) {
        emergencyContactService.createEmergencyContact(emergencyContact);
        return "redirect:/emergency-contacts";
    }

    @GetMapping("/edit/{id}")
    public String editEmergencyContactForm(@PathVariable Long id, Model model) {
        EmergencyContact emergencyContact = emergencyContactService.getEmergencyContactById(id);
        model.addAttribute("emergencyContact", emergencyContact);
        return "emergency_contacts/edit_emergency_contact"; // Thymeleaf template
    }

    @PostMapping("/edit/{id}")
    public String updateEmergencyContact(@PathVariable Long id, @ModelAttribute EmergencyContact emergencyContact) {
        emergencyContactService.updateEmergencyContact(id, emergencyContact);
        return "redirect:/emergency-contacts";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmergencyContact(@PathVariable Long id) {
        emergencyContactService.deleteEmergencyContact(id);
        return "redirect:/emergency-contacts";
    }
}
