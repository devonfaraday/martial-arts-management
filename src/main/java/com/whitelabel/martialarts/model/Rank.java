package com.whitelabel.martialarts.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Rank {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @ElementCollection
    private List<String> subRanks;

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

    public List<String> getSubRanks() {
        return subRanks;
    }

    public void setSubRanks(List<String> subRanks) {
        this.subRanks = subRanks;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
