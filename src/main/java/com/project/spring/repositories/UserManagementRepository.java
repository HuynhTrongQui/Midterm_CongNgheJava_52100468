package com.project.spring.repositories;

import com.project.spring.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserManagementRepository extends JpaRepository<AppUser, Long> {
    // Các phương thức tùy chỉnh nếu cần
}