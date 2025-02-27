package com.whitelabel.martialarts.service.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.whitelabel.martialarts.model.Payment;
import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.model.Subscription;
import com.whitelabel.martialarts.model.SubscriptionInterval;

import java.math.BigDecimal;
import java.util.Map;

public interface StripeService {
    /**
     * Creates a Stripe Checkout Session for a student through the school's connected account
     * 
     * @param student The student making the payment
     * @param amount The payment amount
     * @param description Description of what the payment is for
     * @return Map containing sessionId and checkout URL
     * @throws StripeException If there's an error creating the session
     */
    Map<String, String> createCheckoutSession(Student student, BigDecimal amount, String description) throws StripeException;
    
    /**
     * Creates a Payment entity from a completed Stripe Session
     * 
     * @param session The completed Stripe Session
     * @param student The student who made the payment
     * @return Payment entity ready to be saved
     */
    Payment createPaymentFromSession(Session session, Student student);
    
    /**
     * Retrieves a Stripe Session by ID
     * 
     * @param sessionId The Stripe Session ID
     * @return The Stripe Session
     * @throws StripeException If there's an error retrieving the session
     */
    Session retrieveSession(String sessionId) throws StripeException;
    
    /**
     * Generates a Stripe Connect onboarding URL for a school
     * 
     * @param school The school to onboard
     * @param returnUrl The URL to redirect to after onboarding
     * @return The onboarding URL
     * @throws StripeException If there's an error generating the URL
     */
    String createConnectAccountLink(School school, String returnUrl) throws StripeException;
    
    /**
     * Creates a Stripe Connect account for a school
     * 
     * @param school The school to create an account for
     * @return The created Stripe account ID
     * @throws StripeException If there's an error creating the account
     */
    String createConnectAccount(School school) throws StripeException;
    
    /**
     * Generates a Stripe Connect account link for an existing account ID
     * 
     * @param accountId The Stripe Connect account ID
     * @param returnUrl The URL to redirect to after onboarding
     * @return The account link URL
     * @throws StripeException If there's an error generating the link
     */
    String generateAccountLink(String accountId, String returnUrl) throws StripeException;
    
    /**
     * Handles the OAuth callback from Stripe Connect
     * 
     * @param code The authorization code from Stripe
     * @param schoolId The ID of the school being connected
     * @return The updated school with Stripe Connect credentials
     * @throws StripeException If there's an error handling the callback
     */
    School handleConnectOAuthCallback(String code, Long schoolId) throws StripeException;
    
    /**
     * Checks if a school's Stripe Connect account is properly set up
     * 
     * @param school The school to check
     * @return true if the account is active and ready to accept payments
     * @throws StripeException If there's an error checking the account
     */
    boolean isConnectAccountEnabled(School school) throws StripeException;
    
    /**
     * Retrieves a Stripe Session from a specific connected account
     * 
     * @param sessionId The Stripe Session ID
     * @param connectedAccountId The ID of the connected account
     * @return The Stripe Session
     * @throws StripeException If there's an error retrieving the session
     */
    Session retrieveConnectedAccountSession(String sessionId, String connectedAccountId) throws StripeException;
    
    /**
     * Imports an existing Stripe Connect account into our system
     * 
     * @param school The school to associate with the account
     * @param accountId The Stripe account ID to import
     * @return The updated school with Stripe Connect credentials
     * @throws StripeException If there's an error importing the account
     */
    School importConnectAccount(School school, String accountId) throws StripeException;
    
    /**
     * Creates a Stripe Customer for a student
     * 
     * @param student The student to create a customer for
     * @param paymentMethodId The ID of the payment method to attach
     * @return The Stripe Customer ID
     * @throws StripeException If there's an error creating the customer
     */
    String createCustomer(Student student, String paymentMethodId) throws StripeException;
    
    /**
     * Creates a subscription for a student
     * 
     * @param student The student to create a subscription for
     * @param customerId The Stripe Customer ID
     * @param paymentMethodId The ID of the payment method to use
     * @param amount The subscription amount
     * @param interval The billing interval (monthly or bi-weekly)
     * @return The created Subscription object
     * @throws StripeException If there's an error creating the subscription
     */
    Subscription createSubscription(Student student, String customerId, String paymentMethodId, 
                                   BigDecimal amount, SubscriptionInterval interval) throws StripeException;
                                   
    /**
     * Creates a Stripe Checkout session for subscription setup
     * 
     * @param student The student to create a subscription for
     * @param amount The subscription amount
     * @param interval The billing interval (monthly or bi-weekly)
     * @return The created Checkout Session
     * @throws StripeException If there's an error creating the session
     */
    com.stripe.model.checkout.Session createSubscriptionCheckoutSession(Student student, 
                                                                       BigDecimal amount, 
                                                                       SubscriptionInterval interval) throws StripeException;
    
    /**
     * Process a successful Stripe Checkout session for subscription
     * 
     * @param sessionId The ID of the completed checkout session
     * @return The created Subscription object
     * @throws StripeException If there's an error processing the session
     */
    Subscription processSuccessfulSubscriptionCheckout(String sessionId) throws StripeException;
    
    /**
     * Get the student associated with a checkout session
     * 
     * @param sessionId The ID of the checkout session
     * @return The associated Student or null if not found
     * @throws StripeException If there's an error retrieving the session
     */
    Student getStudentFromCheckoutSession(String sessionId) throws StripeException;
}
