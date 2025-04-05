package com.whitelabel.martialarts.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.whitelabel.martialarts.model.Address;
import com.whitelabel.martialarts.model.EmergencyContact;
import com.whitelabel.martialarts.model.Note;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.model.StudentStatus;
import com.whitelabel.martialarts.repository.SchoolRepository;
import com.whitelabel.martialarts.service.EmergencyContactService;
import com.whitelabel.martialarts.service.service.NoteService;
import com.whitelabel.martialarts.service.service.RankService;
import com.whitelabel.martialarts.service.service.StudentService;
import com.whitelabel.martialarts.model.Rank;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private NoteService noteService;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private EmergencyContactService emergencyContactService;
    
    @Autowired
    private RankService rankService;

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    @GetMapping
    public String getAllStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "students/students"; // Note the subfolder reference
    }

    @GetMapping("/{id}")
    public String getStudentById(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        List<Note> notes = noteService.getNotesByStudentId(id); // Fetch notes for the student
        model.addAttribute("student", student);
        model.addAttribute("notes", notes); // Add notes to the model
        return "students/student_detail"; // Note the subfolder reference
    }

    @GetMapping("/add")
    public String addStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "students/add_student"; // Note the subfolder reference
    }

    @PostMapping("/add")
    public String createStudent(@ModelAttribute Student student) {
        // Get the first school from the database
        // In a real application, you would get the school associated with the logged-in user
        School school = schoolRepository.findAll().stream().findFirst().orElse(null);
        student.setSchool(school);
        
        studentService.createStudent(student);
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student.getStatus() == null) {
            student.setStatus(StudentStatus.ACTIVE);
        }
        // Initialize nested objects if null
        if (student.getHomeAddress() == null) {
            student.setHomeAddress(new Address());
        }
        
        // Get emergency contacts for this student
        List<EmergencyContact> emergencyContacts = emergencyContactService.findByStudentId(id);
        
        // Get all ranks for the rank dropdown
        List<Rank> ranks = rankService.getAllRanks();
        
        model.addAttribute("student", student);
        model.addAttribute("emergencyContacts", emergencyContacts);
        model.addAttribute("statuses", StudentStatus.values());
        model.addAttribute("ranks", ranks);
        return "students/edit_student";
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(Model model, @PathVariable Long id, @ModelAttribute("student") Student student) {
        Student existingStudent = studentService.getStudentById(id);
        StudentStatus currentStatus = existingStudent.getStatus(); // Store current status

        // Preserve existing data that isn't in the form
        if (student.getHomeAddress() != null) {
            existingStudent.setHomeAddress(student.getHomeAddress());
        }


        // Update all fields
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setPhoneNumber(student.getPhoneNumber());

        // Only update status if it's explicitly changed in the form
        if (student.getStatus() != null && student.getStatus() != StudentStatus.PROSPECT) {
            existingStudent.setStatus(student.getStatus());
        } else {
            existingStudent.setStatus(currentStatus); // Maintain existing status
        }

        Student updatedStudent = studentService.updateStudent(id, existingStudent);
        
        // Get all ranks for the rank dropdown
        List<Rank> ranks = rankService.getAllRanks();
        
        // Get emergency contacts for this student
        List<EmergencyContact> emergencyContacts = emergencyContactService.findByStudentId(id);
        
        model.addAttribute("student", updatedStudent);
        model.addAttribute("emergencyContacts", emergencyContacts);
        model.addAttribute("statuses", StudentStatus.values());
        model.addAttribute("ranks", ranks);

        return "students/edit_student";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        boolean canDelete = student.getStatus() == StudentStatus.PROSPECT || 
                           student.getStatus() == StudentStatus.CANCELED;
        
        if (canDelete) {
            studentService.deleteStudent(id);
            return "redirect:/students";
        } else {
            return "redirect:/students/edit/" + id + "?error=cannot_delete_active";
        }
    }
    
    @GetMapping("/{id}/confirm-delete")
    public String confirmDeleteStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        boolean canDelete = student.getStatus() == StudentStatus.PROSPECT || 
                           student.getStatus() == StudentStatus.CANCELED;
        
        model.addAttribute("studentId", id);
        model.addAttribute("student", student);
        model.addAttribute("canDelete", canDelete);
        
        if (canDelete) {
            return "students/fragments/delete_confirmation :: confirmDelete";
        } else {
            return "students/fragments/delete_error :: deleteError";
        }
    }
    
    @GetMapping("/cancel-delete")
    public String cancelDelete() {
        // Return empty string to clear the modal
        return "students/fragments/empty :: empty";
    }

    // // New endpoint: Show form to add a note for a student
    // @GetMapping("/{id}/notes/add")
    // public String addNoteForm(@PathVariable Long id, Model model) {
    //     Student student = studentService.getStudentById(id);
    //     model.addAttribute("student", student);
    //     model.addAttribute("note", new Note()); // Create a new Note object for the form
    //     return "students/add_note"; // View template for adding a note
    // }

    // New endpoint: Handle form submission for adding a note
    @PostMapping("/{id}/notes/add")
public String createNote(@PathVariable Long id,
                         @RequestParam("content") String content,
                         Model model) {
    Student student = studentService.getStudentById(id);

    Note note = new Note();
    note.setContent(content);
    note.setStudent(student);

    noteService.createNote(note);

    // Add the updated student to the model so the fragment has access to student.notes
    model.addAttribute("student", student);
    return "students/edit_student :: notes-container";
}

    // Rank Modal Fragment
    @GetMapping("/{id}/rank-modal")
    public String showRankModal(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "students/fragments/rank_modal :: rankModalContent";
    }
    
    // Stripe Preview Fragment
    @GetMapping("/{id}/stripe-preview")
    public String showStripePreview(
            @PathVariable Long id,
            @RequestParam(value = "rankId", required = false) Long rankId,
            @RequestParam(value = "stripes", defaultValue = "0") Integer stripes,
            Model model) {
        
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("currentStripes", stripes);
        
        // If rankId is provided, get the max stripes for that rank
        if (rankId != null) {
            try {
                Rank rank = rankService.getRankById(rankId);
                model.addAttribute("selectedRank", rank);
                // Ensure stripes doesn't exceed max for the rank
                if (rank.getMaxStripes() != null && stripes > rank.getMaxStripes()) {
                    model.addAttribute("currentStripes", rank.getMaxStripes());
                }
            } catch (Exception e) {
                log.error("Error fetching rank: {}", e.getMessage());
            }
        }
        
        return "students/fragments/stripe_preview :: stripePreview";
    }


    // New endpoint: Delete a specific note by its ID
    @GetMapping("/{studentId}/notes/delete/{noteId}")
    public String deleteNote(@PathVariable Long studentId, 
                             @PathVariable Long noteId,
                             Model model) {
        Student student = studentService.getStudentById(studentId);

        noteService.deleteNote(noteId);
        
        model.addAttribute("student", student);// Delete the note by its ID
        return "students/edit_student :: notes-container"; // Redirect back to the student's detail page
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") String statusStr) {
        try {
            log.info("Received status update request - ID: {} Status: {}", id, statusStr);
            StudentStatus status = StudentStatus.valueOf(statusStr);
            log.info("Converted to enum: {}", status);
            studentService.updateStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating status", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Add this exception handler in your controller
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        return ex.getMessage();
    }

}
