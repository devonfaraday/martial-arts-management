package com.whitelabel.martialarts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.whitelabel.martialarts.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
