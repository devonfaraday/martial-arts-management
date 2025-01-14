package com.whitelabel.martialarts.repository;

import com.whitelabel.martialarts.model.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    // Additional query methods can be added here if needed
}
