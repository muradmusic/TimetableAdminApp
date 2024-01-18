package com.example.thesisproject.repository;

import com.example.thesisproject.datamodel.entity.Subject;
import com.example.thesisproject.datamodel.entity.User;
import com.example.thesisproject.datamodel.entity.UserSubject;
import com.example.thesisproject.datamodel.enums.TeachingType;
import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubjectRepository extends JpaRepository<UserSubject, Long> {

    List<UserSubject> findUserSubjectsBySubjectId(Long subjectId);

    List<UserSubject> findUserSubjectByUserId(Long userId);

    boolean existsByUserAndSubjectAndTeachingType(User user, Subject subject, TeachingType teachingType);


}
