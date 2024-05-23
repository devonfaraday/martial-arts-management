package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.Staff;
import com.whitelabel.martialarts.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping
    public String getAllStaff(Model model) {
        List<Staff> staffList = staffService.getAllStaff();
        model.addAttribute("staff", staffList);
        return "staff/staff"; // Name of your Thymeleaf template (staff.html)
    }

    @GetMapping("/{id}")
    public String getStaffById(@PathVariable Long id, Model model) {
        Staff staff = staffService.getStaffById(id);
        model.addAttribute("staff", staff);
        return "staff/staff_detail"; // Name of your Thymeleaf template for individual staff details
    }

    @GetMapping("/add")
    public String addStaffForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "staff/add_staff"; // Name of your Thymeleaf template for adding staff
    }

    @PostMapping
    public String createStaff(@ModelAttribute Staff staff) {
        staffService.createStaff(staff);
        return "redirect:/staff";
    }

    @GetMapping("/edit/{id}")
    public String editStaffForm(@PathVariable Long id, Model model) {
        Staff staff = staffService.getStaffById(id);
        model.addAttribute("staff", staff);
        return "staff/edit_staff"; // Name of your Thymeleaf template for editing staff
    }

    @PostMapping("/edit/{id}")
    public String updateStaff(@PathVariable Long id, @ModelAttribute Staff staff) {
        staffService.updateStaff(id, staff);
        return "redirect:/staff";
    }

    @GetMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return "redirect:/staff";
    }
}
