package com.example.thesisproject.service;


import com.example.thesisproject.datamodel.entity.Subject;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.Decision;
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
import java.util.Optional;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private SubjectRepository subjectRepository;

    private UserSubjectRepository userSubjectRepository;


    @Override
    public Subject findSubjectBySubjectCode(String subjectCode) {
        return findSubjectBySubjectCode(subjectCode);
    }

    @Autowired
    public SubjectServiceImpl(SubjectRepository subjectRepository, UserSubjectRepository userSubjectRepository) {
        this.subjectRepository= subjectRepository;
        this.userSubjectRepository = userSubjectRepository;

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
    public void deleteSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject with ID " + subjectId + " not found"));

        userSubjectRepository.deleteBySubjectId(subjectId);

        subjectRepository.delete(subject);
    }

}
