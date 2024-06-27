package com.example.thesisproject.repository;

import com.example.thesisproject.datamodel.entity.Course;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.entity.UserCourse;
import com.example.thesisproject.datamodel.enums.TeachingType;
import jakarta.transaction.Transactional;
//import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {

    List<UserCourse> findUserCoursesByCourseId(Long courseId);

//    @Query("SELECT uc FROM UserCourse uc WHERE uc.course.id = :courseId AND uc.teachingType = :teachingType")
    List<UserCourse> findUserCoursesByCourseIdAndTeachingType(Long courseId, TeachingType teachingType);


    List<UserCourse> findUserCourseByUserId(Long userId);

    boolean existsByUserAndCourseAndTeachingType(User user, Course course, TeachingType teachingType);

    @Query("DELETE FROM UserCourse us WHERE us.course.id = :courseId")
    @Modifying
    @Transactional
    void deleteByCourseId(@Param("courseId") Long courseId);

    @Query("DELETE FROM UserCourse us WHERE us.user.id = :userId")
    @Modifying
    @Transactional
    void deleteByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(us.maxLab) FROM UserCourse us WHERE us.course.id = :courseId")
    Optional<Integer> sumMaxLabByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT SUM(us.minLab) FROM UserCourse us WHERE us.course.id = :courseId")
    Optional<Integer> sumMinLabByCourseId(@Param("courseId") Long courseId);

    UserCourse findByCourse_CourseCodeAndUser_IdAndTeachingType(String courseCode, Long userId, TeachingType teachingType);


}
