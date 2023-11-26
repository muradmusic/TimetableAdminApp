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
    private UserRepository userRepository;

    @Autowired

    private SubjectRepository subjectRepository;

    @Override
    public List<UserSubject> fetchUserSubjects() {
        return userSubjectRepository.findAll();
    }
}
