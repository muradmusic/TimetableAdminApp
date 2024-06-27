package com.example.thesisproject.datamodel.dto;

import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.TeachingType;

import java.util.List;
import java.util.Map;

public class CourseDataDto {
    public final List<User> users;
    public final Course course;
    public final List<TeachingType> allTeachingTypes;
    public final Map<String, Map<String, Boolean>> usersMap;
    public final List<UserCourse> userCourses;
    public final int currentLectures;
    public final int currentSeminars;
    public final int currentLabs;
    public final Map<Long, Integer> currentLabSumsMax;
    public final Map<Long, Integer> currentLabSumsMin;

    public CourseDataDto(List<User> users, Course course, List<TeachingType> allTeachingTypes, Map<String, Map<String, Boolean>> usersMap, List<UserCourse> userCourses, int currentLectures, int currentSeminars, int currentLabs, Map<Long, Integer> currentLabSumsMax, Map<Long, Integer> currentLabSumsMin) {
        this.users = users;
        this.course = course;
        this.allTeachingTypes = allTeachingTypes;
        this.usersMap = usersMap;
        this.userCourses = userCourses;
        this.currentLectures = currentLectures;
        this.currentSeminars = currentSeminars;
        this.currentLabs = currentLabs;
        this.currentLabSumsMax = currentLabSumsMax;
        this.currentLabSumsMin = currentLabSumsMin;
    }
}