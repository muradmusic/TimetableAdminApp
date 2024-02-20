package com.example.thesisproject.repository;

import com.example.thesisproject.datamodel.entity.Subject;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Subject findBySubjectCode(String subjectCode);
}
