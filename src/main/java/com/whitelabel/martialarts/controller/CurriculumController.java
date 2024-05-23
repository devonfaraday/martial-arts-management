package com.whitelabel.martialarts.controller;

import com.whitelabel.martialarts.model.Curriculum;
import com.whitelabel.martialarts.service.CurriculumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curriculum")
public class CurriculumController {

    @Autowired
    private CurriculumService curriculumService;

    @GetMapping
    public List<Curriculum> getAllCurriculum() {
        return curriculumService.getAllCurriculum();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curriculum> getCurriculumById(@PathVariable Long id) {
        Curriculum curriculum = curriculumService.getCurriculumById(id);
        return ResponseEntity.ok(curriculum);
    }

    @PostMapping
    public Curriculum createCurriculum(@RequestBody Curriculum curriculum) {
        return curriculumService.createCurriculum(curriculum);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curriculum> updateCurriculum(@PathVariable Long id, @RequestBody Curriculum curriculum) {
        Curriculum updatedCurriculum = curriculumService.updateCurriculum(id, curriculum);
        return ResponseEntity.ok(updatedCurriculum);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurriculum(@PathVariable Long id) {
        curriculumService.deleteCurriculum(id);
        return ResponseEntity.noContent().build();
    }
}
