package com.whitelabel.martialarts.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.whitelabel.martialarts.model.Payment;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.repository.PaymentRepository;
import com.whitelabel.martialarts.repository.StudentRepository;
import com.whitelabel.martialarts.service.service.PaymentService;
import com.whitelabel.martialarts.service.service.StripeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Value("${stripe.platform.fee.percentage}")
    private int platformFeePercentage;

    @Override
    @Transactional
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
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
        
        // If school is changed, update the relationship
        if (paymentDetails.getSchool() != null) {
            payment.setSchool(paymentDetails.getSchool());
        }
        
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);
        paymentRepository.delete(payment);
    }

    @Override
    @Transactional
    public Map<String, String> createCheckoutSession(Long studentId, BigDecimal amount, String description) throws StripeException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));
        
        return stripeService.createCheckoutSession(student, amount, description);
    }

    @Override
    @Transactional
    public Payment processStripePayment(String sessionId) throws StripeException {
        Session session = stripeService.retrieveSession(sessionId);
        
        // Get the student from the client reference ID
        String clientReferenceId = session.getClientReferenceId();
        if (clientReferenceId == null) {
            throw new IllegalStateException("No client reference ID found in session");
        }
        
        Long studentId = Long.parseLong(clientReferenceId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));
        
        // Create and save the payment
        Payment payment = stripeService.createPaymentFromSession(session, student);
        
        // Set the school from the student
        School school = student.getSchool();
        if (school != null) {
            payment.setSchool(school);
            
            // If this is a Connect payment, set the connected account ID
            // Check if this is a Connect payment by looking at the metadata
            Map<String, String> metadata = session.getMetadata();
            String connectedAccountId = null;
            if (metadata != null && metadata.containsKey("connected_account")) {
                connectedAccountId = metadata.get("connected_account");
            }
            
            if (connectedAccountId != null) {
                payment.setConnectedAccountId(connectedAccountId);
                
                // Calculate and set the platform fee
                long amountInCents = session.getAmountTotal();
                long feeInCents = (amountInCents * platformFeePercentage) / 100;
                payment.setPlatformFee(new BigDecimal(feeInCents).divide(new BigDecimal("100")));
            }
        }
        
        // Set the description
        if (session.getMetadata() != null && session.getMetadata().containsKey("description")) {
            payment.setDescription(session.getMetadata().get("description"));
        }
        
        return savePayment(payment);
    }
    
    @Override
    @Transactional
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    @Override
    public List<Payment> getPaymentsByStudentId(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }
    
    @Override
    public List<Payment> getPaymentsBySchoolId(Long schoolId) {
        return paymentRepository.findBySchoolId(schoolId);
    }
}
