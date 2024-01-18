package com.example.thesisproject.service;


import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.repository.UserSubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserSubjectService {

    List<UserSubject> fetchUserSubjects() ;

    void createUserSubject(UserSubject userSubject);
}
