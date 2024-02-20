package com.example.thesisproject.repository;

import com.example.thesisproject.datamodel.entity.Subject;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.TeachingType;
import jakarta.transaction.Transactional;
import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubjectRepository extends JpaRepository<UserSubject, Long> {

    List<UserSubject> findUserSubjectsBySubjectId(Long subjectId);

    List<UserSubject> findUserSubjectByUserId(Long userId);

    boolean existsByUserAndSubjectAndTeachingType(User user, Subject subject, TeachingType teachingType);

    @Query("DELETE FROM UserSubject us WHERE us.subject.id = :subjectId")
    @Modifying
    @Transactional
    void deleteBySubjectId(@Param("subjectId") Long subjectId);

    @Query("SELECT SUM(us.maxLab) FROM UserSubject us WHERE us.subject.id = :subjectId")
    Optional<Integer> sumMaxLabBySubjectId(@Param("subjectId") Long subjectId);

    @Query("SELECT SUM(us.minLab) FROM UserSubject us WHERE us.subject.id = :subjectId")
    Optional<Integer> sumMinLabBySubjectId(@Param("subjectId") Long subjectId);

}
