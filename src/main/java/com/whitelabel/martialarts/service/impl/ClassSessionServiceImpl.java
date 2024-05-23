package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.ClassSession;
import com.whitelabel.martialarts.repository.ClassSessionRepository;
import com.whitelabel.martialarts.service.ClassSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassSessionServiceImpl implements ClassSessionService {

    @Autowired
    private ClassSessionRepository classSessionRepository;

    @Override
    public List<ClassSession> getAllClassSessions() {
        return classSessionRepository.findAll();
    }

    @Override
    public ClassSession getClassSessionById(Long id) {
        return classSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClassSession not found with id: " + id));
    }

    @Override
    public ClassSession createClassSession(ClassSession classSession) {
        return classSessionRepository.save(classSession);
    }

    @Override
    public ClassSession updateClassSession(Long id, ClassSession classSession) {
        ClassSession existingClassSession = classSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClassSession not found with id: " + id));
        existingClassSession.setName(classSession.getName());
        existingClassSession.setDescription(classSession.getDescription());
        existingClassSession.setSchedule(classSession.getSchedule());
        existingClassSession.setLocation(classSession.getLocation());
        existingClassSession.setStaff(classSession.getStaff());
        return classSessionRepository.save(existingClassSession);
    }

    @Override
    public void deleteClassSession(Long id) {
        classSessionRepository.deleteById(id);
    }
}
