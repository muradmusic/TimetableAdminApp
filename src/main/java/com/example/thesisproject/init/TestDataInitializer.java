package com.example.thesisproject.init;

import com.example.thesisproject.datamodel.entity.Subject;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.SubjectRepository;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.repository.UserSubjectRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class TestDataInitializer implements CommandLineRunner {

//    private static boolean initialized = false;

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final UserSubjectRepository userSubjectRepository;

    @Autowired
    public TestDataInitializer(
            UserRepository userRepository,
            SubjectRepository subjectRepository,
            UserSubjectRepository userSubjectRepository
    ) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.userSubjectRepository = userSubjectRepository;
    }

    @Override
    public void run(String... args) throws Exception {

            User user = new User("guthondr", "password");
            User user1 = new User("mammamur", "password");

            userRepository.save(user);
            userRepository.save(user1);

            Subject subject = new Subject("BIE-PA1");
            Subject subject1 = new Subject("BIE-TJV");

            subjectRepository.save(subject);
            subjectRepository.save(subject1);

            // Create UserSubject instances with different teaching types
            UserSubject lectureUserSubject = new UserSubject(user, subject, TeachingType.LECTURE);
            UserSubject labUserSubject = new UserSubject(user, subject, TeachingType.LAB);
            UserSubject lectureUserSubject1 = new UserSubject(user1, subject1, TeachingType.LECTURE);
            UserSubject labUserSubject1 = new UserSubject(user1, subject1, TeachingType.LAB);

            // Save the instances to the database
            userSubjectRepository.save(lectureUserSubject);
            userSubjectRepository.save(labUserSubject);
            userSubjectRepository.save(lectureUserSubject1);
            userSubjectRepository.save(labUserSubject1);
        }
}