package com.whitelabel.martialarts.model;

public class RegistrationForm {
    private Organization organization;
    private AppUser appUser;

    // Getters and setters
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
