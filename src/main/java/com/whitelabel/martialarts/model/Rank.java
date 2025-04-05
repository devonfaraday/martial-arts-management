package com.whitelabel.martialarts.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Rank {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name;
    
    private String beltColor;
    
    private Integer maxStripes;
    
    private Integer displayOrder;

    @OneToMany(mappedBy = "rank")
    private List<Student> students;

    // Getters and setters
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

    public String getBeltColor() {
        return beltColor;
    }

    public void setBeltColor(String beltColor) {
        this.beltColor = beltColor;
    }

    public Integer getMaxStripes() {
        return maxStripes;
    }

    public void setMaxStripes(Integer maxStripes) {
        this.maxStripes = maxStripes;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
