package com.whitelabel.martialarts.controller;

import java.security.Principal;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.model.StudentStatus;
import com.whitelabel.martialarts.repository.SchoolRepository;
import com.whitelabel.martialarts.repository.StudentRepository;

@Controller
public class DashboardController {

    private final StudentRepository studentRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    public DashboardController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        // Use an EnumMap for clarity and efficiency
        Map<StudentStatus, Long> studentStats = new EnumMap<>(StudentStatus.class);
        for (StudentStatus status : StudentStatus.values()) {
            long count = studentRepository.countByStatus(status);
            studentStats.put(status, count);
        }
        model.addAttribute("studentStats", studentStats);
        
        // Get the school for the current user
        // For demo purposes, we're using the first school in the database
        // In a real application, you would get the school associated with the logged-in user
        School school = schoolRepository.findAll().stream().findFirst().orElse(null);
        model.addAttribute("school", school);
        
        // Add student count
        long studentCount = studentRepository.count();
        model.addAttribute("studentCount", studentCount);
        
        // In a real application, you would calculate these from actual data
        model.addAttribute("revenueMonth", 0.0);
        model.addAttribute("classCount", 0);
        model.addAttribute("pendingPayments", 0);
        
        return "dashboard";
    }
}
