package com.s_giken.training.webapp.authentication;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class DeleteCashSchedule {

    private final UserAuthRepository userAuthRepository;

    public DeleteCashSchedule(UserAuthRepository auth) {
        this.userAuthRepository = auth;
    }

    @Scheduled(initialDelay = 360000)
    public void excute() {
        userAuthRepository.deleteCashrecord();
    }

}