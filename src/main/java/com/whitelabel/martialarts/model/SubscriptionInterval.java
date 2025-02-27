package com.whitelabel.martialarts.model;

public enum SubscriptionInterval {
    MONTHLY("Monthly"),
    BI_WEEKLY("Bi-Weekly");
    
    private final String displayName;
    
    SubscriptionInterval(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
