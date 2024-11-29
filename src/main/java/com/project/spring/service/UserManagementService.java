package com.project.spring.service;

import com.project.spring.model.AppUser;
import com.project.spring.model.Role;
import com.project.spring.repositories.UserManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserManagementService {
    private final UserManagementRepository userRepository;

    @Autowired
    public UserManagementService(UserManagementRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<AppUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public AppUser saveUser(AppUser user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void saveUserWithRoles(AppUser user, List<Role> roles){
//        user.setRoles(roles);
        userRepository.save(user);
    };

}