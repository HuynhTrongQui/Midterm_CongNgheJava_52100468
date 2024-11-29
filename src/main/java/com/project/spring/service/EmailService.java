package com.project.spring.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    public void sendEmail(SimpleMailMessage email);
}
