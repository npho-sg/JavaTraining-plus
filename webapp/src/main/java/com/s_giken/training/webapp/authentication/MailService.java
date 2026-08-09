package com.s_giken.training.webapp.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class MailService {

    private final JavaMailSender mailsender;

    public MailService(JavaMailSender mailsender) {
        this.mailsender = mailsender;
    }

    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendTokenMail(String gmail, String token, UUID authid) {
        String url = "http://localhost:8080/authentication/gmailauth" + "?authid=" + authid;
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(gmail);
        message.setSubject("ユーザー登録認証メール");
        message.setText("ユーザー管理システムのアカウント登録の認証メールです。\n" + "以下のURLをクリックして認証を完了してください。\n" + url);
        mailsender.send(message);
    }

}
