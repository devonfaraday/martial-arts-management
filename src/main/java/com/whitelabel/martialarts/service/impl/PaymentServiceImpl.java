package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.Payment;
import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.repository.PaymentRepository;
import com.whitelabel.martialarts.repository.StudentRepository;
import com.whitelabel.martialarts.service.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    @Override
    @Transactional
    public Payment createPayment(Payment payment) {
        // Persist the payment record.
        Payment savedPayment = paymentRepository.save(payment);
        
        // Optionally update the student record if you need to adjust a balance or log the payment.
        Student student = savedPayment.getStudent();
        // For example, you might update the outstanding balance on the Student here.
        // student.setOutstandingBalance(student.getOutstandingBalance().subtract(savedPayment.getAmount()));
        // studentRepository.save(student);
        
        return savedPayment;
    }

    @Override
    public List<Payment> getPaymentsForStudent(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    // Optionally add methods here to integrate with Stripe's PaymentIntent API.
    // For example, a method that calls Stripe, handles the response, and then records the payment.
}
