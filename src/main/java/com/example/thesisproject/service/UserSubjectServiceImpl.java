package com.example.thesisproject.service;


import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.Subject;

import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.SubjectRepository;
import com.example.thesisproject.repository.UserRepository;
import com.example.thesisproject.repository.UserSubjectRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserSubjectServiceImpl implements UserSubjectService {

    @Autowired
    private UserSubjectRepository userSubjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired

    private SubjectRepository subjectRepository;

    @PostConstruct
    public void initializeTestData() {
        // Assuming you have users and subjects already created in your database
        User user = userRepository.findUserByUsername("mammamur").orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user1 = userRepository.findUserByUsername("guthondr").orElseThrow(() -> new EntityNotFoundException("User not found"));

        Subject subject = subjectRepository.findById(1l).orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        Subject subject1 = subjectRepository.findById(2l).orElseThrow(() -> new EntityNotFoundException("Subject not found"));


        // Create UserSubject instances with different teaching types
        UserSubject lectureUserSubject = new UserSubject(user, subject, TeachingType.LECTURE);
        UserSubject labUserSubject = new UserSubject(user, subject, TeachingType.LAB);
        UserSubject lectureUserSubject1 = new UserSubject(user1, subject1, TeachingType.LECTURE);
        UserSubject labUserSubject1 = new UserSubject(user1, subject1, TeachingType.LAB);

//        UserSubject bothUserSubject = new UserSubject(user, subject, TeachingType.BOTH);

        // Save the instances to the database
        userSubjectRepository.save(lectureUserSubject);
        userSubjectRepository.save(labUserSubject);
        userSubjectRepository.save(lectureUserSubject1);
        userSubjectRepository.save(labUserSubject1);
//        userSubjectRepository.save(bothUserSubject);
    }


}
