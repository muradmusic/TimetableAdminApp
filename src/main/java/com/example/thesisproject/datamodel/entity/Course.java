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


    private int numLecture = 0;
    private int numSeminar = 0;
    private int numLab = 0;
    private int currentLecture = 0;
    private int currentSeminar = 0;
    private int currentLab = 0;
    private boolean hasLabs;

//    private int desiredLab;

    private boolean approvedAll;
    public Course(String courseCode, boolean hasLabs, int numLecture, int numSeminar, int numLab, int currentLecture, int currentSeminar, int currentLab) {
        this.courseCode = courseCode;
        this.hasLabs = hasLabs;
        this.numLecture = numLecture;
        this.numSeminar = numSeminar;
        this.numLab = numLab;
        this.currentLecture = currentLecture;
        this.currentSeminar = currentSeminar;
        this.currentLab = currentLab;
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
