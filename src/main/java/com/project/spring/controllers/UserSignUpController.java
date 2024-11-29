package com.project.spring.controllers;


import java.util.*;

import com.project.spring.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


import com.project.spring.model.Role;
import com.project.spring.model.AppUser;
import com.project.spring.repositories.UserRepository;


@Controller
public class UserSignUpController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    public UserSignUpController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String getSignUpForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "register";
    }

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/register")
    public String signUpAcc(@ModelAttribute("user") AppUser userInf, Model model) {
        Optional<AppUser> emails = userRepository.findByEmail(userInf.getEmail());
        if (emails.isPresent()) {
            model.addAttribute("emailExists", true);
            return "register";
        }
        Optional<AppUser> username = userRepository.findByUsername(userInf.getUsername());
        if (username.isPresent()) {
            model.addAttribute("nameExists", true);
            return "register";
        }

        String encodedPassword = passwordEncoder.encode(userInf.getPassword());
        userInf.setPassword(encodedPassword);

        List<Role> roles = new ArrayList<Role>();

        Role role = this.roleRepository.findByName("user");
        roles.add(role);
        userInf.setRoles(roles);
        userInf.setEnable(true);

        userRepository.save(userInf);
        return "redirect:/login";
    }

}
