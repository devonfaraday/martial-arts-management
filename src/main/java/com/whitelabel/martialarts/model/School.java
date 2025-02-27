package com.whitelabel.martialarts.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import java.util.HashSet;
import java.util.Set;

@Entity
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name;
    private String address;
    private String email;
    private String phone;
    private String website;
    
    // Stripe Connect fields
    private String stripeConnectAccountId;
    private boolean stripeConnectEnabled;
    private String stripeConnectAccessToken;
    private String stripeConnectRefreshToken;
    
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;
    
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    private Set<Student> students = new HashSet<>();
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getStripeConnectAccountId() {
        return stripeConnectAccountId;
    }
    
    public void setStripeConnectAccountId(String stripeConnectAccountId) {
        this.stripeConnectAccountId = stripeConnectAccountId;
    }
    
    public boolean isStripeConnectEnabled() {
        return stripeConnectEnabled;
    }
    
    public void setStripeConnectEnabled(boolean stripeConnectEnabled) {
        this.stripeConnectEnabled = stripeConnectEnabled;
    }
    
    public String getStripeConnectAccessToken() {
        return stripeConnectAccessToken;
    }
    
    public void setStripeConnectAccessToken(String stripeConnectAccessToken) {
        this.stripeConnectAccessToken = stripeConnectAccessToken;
    }
    
    public String getStripeConnectRefreshToken() {
        return stripeConnectRefreshToken;
    }
    
    public void setStripeConnectRefreshToken(String stripeConnectRefreshToken) {
        this.stripeConnectRefreshToken = stripeConnectRefreshToken;
    }
    
    public Organization getOrganization() {
        return organization;
    }
    
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
    
    public Set<Student> getStudents() {
        return students;
    }
    
    public void setStudents(Set<Student> students) {
        this.students = students;
    }
    
    public void addStudent(Student student) {
        students.add(student);
        student.setSchool(this);
    }
    
    public void removeStudent(Student student) {
        students.remove(student);
        student.setSchool(null);
    }
}
