package com.project.spring.config;


import com.project.spring.model.AppUser;
import com.project.spring.repositories.UserRepository;
import com.project.spring.security.CustomAuthenticationSuccessHandler;
import com.project.spring.service.UserService;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import com.project.spring.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {
    @Autowired
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests(auth -> {
//                    auth.requestMatchers("/static/**").permitAll();

                    auth.requestMatchers("/css/**").permitAll();
                    auth.requestMatchers("/js/**").permitAll();
                    auth.requestMatchers("/assets/**").permitAll();
                    auth.requestMatchers("/assets2/**").permitAll();

                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/logout").permitAll();
                    auth.requestMatchers("/login?error=true").anonymous();
                    auth.requestMatchers("/register").permitAll();
                    auth.requestMatchers("/contact").permitAll();

                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/api/**").permitAll();

                    auth.requestMatchers("/cart/**").permitAll();
                    auth.requestMatchers("/products/**").permitAll();

                    auth.requestMatchers("/orders").permitAll();
                    auth.requestMatchers("/forgot").permitAll();
                    auth.requestMatchers("/reset").permitAll();

                    auth.requestMatchers("/admin").hasAnyAuthority("admin");
                    auth.requestMatchers("/admin/**").hasAnyAuthority("admin");

                    auth.requestMatchers("/upload/**").permitAll();
                    auth.anyRequest().authenticated();
                }).formLogin(formLogin -> formLogin.loginPage("/login").successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(
                                new AuthenticationFailureHandler() {
                                    @Override
                                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                                        if (exception instanceof DisabledException) {
                                            response.sendRedirect("/login?isEnable=false");
                                            return;
                                        }
                                        response.sendRedirect("/login?error=true");

                                    }
                                }
                        ))
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").clearAuthentication(true).invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll())
                .rememberMe(remmember -> remmember.key("remember-me")
                        .tokenValiditySeconds(86400))
                .exceptionHandling(handling -> handling.accessDeniedPage("/403"))
                .oauth2Login(oauth2Login -> oauth2Login.loginPage("/login").successHandler(oauth2LoginSuccessHandler()).failureHandler(oauth2LoginFailureHandler())
                        .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))).httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler("/");
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();
        simpleUrlAuthenticationFailureHandler.setDefaultFailureUrl("/login?error=true");
        return simpleUrlAuthenticationFailureHandler;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler oauth2LoginFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/login?error=true");
    }

    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    UserRepository userRepository;

    @Bean
    public AuthenticationSuccessHandler oauth2LoginSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler("/") {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                String email = oAuth2User.getAttribute("email");
                String name = oAuth2User.getAttribute("name");
                UserDetails userDetails = userDetailsService.loadUserByEmail(email, name);
                Authentication newAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }
}
