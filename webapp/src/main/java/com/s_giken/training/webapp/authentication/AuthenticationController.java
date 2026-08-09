package com.s_giken.training.webapp.authentication;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;
import org.springframework.ui.Model;

@Controller
public class AuthenticationController {

    private final UserAuthRepository userAuthRepository;

    public AuthenticationController(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }

    @GetMapping("/userauth/google")
    public String googleLogin(@AuthenticationPrincipal OAuth2User oauthUser) {

        String gmail = oauthUser.getAttribute("email");

        if (!userAuthRepository.searchGmailuser(gmail)) {
            return "redirect:/login?googleError";
        }

        return "redirect:/";
    }

    // トークン認証ページに遷移
    @GetMapping("/authentication/gmailauth")
    public String gmailAuth(@RequestParam UUID authid, Model model) {
        model.addAttribute("authid", authid);
        return "authentication/gmailauth";
    }

    // トークン認証ページからの受取
    @PostMapping("/authentication/gmailauth")
    public void authToken(@RequestParam UUID authid, @RequestParam String token) {
        if (!userAuthRepository.authToken(token, authid)) {
            // エラー
        }
        userAuthRepository.authCommit(authid);
    }
}
