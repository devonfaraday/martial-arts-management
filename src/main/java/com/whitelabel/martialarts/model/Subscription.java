package com.whitelabel.martialarts.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    private SubscriptionInterval interval;
    
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    
    private LocalDate nextBillingDate;
    private boolean active;
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public SubscriptionInterval getInterval() {
        return interval;
    }
    
    public void setInterval(SubscriptionInterval interval) {
        this.interval = interval;
    }
    
    public String getStripeCustomerId() {
        return stripeCustomerId;
    }
    
    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }
    
    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }
    
    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }
    
    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }
    
    public void setNextBillingDate(LocalDate nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}
