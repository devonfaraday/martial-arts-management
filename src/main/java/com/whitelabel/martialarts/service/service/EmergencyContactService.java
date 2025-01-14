package com.whitelabel.martialarts.service.service;

import com.whitelabel.martialarts.model.EmergencyContact;
import java.util.List;

public interface EmergencyContactService {
    List<EmergencyContact> getAllEmergencyContacts();
    EmergencyContact getEmergencyContactById(Long id);
    EmergencyContact createEmergencyContact(EmergencyContact emergencyContact);
    EmergencyContact updateEmergencyContact(Long id, EmergencyContact emergencyContact);
    void deleteEmergencyContact(Long id);
}
