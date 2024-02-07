package com.example.thesisproject.init;

import com.example.thesisproject.datamodel.entity.Subject;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.SubjectRepository;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.repository.UserSubjectRepository;
import com.example.thesisproject.service.SubjectService;
import com.example.thesisproject.service.UserService;
import com.example.thesisproject.service.UserSubjectService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


//@Component
//public class TestDataInitializer implements CommandLineRunner {
//
//    private final UserSubjectRepository userSubjectRepository;
//    private final UserService userService;
//    private final SubjectService subjectService;
//
//    private final UserSubjectService userSubjectService;
//
//    @Autowired
//    public TestDataInitializer(
//            UserSubjectService userSubjectService,
//            UserService userService,
//            SubjectService subjectService,
//            UserSubjectRepository userSubjectRepository
//    ) {
//        this.userSubjectService = userSubjectService;
//        this.userService = userService;
//        this.subjectService = subjectService;
//        this.userSubjectRepository = userSubjectRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//
//
//
//        User user = new User("guthondr", "password");
//        User user1 = new User("mammamur", "password");
//
//        userService.createUser(user);
//        userService.createUser(user1);
//
//        Subject subject = new Subject("BIE-PA1", true);
//        Subject subject1 = new Subject("BIE-TJV", false);
//
//        subjectService.createSubject(subject);
//        subjectService.createSubject(subject1);
//
//        UserSubject lectureUserSubject = new UserSubject(user, subject, TeachingType.LECTURE);
//        UserSubject labUserSubject = new UserSubject(user, subject, TeachingType.LAB);
//        UserSubject lectureUserSubject1 = new UserSubject(user1, subject1, TeachingType.LECTURE);
//        UserSubject labUserSubject1 = new UserSubject(user1, subject1, TeachingType.LAB);
//
//        userSubjectService.createUserSubject(lectureUserSubject);
//        userSubjectService.createUserSubject(labUserSubject);
//        userSubjectService.createUserSubject(lectureUserSubject1);
//        userSubjectService.createUserSubject(labUserSubject1);
//
//
//        }
//}