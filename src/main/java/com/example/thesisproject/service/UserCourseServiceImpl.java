package com.example.thesisproject.service;


import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.TeachingType;
import com.example.thesisproject.repository.CourseRepository;
import com.example.thesisproject.repository.UserCourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserCourseServiceImpl implements UserCourseService {

    @Autowired
    private UserCourseRepository userCourseRepository;


    @Override
    public int sumMaxLabByCourseId(Long courseId) {
        return userCourseRepository.sumMaxLabByCourseId(courseId).orElse(0);
    }

    @Override
    public int sumMinLabByCourseId(Long courseId) {
        return userCourseRepository.sumMinLabByCourseId(courseId).orElse(0);
    }

    @Override
    public List<UserCourse> getUserCoursesByCourseId(Long courseId) {
        return userCourseRepository.findUserCoursesByCourseId(courseId);
    }

    @Override
    public void deleteUserCourseById(Long userCourseId) {
        if (existsUserCourse(userCourseId)) {
            userCourseRepository.deleteById(userCourseId);
        }
    }
    @Override
    public boolean existsUserCourse(Long userCourseId) {
        return userCourseRepository.existsById(userCourseId);
    }

    @Override
    public void deleteUserCourseByUserId(Long userId) {
        userCourseRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteUserCourseByCourseId(Long courseId) {
        userCourseRepository.deleteByCourseId(courseId);
    }

    @Override
    public void deleteUserCourse(UserCourse userCourse) {
        userCourseRepository.delete(userCourse);
    }

    @Override
    public boolean existsByUserAndCourseAndTeachingType(User user, Course course, TeachingType teachingType) {
        return userCourseRepository.existsByUserAndCourseAndTeachingType(user, course, teachingType);
    }

    @Override
    public UserCourse getUserCourseById(Long userCourseId) {
        return userCourseRepository.findById(userCourseId)
                .orElseThrow(() -> new EntityNotFoundException("UserCourse not found with id: " + userCourseId));
    }

    @Override
    public UserCourse saveUserCourse(UserCourse userCourse) {
        return userCourseRepository.save(userCourse);
    }

    @Override
    public List<UserCourse> getUserCoursesByUserId(Long userId) {
        return userCourseRepository.findUserCourseByUserId(userId);
    }


    public void createUserCourse(UserCourse userCourse) {
        boolean userCourseExists = userCourseRepository.existsByUserAndCourseAndTeachingType(
                userCourse.getUser(),
                userCourse.getCourse(),
                userCourse.getTeachingType());

        if (!userCourseExists) {
            userCourseRepository.save(userCourse);
        } else {
            System.out.println("UserCourse already exists for user, course, and teaching type combination.");
        }
    }

    @Override
    public List<UserCourse> fetchUserCourses() {
        return userCourseRepository.findAll();
    }
    public void updateLabValuesForSuitableCourses(List<UserCourse> userCourses, int minLab, int maxLab) {
        for (UserCourse userCourse : userCourses) {
            if (userCourse.getTeachingType() == TeachingType.LAB) {
                // Fetch the UserCourse entity by its ID
                Optional<UserCourse> optionalUserCourse = userCourseRepository.findById(userCourse.getId());
                if (optionalUserCourse.isPresent()) {
                    UserCourse userCourseToUpdate = optionalUserCourse.get();
                    // Update the minLab and maxLab values
                    userCourseToUpdate.setMinLab(minLab);
                    userCourseToUpdate.setMaxLab(maxLab);
                    // Save the updated UserCourse entity
                    userCourseRepository.save(userCourseToUpdate);
                }
            }
        }
    }

}
