package com.whitelabel.martialarts.controller;

import java.security.Principal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.whitelabel.martialarts.model.AppUser;
import com.whitelabel.martialarts.model.Organization;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.model.StudentStatus;
import com.whitelabel.martialarts.repository.AppUserRepository;
import com.whitelabel.martialarts.repository.SchoolRepository;
import com.whitelabel.martialarts.repository.StudentRepository;

@Controller
public class DashboardController {

    private final StudentRepository studentRepository;
    private final AppUserRepository appUserRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    public DashboardController(StudentRepository studentRepository, AppUserRepository appUserRepository) {
        this.studentRepository = studentRepository;
        this.appUserRepository = appUserRepository;
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
        School school = null;
        if (principal != null) {
            // Get the user from the principal
            String username = principal.getName();
            // Find the user in the database
            Optional<AppUser> userOpt = appUserRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                AppUser user = userOpt.get();
                // Get the organization associated with the user
                Organization org = user.getOrganization();
                if (org != null) {
                    // Get the first school associated with the organization
                    List<School> schools = schoolRepository.findByOrganization(org);
                    if (!schools.isEmpty()) {
                        school = schools.get(0);
                    }
                }
            }
        }
        
        // If we couldn't find a school, fall back to the first school in the database
        if (school == null) {
            school = schoolRepository.findAll().stream().findFirst().orElse(new School()); // Create empty school if none found
        }
        
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
