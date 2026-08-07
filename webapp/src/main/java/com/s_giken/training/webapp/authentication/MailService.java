package com.s_giken.training.webapp.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailsender;

    public MailService(JavaMailSender mailsender) {
        this.mailsender = mailsender;
    }

    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendTokenMail(String gmail, String token, String authid) {
        String url = "http://localhost:8080/authentication/gmailauth" + "?authid=" + authid;
        SimpleMailMessage message = new SimpleMailMessage();

    }

}
