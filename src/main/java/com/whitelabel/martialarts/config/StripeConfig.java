package com.whitelabel.martialarts.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.annotation.PostConstruct;

@Configuration
@PropertySource(value = "classpath:application-stripe.properties", ignoreResourceNotFound = true)
public class StripeConfig {

    @Value("${stripe.api.secret-key:sk_test_placeholder}")
    private String stripeSecretKey;
    
    @Value("${stripe.api.publishable-key:pk_test_placeholder}")
    private String stripePublishableKey;
    
    @Value("${stripe.success.url:http://localhost:8080/payment/success}")
    private String successUrl;

    @Value("${stripe.cancel.url:http://localhost:8080/payment/cancel}")
    private String cancelUrl;
    
    @Value("${stripe.base.url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${stripe.platform.fee.percentage:10}")
    private long platformFeePercentage;
    
    @Value("${stripe.connect.client.id:ca_placeholder}")
    private String connectClientId;

    @Value("${stripe.webhook.secret:whsec_placeholder}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
        // Remove the API version setting as it might be causing compatibility issues
        // Stripe.apiVersion = "2020-08-27"; 
    }
    
    public String getPublishableKey() {
        return stripePublishableKey;
    }
    
    public String getApiKey() {
        return stripeSecretKey;
    }
    
    public String getSuccessUrl() {
        return successUrl;
    }
    
    public String getCancelUrl() {
        return cancelUrl;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public long getPlatformFeePercentage() {
        return platformFeePercentage;
    }
    
    public String getConnectClientId() {
        return connectClientId;
    }
    
    public String getWebhookSecret() {
        return webhookSecret;
    }
}
