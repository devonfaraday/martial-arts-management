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
import java.util.Optional;
import jakarta.persistence.EntityNotFoundException;

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

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
    }

    @Override
    @Transactional
    public Payment updatePayment(Long id, Payment paymentDetails) {
        Payment payment = getPaymentById(id);
        
        // Update payment details
        payment.setAmount(paymentDetails.getAmount());
        payment.setCurrency(paymentDetails.getCurrency());
        payment.setStatus(paymentDetails.getStatus());
        payment.setPaymentDate(paymentDetails.getPaymentDate());
        
        // If student is changed, update the relationship
        if (paymentDetails.getStudent() != null) {
            payment.setStudent(paymentDetails.getStudent());
        }
        
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);
        paymentRepository.delete(payment);
    }

    // Optionally add methods here to integrate with Stripe's PaymentIntent API.
    // For example, a method that calls Stripe, handles the response, and then records the payment.
}
