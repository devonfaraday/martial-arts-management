package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.Note;
import com.whitelabel.martialarts.service.service.NoteService;
import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.model.StudentStatus;
import com.whitelabel.martialarts.service.service.StudentService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private NoteService noteService;

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
        studentService.createStudent(student);
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student.getStatus() == null) {
            student.setStatus(StudentStatus.ACTIVE); // Set a default if null
        }
        System.out.println("Student: " + student); // Log student details
        System.out.println("Statuses: " + Arrays.toString(StudentStatus.values())); // Log statu
        model.addAttribute("student", student);
        model.addAttribute("statuses", StudentStatus.values());
        return "students/edit_student";
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute Student student) {
        studentService.updateStudent(id, student);
        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }

    // New endpoint: Show form to add a note for a student
    @GetMapping("/{id}/notes/add")
    public String addNoteForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("note", new Note()); // Create a new Note object for the form
        return "students/add_note"; // View template for adding a note
    }

    // New endpoint: Handle form submission for adding a note
    @PostMapping("/{id}/notes/add")
    public String createNote(@PathVariable Long id, @ModelAttribute Note note) {
        Student student = studentService.getStudentById(id);
        note.setStudent(student); // Associate the note with the student
        noteService.createNote(note); // Save the note
        return "redirect:/students/" + id; // Redirect back to the student's detail page
    }

    // New endpoint: Delete a specific note by its ID
    @GetMapping("/{studentId}/notes/delete/{noteId}")
    public String deleteNote(@PathVariable Long studentId, @PathVariable Long noteId) {
        noteService.deleteNote(noteId); // Delete the note by its ID
        return "redirect:/students/" + studentId; // Redirect back to the student's detail page
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
}
