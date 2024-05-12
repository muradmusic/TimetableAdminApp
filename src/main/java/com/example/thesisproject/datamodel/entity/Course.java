package com.example.thesisproject.datamodel.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Table(name = "courses")
@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseCode;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCourse> userCourses = new HashSet<>();

    private boolean hasLabs;

    private int desiredLab;

    private boolean approvedAll;
    public Course(String courseCode, boolean hasLabs) {
        this.courseCode = courseCode;
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
