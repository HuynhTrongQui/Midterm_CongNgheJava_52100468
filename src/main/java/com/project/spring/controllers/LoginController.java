package com.project.spring.controllers;

import com.project.spring.model.AppUser;
import com.project.spring.repositories.UserRepository;
import com.project.spring.security.CustomUserDetails;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import com.project.spring.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class LoginController {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/login")
    public String login(@RequestParam(name = "logout", required = false) String logout, Model model, HttpSession httpSession, @RequestParam(name = "error", required = false) String error, @RequestParam(name = "isEnable", required = false) String s, Authentication authentication) {
        if (logout != null) {
            SecurityContextHolder.clearContext();
            httpSession.invalidate();
            model.addAttribute("logoutMessage", "You have been successfully logged out.");
        }
        if (error != null) {
            model.addAttribute("errorLogin", "Fail login");
        }
        if (s != null) {
            model.addAttribute("isEnable", true);
        }
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("isLogin", null);
        return "login";
    }
}
