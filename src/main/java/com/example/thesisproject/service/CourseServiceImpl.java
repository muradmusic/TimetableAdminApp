package com.example.thesisproject.service;


import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.repository.CourseRepository;
import com.example.thesisproject.repository.UserCourseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;

    private UserCourseRepository userCourseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, UserCourseRepository userCourseRepository) {
        this.courseRepository= courseRepository;
        this.userCourseRepository = userCourseRepository;

    }


    @Override
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
    }

    @Override
    public Course findCourseByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }


    @Override
    public List<Course> fetchCourses() {
//        return courseRepository.findAll() ;
        return courseRepository.findAll(Sort.by(Sort.Direction.ASC, "courseCode"));

    }

    public boolean createCourse(Course course) {
        Course existingCourse = courseRepository.findByCourseCode(course.getCourseCode());
        if (existingCourse == null) {
            courseRepository.save(course);
            return true;
        } else {
            System.out.println("Course with code " + course.getCourseCode() + " already exists.");
            return false;
        }
    }

    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with ID " + courseId + " not found"));

        userCourseRepository.deleteByCourseId(courseId);

        courseRepository.delete(course);
    }
    public boolean updateCourse(Course course) {
        Course existingCourse = courseRepository.findByCourseCode(course.getCourseCode());
        if (existingCourse != null && !existingCourse.getId().equals(course.getId())) {
            return false; // Course code already exists for a different course
        }
        courseRepository.save(course);
        return true;
    }






}
