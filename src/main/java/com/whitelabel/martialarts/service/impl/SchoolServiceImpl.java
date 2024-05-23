package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.repository.SchoolRepository;
import com.whitelabel.martialarts.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    @Override
    public School getSchoolById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("School not found with id: " + id));
    }

    @Override
    public School createSchool(School school) {
        return schoolRepository.save(school);
    }

    @Override
    public School updateSchool(Long id, School school) {
        School existingSchool = schoolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("School not found with id: " + id));
        existingSchool.setName(school.getName());
        return schoolRepository.save(existingSchool);
    }

    @Override
    public void deleteSchool(Long id) {
        schoolRepository.deleteById(id);
    }
}
