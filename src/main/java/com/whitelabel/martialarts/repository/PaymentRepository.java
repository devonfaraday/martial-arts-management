package com.whitelabel.martialarts.repository;

import com.whitelabel.martialarts.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudentId(Long studentId);
    List<Payment> findBySchoolId(Long schoolId);
}
