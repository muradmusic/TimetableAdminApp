package com.example.thesisproject.service;

import com.example.thesisproject.datamodel.dto.CourseDataDto;
import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.TeachingType;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Service
public interface CourseService {
    Course getCourseById(Long courseId);

    boolean createCourse(Course course);

    List<Course> fetchCourses();

    void deleteCourse(Long courseId);

    boolean updateCourse(Course course);

    void updateCourseCoveredStatus(Long courseId);

    void updateLabs(Long courseId, Map<String, String> allParams);

    void changeDecision(Long courseId, Map<String, String[]> parameters);

    boolean addUserCourse(Long courseId, Long userId, TeachingType teachingType, UserCourse userCourse, BindingResult result, RedirectAttributes redirectAttributes);

    void updateCourses(Long courseId, Map<String, String[]> parameters) throws Exception;

    CourseDataDto prepareCoursePageData(Long courseId);

}


