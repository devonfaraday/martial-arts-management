package com.whitelabel.martialarts.repository;

import com.whitelabel.martialarts.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByLastNameContainingOrRoleContaining(String keyword, String keyword1);
}
