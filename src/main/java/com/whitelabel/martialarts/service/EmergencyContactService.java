package com.whitelabel.martialarts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whitelabel.martialarts.model.EmergencyContact;
import com.whitelabel.martialarts.repository.EmergencyContactRepository;

@Service
public class EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;

    @Autowired
    public EmergencyContactService(EmergencyContactRepository emergencyContactRepository) {
        this.emergencyContactRepository = emergencyContactRepository;
    }

    public EmergencyContact save(EmergencyContact emergencyContact) {
        return emergencyContactRepository.save(emergencyContact);
    }

    public List<EmergencyContact> findAll() {
        return emergencyContactRepository.findAll();
    }
    
    public List<EmergencyContact> findByStudentId(Long studentId) {
        return emergencyContactRepository.findByStudentId(studentId);
    }

    public EmergencyContact findById(Long id) {
        return emergencyContactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency Contact not found"));
    }

    public void deleteById(Long id) {
        emergencyContactRepository.deleteById(id);
    }
}
