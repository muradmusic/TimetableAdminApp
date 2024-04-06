package com.example.thesisproject.repository;


import com.example.thesisproject.datamodel.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_roles WHERE user_id = ?1", nativeQuery = true)
    void deleteRolesByUserId(Long userId);

}
