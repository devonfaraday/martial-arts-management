package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.ClassSession;
import com.whitelabel.martialarts.service.ClassSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class-sessions")
public class ClassSessionController {

    @Autowired
    private ClassSessionService classSessionService;

    @GetMapping
    public List<ClassSession> getAllClassSessions() {
        return classSessionService.getAllClassSessions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassSession> getClassSessionById(@PathVariable Long id) {
        ClassSession classSession = classSessionService.getClassSessionById(id);
        return ResponseEntity.ok(classSession);
    }

    @PostMapping
    public ClassSession createClassSession(@RequestBody ClassSession classSession) {
        return classSessionService.createClassSession(classSession);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassSession> updateClassSession(@PathVariable Long id, @RequestBody ClassSession classSession) {
        ClassSession updatedClassSession = classSessionService.updateClassSession(id, classSession);
        return ResponseEntity.ok(updatedClassSession);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassSession(@PathVariable Long id) {
        classSessionService.deleteClassSession(id);
        return ResponseEntity.noContent().build();
    }
}
