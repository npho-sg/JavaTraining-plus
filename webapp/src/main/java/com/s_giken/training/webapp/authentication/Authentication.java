package com.s_giken.training.webapp.authentication;

import java.security.SecureRandom;

public class Authentication {

    public static void main(String[] args) {

        String username = args[0];
        String password = args[1];
        String gmail = args[2];
        SecureRandom random = new SecureRandom();
        String token = String.format("%06d", random.nextInt(1_000_000));

        UserAuthRepository auth = SpringContextHolder.getBean(UserAuthRepository.class);
        auth.tempRegist(username, password, gmail, token);
        MailService mailservice = SpringContextHolder.getBean(MailService.class);
        mailservice.sendTokenMail(gmail, token);

    }
}