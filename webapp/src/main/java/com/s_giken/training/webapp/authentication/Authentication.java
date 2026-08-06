package com.s_giken.training.webapp.authentication;

import java.security.SecureRandom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Authentication {

    public static void main(String[] args) {

        String username = args[0];
        String password = args[1];
        String gmail = args[2];
        SecureRandom random = new SecureRandom();
        int token = random.nextInt(1_000_000);

        ApplicationContext context = SpringApplication.run(Authentication.class, args);
        UserAuthRepository repository = context.getBean(UserAuthRepository.class);
        repository.addToken(username, password, gmail, token);

    }

}
