package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.Subject;
import com.example.thesisproject.datamodel.entity.UserSubject;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.util.List;

@Service
public interface SubjectService {

    //    void initializeTestData();
    void createSubject(Subject subject);

    List<Subject> fetchSubjects();

    Subject findSubjectBySubjectCode(String subjectCode);

    void deleteSubject(Long subjectId);

}
