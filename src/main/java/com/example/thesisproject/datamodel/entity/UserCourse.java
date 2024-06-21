package com.example.thesisproject.datamodel.entity;

import com.example.thesisproject.datamodel.enums.Decision;
import com.example.thesisproject.datamodel.enums.TeachingType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name ="user_courses")
@NoArgsConstructor
public class UserCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "teaching_type")
    private TeachingType teachingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision")
    private Decision decision;

    private int minLab = 0;
    private int maxLab = 0;



    public UserCourse(User user, Course course, TeachingType teachingType) {
        this.user = user;
        this.course = course;
        this.teachingType = teachingType;
    }
@Override
public String toString() {
    return "UserCourse{" +
            "id=" + id +
            ", userId=" + (user != null ? user.getId() : null) +  // only user's ID
            ", course=" + course +
            ", teachingType='" + teachingType + '\'' +
            '}';
}

}
