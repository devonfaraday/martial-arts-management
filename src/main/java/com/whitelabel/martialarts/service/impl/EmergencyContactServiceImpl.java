package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.EmergencyContact;
import com.whitelabel.martialarts.repository.EmergencyContactRepository;
import com.whitelabel.martialarts.service.service.EmergencyContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmergencyContactServiceImpl implements EmergencyContactService {

    @Autowired
    private EmergencyContactRepository emergencyContactRepository;

    @Override
    public List<EmergencyContact> getAllEmergencyContacts() {
        return emergencyContactRepository.findAll();
    }

    @Override
    public EmergencyContact getEmergencyContactById(Long id) {
        return emergencyContactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency Contact not found with id: " + id));
    }

    @Override
    public EmergencyContact createEmergencyContact(EmergencyContact emergencyContact) {
        return emergencyContactRepository.save(emergencyContact);
    }

    @Override
    public EmergencyContact updateEmergencyContact(Long id, EmergencyContact emergencyContact) {
        EmergencyContact existingEmergencyContact = getEmergencyContactById(id);
        existingEmergencyContact.setName(emergencyContact.getName());
        existingEmergencyContact.setRelationship(emergencyContact.getRelationship());
        existingEmergencyContact.setPhoneNumber(emergencyContact.getPhoneNumber());
        existingEmergencyContact.setEmail(emergencyContact.getEmail());
        return emergencyContactRepository.save(existingEmergencyContact);
    }

    @Override
    public void deleteEmergencyContact(Long id) {
        emergencyContactRepository.deleteById(id);
    }
}
