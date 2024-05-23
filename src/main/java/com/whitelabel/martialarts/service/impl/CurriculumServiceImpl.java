package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.Curriculum;
import com.whitelabel.martialarts.repository.CurriculumRepository;
import com.whitelabel.martialarts.service.CurriculumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurriculumServiceImpl implements CurriculumService {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Override
    public List<Curriculum> getAllCurricula() {
        return curriculumRepository.findAll();
    }

    @Override
    public Curriculum getCurriculumById(Long id) {
        return curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found with id: " + id));
    }

    @Override
    public Curriculum createCurriculum(Curriculum curriculum) {
        return curriculumRepository.save(curriculum);
    }

    @Override
    public Curriculum updateCurriculum(Long id, Curriculum curriculum) {
        Curriculum existingCurriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found with id: " + id));
        existingCurriculum.setName(curriculum.getName());
        existingCurriculum.setDescription(curriculum.getDescription());
        existingCurriculum.setSchool(curriculum.getSchool());
        return curriculumRepository.save(existingCurriculum);
    }

    @Override
    public void deleteCurriculum(Long id) {
        curriculumRepository.deleteById(id);
    }
}
