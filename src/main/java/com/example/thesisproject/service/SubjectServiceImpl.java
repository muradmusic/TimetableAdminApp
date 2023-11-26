package com.example.thesisproject.service;


import com.example.thesisproject.datamodel.entity.Subject;
import com.example.thesisproject.repository.SubjectRepository;
import com.example.thesisproject.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private SubjectRepository subjectRepository;

    @Autowired
    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository= subjectRepository;
    }

    @Override
    public List<Subject> fetchSubjects() {
        return subjectRepository.findAll() ;
    }

    @Override
    public void createSubject(Subject subject) {
         subjectRepository.save(subject);
    }
    //    @Override
//    @PostConstruct
//    public void initializeTestData() {
//
//        Subject subject = new Subject("BIE-PA1");
//        Subject subject1 = new Subject("BIE-TJV");
//
//
//        subjectRepository.save(subject);
//        subjectRepository.save(subject1);
//
//
//
//    }
}
