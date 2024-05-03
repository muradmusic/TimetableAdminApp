package com.example.thesisproject.datamodel.entity;

import com.example.thesisproject.datamodel.enums.Decision;
import com.example.thesisproject.datamodel.enums.TeachingType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name ="user_subjects")
@NoArgsConstructor
public class UserSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "teaching_type")
    private TeachingType teachingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision")
    private Decision decision;

    private int minLab = 0;
    private int maxLab = 0;

    public UserSubject(User user, Subject subject, TeachingType teachingType) {
        this.user = user;
        this.subject = subject;
        this.teachingType = teachingType;
    }
@Override
public String toString() {
    return "UserSubject{" +
            "id=" + id +
            ", userId=" + (user != null ? user.getId() : null) +  // only user's ID
            ", subject=" + subject +
            ", teachingType='" + teachingType + '\'' +
            '}';
}

}
