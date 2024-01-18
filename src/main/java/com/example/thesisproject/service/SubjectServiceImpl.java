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

    @Override
    public Subject findSubjectBySubjectCode(String subjectCode) {
        return findSubjectBySubjectCode(subjectCode);
    }

    @Autowired
    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository= subjectRepository;
    }

    @Override
    public List<Subject> fetchSubjects() {
        return subjectRepository.findAll() ;
    }

    public void createSubject(Subject subject) {
        // Check if the subject with the same code already exists
        Subject existingSubject = subjectRepository.findBySubjectCode(subject.getSubjectCode());
        if (existingSubject == null) {
            subjectRepository.save(subject);
        } else {
            System.out.println("Subject with code " + subject.getSubjectCode() + " already exists.");
        }
    }

}
