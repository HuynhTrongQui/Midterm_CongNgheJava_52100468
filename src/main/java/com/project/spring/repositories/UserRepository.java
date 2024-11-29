package com.project.spring.repositories;

import com.project.spring.model.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    @Query("SELECT u FROM AppUser u WHERE u.username = :username or  u.email = : username")
    AppUser getUserByUsername(@Param("username") String username);
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByResetToken(String resetToken);
    Optional<AppUser> findByUsername(String name);
    @Modifying
    @Query("update AppUser u set u.isEnable =  CASE WHEN u.isEnable = true THEN false ELSE true END where u.id = :id")
    @Transactional
    void blocked(@Param("id") Long id);

}
