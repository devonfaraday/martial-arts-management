package com.whitelabel.martialarts.controller;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.whitelabel.martialarts.model.StudentStatus;
import com.whitelabel.martialarts.repository.StudentRepository;

@Controller
public class DashboardController {

    private final StudentRepository studentRepository;

    public DashboardController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Use an EnumMap for clarity and efficiency
        Map<StudentStatus, Long> studentStats = new EnumMap<>(StudentStatus.class);
        for (StudentStatus status : StudentStatus.values()) {
            long count = studentRepository.countByStatus(status);
            studentStats.put(status, count);
        }
        model.addAttribute("studentStats", studentStats);
        return "dashboard";
    }
}
