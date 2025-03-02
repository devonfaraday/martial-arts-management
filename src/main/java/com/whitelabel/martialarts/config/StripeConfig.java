package com.whitelabel.martialarts.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.secret-key}")
    private String stripeSecretKey;
    
    @Value("${stripe.api.publishable-key}")
    private String stripePublishableKey;
    
    @Value("${stripe.success.url:http://localhost:8080/payment/success}")
    private String successUrl;

    @Value("${stripe.cancel.url:http://localhost:8080/payment/cancel}")
    private String cancelUrl;
    
    @Value("${stripe.base.url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${stripe.platform.fee.percentage:10}")
    private int platformFeePercentage;
    
    @Value("${stripe.connect.client.id:ca_}")
    private String connectClientId;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
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
    
    public int getPlatformFeePercentage() {
        return platformFeePercentage;
    }
    
    public String getConnectClientId() {
        return connectClientId;
    }
}
