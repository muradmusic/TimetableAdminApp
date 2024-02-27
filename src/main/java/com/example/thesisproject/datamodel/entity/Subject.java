package com.example.thesisproject.datamodel.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Table(name = "subjects")
@Entity
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectCode;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserSubject> userSubjects = new HashSet<>();

    private boolean hasLabs;

    private int desiredLab;

    private boolean approvedAll;
    public Subject(String subjectCode, boolean hasLabs) {
        this.subjectCode = subjectCode;
        this.hasLabs = hasLabs;
    }
    public boolean hasLabs() {
        return hasLabs;
    }
    public boolean approvedAll(){
        return approvedAll;
    }

    public void setHasLabs(boolean hasLabs) {
        this.hasLabs = hasLabs;
    }
}
