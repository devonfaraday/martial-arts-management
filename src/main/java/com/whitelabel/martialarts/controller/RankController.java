package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.Rank;
import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.service.service.RankService;
import com.whitelabel.martialarts.service.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Controller
public class RankController {

    @Autowired
    private RankService rankService;
    
    @Autowired
    private StudentService studentService;
    
    // REST API endpoints
    @RestController
    @RequestMapping("/api/ranks")
    public class RankRestController {
        
        @GetMapping
        public List<Rank> getAllRanks() {
            return rankService.getAllRanks();
        }

        @GetMapping("/{id}")
        public ResponseEntity<Rank> getRankById(@PathVariable Long id) {
            Rank rank = rankService.getRankById(id);
            return ResponseEntity.ok(rank);
        }

        @PostMapping
        public Rank createRank(@RequestBody Rank rank) {
            return rankService.createRank(rank);
        }

        @PutMapping("/{id}")
        public ResponseEntity<Rank> updateRank(@PathVariable Long id, @RequestBody Rank rank) {
            Rank updatedRank = rankService.updateRank(id, rank);
            return ResponseEntity.ok(updatedRank);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteRank(@PathVariable Long id) {
            rankService.deleteRank(id);
            return ResponseEntity.noContent().build();
        }
    }
    
    // Web UI endpoints
    @GetMapping("/ranks")
    public String listRanks(Model model) {
        model.addAttribute("ranks", rankService.getAllRanks());
        model.addAttribute("newRank", new Rank());
        return "ranks/list";
    }
    
    @GetMapping("/ranks/new")
    public String newRankForm(Model model) {
        model.addAttribute("rank", new Rank());
        return "ranks/edit";
    }
    
    @PostMapping("/ranks/save")
    public String saveRank(@ModelAttribute Rank rank, RedirectAttributes redirectAttributes) {
        // Set default values if not provided
        if (rank.getMaxStripes() == null) {
            rank.setMaxStripes(0);
        }
        if (rank.getDisplayOrder() == null) {
            rank.setDisplayOrder(999); // Default to end of list
        }
        
        rankService.createRank(rank);
        redirectAttributes.addFlashAttribute("success", "Rank saved successfully");
        return "redirect:/ranks";
    }
    
    @GetMapping("/ranks/edit/{id}")
    public String editRank(@PathVariable Long id, Model model) {
        Rank rank = rankService.getRankById(id);
        model.addAttribute("rank", rank);
        return "ranks/edit";
    }
    
    @PostMapping("/ranks/update/{id}")
    public String updateRank(@PathVariable Long id, @ModelAttribute Rank rank, RedirectAttributes redirectAttributes) {
        rankService.updateRank(id, rank);
        redirectAttributes.addFlashAttribute("success", "Rank updated successfully");
        return "redirect:/ranks";
    }
    
    @GetMapping("/ranks/delete/{id}")
    public String deleteRank(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            rankService.deleteRank(id);
            redirectAttributes.addFlashAttribute("success", "Rank deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete rank that is assigned to students");
        }
        return "redirect:/ranks";
    }
    
    // Student rank promotion
    @PostMapping("/students/{studentId}/promote")
    public String promoteStudent(
            @PathVariable Long studentId, 
            @RequestParam Long rankId,
            @RequestParam(required = false) Integer stripes,
            RedirectAttributes redirectAttributes) {
        
        Student student = studentService.getStudentById(studentId);
        Rank newRank = rankService.getRankById(rankId);
        
        student.setRank(newRank);
        student.setCurrentStripes(stripes != null ? stripes : 0);
        student.setLastPromotionDate(Timestamp.from(Instant.now()));
        
        studentService.updateStudent(studentId, student);
        redirectAttributes.addFlashAttribute("success", "Student rank updated successfully");
        
        return "redirect:/students/edit/" + studentId;
    }
}
