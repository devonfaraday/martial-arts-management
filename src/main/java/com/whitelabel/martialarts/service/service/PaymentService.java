package com.whitelabel.martialarts.service.service;

import com.whitelabel.martialarts.model.Payment;
import java.util.List;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<Payment> getPaymentsForStudent(Long studentId);
    
    // You may later add methods to process Stripe payment directly,
    // for example: Payment processStripePayment(Long studentId, BigDecimal amount, String currency);
}
