package com.whitelabel.martialarts.service.service;

import com.whitelabel.martialarts.model.Payment;
import java.util.List;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<Payment> getPaymentsForStudent(Long studentId);
    List<Payment> getAllPayments();
    Payment getPaymentById(Long id);
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
    
    // You may later add methods to process Stripe payment directly,
    // for example: Payment processStripePayment(Long studentId, BigDecimal amount, String currency);
}
