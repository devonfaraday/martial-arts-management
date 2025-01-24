package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.model.StudentStatus;
import com.whitelabel.martialarts.repository.StudentRepository;
import com.whitelabel.martialarts.service.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setPhoneNumber(student.getPhoneNumber());
        existingStudent.setRank(student.getRank());
        return studentRepository.save(existingStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, StudentStatus status) {
        studentRepository.updateStatus(id, status, new Timestamp(System.currentTimeMillis()));
    }
}
