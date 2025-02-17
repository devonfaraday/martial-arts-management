package com.whitelabel.martialarts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.sql.Timestamp;

import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.model.StudentStatus;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @Modifying
    @Query("UPDATE Student s SET s.status = :status, s.updatedAt = :updatedAt WHERE s.id = :id")
    void updateStatus(@Param("id") Long id, 
                     @Param("status") StudentStatus status,
                     @Param("updatedAt") Timestamp updatedAt);

    long countByStatus(StudentStatus status);
}
