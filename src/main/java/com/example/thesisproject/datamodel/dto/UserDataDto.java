package com.example.thesisproject.datamodel.dto;


import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.TeachingType;

import java.util.List;
import java.util.Map;

public class UserDataDto {
    public final User user;
    public final Long userId;
    public final List<Course> courses;
    public final List<UserCourse> userCourses;
    public final List<TeachingType> allTeachingTypes;
    public final Map<String, Map<String, Boolean>> coursesMap;

    public UserDataDto(User user, Long userId, List<Course> courses, List<UserCourse> userCourses, List<TeachingType> allTeachingTypes, Map<String, Map<String, Boolean>> coursesMap) {
        this.user = user;
        this.userId = userId;
        this.courses = courses;
        this.userCourses = userCourses;
        this.allTeachingTypes = allTeachingTypes;
        this.coursesMap = coursesMap;
    }
}
