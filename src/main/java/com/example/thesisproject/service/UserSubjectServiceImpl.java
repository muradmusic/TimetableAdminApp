package com.example.thesisproject.service;


import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.SubjectRepository;
import com.example.thesisproject.repository.UserSubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public void updateLabValuesForSuitableSubjects(List<UserSubject> userSubjects, int minLab, int maxLab) {
        for (UserSubject userSubject : userSubjects) {
            if (userSubject.getTeachingType() == TeachingType.LAB) {
                // Fetch the UserSubject entity by its ID
                Optional<UserSubject> optionalUserSubject = userSubjectRepository.findById(userSubject.getId());
                if (optionalUserSubject.isPresent()) {
                    UserSubject userSubjectToUpdate = optionalUserSubject.get();
                    // Update the minLab and maxLab values
                    userSubjectToUpdate.setMinLab(minLab);
                    userSubjectToUpdate.setMaxLab(maxLab);
                    // Save the updated UserSubject entity
                    userSubjectRepository.save(userSubjectToUpdate);
                }
            }
        }
    }

}
