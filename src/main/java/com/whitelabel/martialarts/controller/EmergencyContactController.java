package com.whitelabel.martialarts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.whitelabel.martialarts.model.EmergencyContact;
import com.whitelabel.martialarts.service.EmergencyContactService;
import com.whitelabel.martialarts.service.StudentService;

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
                com.whitelabel.martialarts.model.Student student = studentService.getStudentById(studentId);
                emergencyContact.setStudent(student);
                model.addAttribute("studentId", studentId);
                model.addAttribute("studentName", student.getFirstName() + " " + student.getLastName());
            } catch (Exception e) {
                // If student not found, continue without pre-selection
            }
        }
        
        model.addAttribute("emergencyContact", emergencyContact);
        model.addAttribute("students", studentService.getAllStudents());
        return "emergency_contacts/add_emergency_contact";
    }

    @PostMapping
    public String createEmergencyContact(@ModelAttribute EmergencyContact emergencyContact) {
        emergencyContactService.save(emergencyContact);
        return "redirect:/students/edit/" + emergencyContact.getStudent().getId();
    }

    @GetMapping("/edit/{id}")
    public String editEmergencyContactForm(@PathVariable Long id, Model model) {
        EmergencyContact emergencyContact = emergencyContactService.findById(id);
        model.addAttribute("emergencyContact", emergencyContact);
        return "emergency_contacts/edit_emergency_contact"; // Thymeleaf template
    }

    @PostMapping("/edit/{id}")
    public String updateEmergencyContact(@PathVariable Long id, @ModelAttribute EmergencyContact emergencyContact) {
        EmergencyContact existingContact = emergencyContactService.findById(id);
        
        // Update fields but keep the same student
        existingContact.setName(emergencyContact.getName());
        existingContact.setRelationship(emergencyContact.getRelationship());
        existingContact.setPhoneNumber(emergencyContact.getPhoneNumber());
        existingContact.setEmail(emergencyContact.getEmail());
        
        emergencyContactService.save(existingContact);
        return "redirect:/students/edit/" + existingContact.getStudent().getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteEmergencyContact(@PathVariable Long id) {
        EmergencyContact contact = emergencyContactService.findById(id);
        Long studentId = contact.getStudent().getId();
        
        emergencyContactService.deleteById(id);
        return "redirect:/students/edit/" + studentId;
    }
}
