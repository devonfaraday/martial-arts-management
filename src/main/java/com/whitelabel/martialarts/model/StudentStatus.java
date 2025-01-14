package com.whitelabel.martialarts.model;

public enum StudentStatus {
    ACTIVE("Active"),
    FROZEN("Frozen"),
    CANCELED("Canceled"),
    PAYMENT_OVERDUE("Payment Overdue"),
    PROSPECT("Prospect");

    private final String displayName;

    StudentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
