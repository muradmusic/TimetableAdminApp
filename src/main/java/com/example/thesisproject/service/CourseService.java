package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.entity.Course;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseService {
    Course saveCourse(Course course);
    Course getCourseById(Long courseId);
    boolean createCourse(Course course);
    List<Course> fetchCourses();
    Course findCourseByCourseCode(String courseCode);
    void deleteCourse(Long courseId);
    boolean updateCourse(Course course);
}


