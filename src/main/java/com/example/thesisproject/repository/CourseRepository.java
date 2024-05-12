package com.example.thesisproject.repository;

import com.example.thesisproject.datamodel.entity.Course;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface CourseRepository extends JpaRepository<Course, Long> {

    Course findByCourseCode(String courseCode);
}
