package com.whitelabel.martialarts.service.service;

import com.stripe.exception.StripeException;
import com.whitelabel.martialarts.model.Payment;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<Payment> getPaymentsForStudent(Long studentId);
    List<Payment> getAllPayments();
    Payment getPaymentById(Long id);
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
    
    /**
     * Creates a Stripe Checkout Session for a student
     * 
     * @param studentId ID of the student making the payment
     * @param amount The payment amount
     * @param description Description of what the payment is for
     * @return Map containing sessionId and checkout URL
     * @throws StripeException If there's an error creating the session
     */
    Map<String, String> createCheckoutSession(Long studentId, BigDecimal amount, String description) throws StripeException;
    
    /**
     * Processes a completed Stripe payment
     * 
     * @param sessionId The Stripe Session ID
     * @return The created Payment entity
     * @throws StripeException If there's an error processing the payment
     */
    Payment processStripePayment(String sessionId) throws StripeException;
    
    /**
     * Saves a payment to the database
     * 
     * @param payment The payment to save
     * @return The saved payment
     */
    Payment savePayment(Payment payment);
    
    /**
     * Gets all payments for a student
     * 
     * @param studentId The student ID
     * @return List of payments
     */
    List<Payment> getPaymentsByStudentId(Long studentId);
    
    /**
     * Gets all payments for a school
     * 
     * @param schoolId The school ID
     * @return List of payments
     */
    List<Payment> getPaymentsBySchoolId(Long schoolId);
}
