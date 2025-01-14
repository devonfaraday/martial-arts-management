package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.BillingInfo;
import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.repository.StudentRepository;
import com.whitelabel.martialarts.service.service.BillingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingInfoServiceImpl implements BillingInfoService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public BillingInfo getBillingInfoByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        return student.getBillingInfo();
    }

    @Override
    public BillingInfo updateBillingInfo(Long studentId, BillingInfo billingInfo) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        student.setBillingInfo(billingInfo);
        studentRepository.save(student);
        return billingInfo;
    }
}
