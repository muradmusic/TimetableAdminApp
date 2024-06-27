package com.example.thesisproject.service;


import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.TeachingType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserCourseService {

    int sumMaxLabByCourseId(Long courseId);

    int sumMinLabByCourseId(Long courseId);

    boolean existsUserCourse(Long userCourseId);

    void deleteUserCourseById(Long userCourseId);

    void deleteUserCourse(UserCourse userCourse);

    UserCourse getUserCourseById(Long userCourseId);

    UserCourse saveUserCourse(UserCourse userCourse);

    List<UserCourse> getUserCoursesByCourseId(Long courseId);

    List<UserCourse> fetchUserCourses();

    boolean existsByUserAndCourseAndTeachingType(User user, Course course, TeachingType teachingType);


}


