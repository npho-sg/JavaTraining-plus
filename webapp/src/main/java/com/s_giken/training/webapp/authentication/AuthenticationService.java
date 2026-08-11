package com.s_giken.training.webapp.authentication;

import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserAuthRepository auth;
    private final MailService mailService;

    public AuthenticationService(UserAuthRepository auth, MailService mailService) {
        this.auth = auth;
        this.mailService = mailService;
    }

    public String excute(String username, String password, String gmail) {

        SecureRandom random = new SecureRandom();
        String token = String.format("%06d", random.nextInt(1_000_000));
        UUID authid = auth.tempRegist(username, password, gmail, token);
        mailService.sendTokenMail(gmail, token, authid);
        return token;
    }

}