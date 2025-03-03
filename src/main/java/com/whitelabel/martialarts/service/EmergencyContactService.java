package com.whitelabel.martialarts.service;

import com.whitelabel.martialarts.model.EmergencyContact;
import com.whitelabel.martialarts.repository.EmergencyContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public EmergencyContact findById(Long id) {
        return emergencyContactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency Contact not found"));
    }

    public void deleteById(Long id) {
        emergencyContactRepository.deleteById(id);
    }
}
