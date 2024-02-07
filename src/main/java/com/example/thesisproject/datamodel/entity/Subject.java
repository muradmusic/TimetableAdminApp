package com.example.thesisproject.datamodel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Table(name = "subjects")
@Entity
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectCode;

    private int minValue = -1;
    private int maxValue = -1;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserSubject> userSubjects = new HashSet<>();

    private boolean hasLabs;


    public Subject(String subjectCode, boolean hasLabs) {
        this.subjectCode = subjectCode;
        this.hasLabs = hasLabs;
    }
}
