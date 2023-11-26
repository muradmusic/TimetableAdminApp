package com.example.thesisproject.repository;

import com.example.thesisproject.datamodel.entity.UserSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubjectRepository extends JpaRepository<UserSubject, Long> {

    List<UserSubject> findUserSubjectsBySubjectId(Long subjectId);

}
