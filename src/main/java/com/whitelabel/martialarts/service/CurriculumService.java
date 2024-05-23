package com.whitelabel.martialarts.service;

import com.whitelabel.martialarts.model.Curriculum;
import java.util.List;

public interface CurriculumService {
    List<Curriculum> getAllCurricula();
    Curriculum getCurriculumById(Long id);
    Curriculum createCurriculum(Curriculum curriculum);
    Curriculum updateCurriculum(Long id, Curriculum curriculum);
    void deleteCurriculum(Long id);
}
