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

import java.util.List;

@Service
@Transactional
public class UserSubjectServiceImpl implements UserSubjectService {


    @Autowired
    private UserSubjectRepository userSubjectRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public void createUserSubject(UserSubject userSubject) {
        boolean userSubjectExists = userSubjectRepository.existsByUserAndSubjectAndTeachingType(
                userSubject.getUser(),
                userSubject.getSubject(),
                userSubject.getTeachingType());

        if (!userSubjectExists) {
            userSubjectRepository.save(userSubject);
        } else {
            System.out.println("UserSubject already exists for user, subject, and teaching type combination.");
        }
    }

    @Override
    public List<UserSubject> fetchUserSubjects() {
        return userSubjectRepository.findAll();
    }
    public void updateMinMaxValues(Long userId, List<UserSubject> userSubjects) {
        for (UserSubject userSubject : userSubjects) {
            if (userSubject.getSubject() != null && userSubject.getSubject().isHasLabs()) {
                Subject subject = userSubject.getSubject();
                // Update min and max values based on requirements
                subject.setMinValue(userSubject.getSubject().getMinValue());
                subject.setMaxValue(userSubject.getSubject().getMaxValue());
                subjectRepository.save(subject);
            }
        }
        userSubjectRepository.saveAll(userSubjects);
    }
}
