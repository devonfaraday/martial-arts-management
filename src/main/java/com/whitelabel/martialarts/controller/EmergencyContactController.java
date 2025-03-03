package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.EmergencyContact;
import com.whitelabel.martialarts.service.EmergencyContactService;
import com.whitelabel.martialarts.service.StudentService;
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

    @Autowired
    private StudentService studentService;

    @GetMapping
    public String listEmergencyContacts(Model model) {
        List<EmergencyContact> emergencyContacts = emergencyContactService.findAll();
        model.addAttribute("emergencyContacts", emergencyContacts);
        return "emergency_contacts/list_emergency_contacts"; // Thymeleaf template
    }

    @GetMapping("/{id}")
    public String getEmergencyContactById(@PathVariable Long id, Model model) {
        EmergencyContact emergencyContact = emergencyContactService.findById(id);
        model.addAttribute("emergencyContact", emergencyContact);
        return "emergency_contacts/view_emergency_contact"; // Thymeleaf template
    }

    @GetMapping("/add")
    public String addEmergencyContactForm(@RequestParam(required = false) Long studentId, Model model) {
        EmergencyContact emergencyContact = new EmergencyContact();
        
        // If studentId is provided, pre-select the student
        if (studentId != null) {
            try {
                emergencyContact.setStudent(studentService.getStudentById(studentId));
            } catch (Exception e) {
                // If student not found, continue without pre-selection
            }
        }
        
        model.addAttribute("emergencyContact", emergencyContact);
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("preSelectedStudentId", studentId);
        return "emergency_contacts/add_emergency_contact";
    }

    @PostMapping
    public String createEmergencyContact(@ModelAttribute EmergencyContact emergencyContact) {
        emergencyContactService.save(emergencyContact);
        return "redirect:/emergency-contacts";
    }

    @GetMapping("/edit/{id}")
    public String editEmergencyContactForm(@PathVariable Long id, Model model) {
        EmergencyContact emergencyContact = emergencyContactService.findById(id);
        model.addAttribute("emergencyContact", emergencyContact);
        return "emergency_contacts/edit_emergency_contact"; // Thymeleaf template
    }

    @PostMapping("/edit/{id}")
    public String updateEmergencyContact(@PathVariable Long id, @ModelAttribute EmergencyContact emergencyContact) {
        emergencyContact.setId(id);
        emergencyContactService.save(emergencyContact);
        return "redirect:/emergency-contacts";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmergencyContact(@PathVariable Long id) {
        emergencyContactService.deleteById(id);
        return "redirect:/emergency-contacts";
    }
}
