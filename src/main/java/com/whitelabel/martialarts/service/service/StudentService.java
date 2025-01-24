package com.whitelabel.martialarts.service.service;

import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.model.StudentStatus;

import java.util.List;

public interface StudentService {
    List<Student> getAllStudents();
    Student getStudentById(Long id);
    Student createStudent(Student student);
    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);
    void updateStatus(Long id, StudentStatus status);
}
