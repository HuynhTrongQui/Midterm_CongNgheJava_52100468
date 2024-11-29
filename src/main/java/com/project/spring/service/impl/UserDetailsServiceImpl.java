package com.project.spring.service.impl;

import com.project.spring.exceptions.UserEnableException;
import com.project.spring.model.AppUser;
import com.project.spring.model.Role;
import com.project.spring.repositories.UserRepository;
import com.project.spring.security.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.getUserByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("user not found");
        }
        CustomUserDetails userDetails = new CustomUserDetails(appUser);
        return userDetails;
    }
    
    public UserDetails loadUserByEmail(String email, String name) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByEmail(email).orElse(null);
        if (appUser == null) {
            /* create new user*/
            AppUser user = new AppUser();
            user.setEmail(email);
            user.setName(name);
            List<GrantedAuthority> grantedAuthorityList = new ArrayList<GrantedAuthority>();
            GrantedAuthority authority = new SimpleGrantedAuthority("user");
            grantedAuthorityList.add(authority);
            this.userRepository.save(user);
            return new User(email, "", grantedAuthorityList);
        }
        List<Role> roles = appUser.getRoles();
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<GrantedAuthority>();
        if (roles != null) {
            for (Role role : roles) {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getName());
                grantedAuthorityList.add(grantedAuthority);
            }
        }
        return new User(appUser.getEmail(), "", grantedAuthorityList);
    }


    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                return ((UserDetails) authentication.getPrincipal()).getUsername();
            }
        }
        return null;
    }
}
