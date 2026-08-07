package com.s_giken.training.webapp.authentication;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping("/authentication/gmailauth")
    public String gmailAuth() {
        return "authentication/gmailauth";
    }

    @PostMapping("/authentication/gmailauth")
    public void gmailAuthCommit() {

    }
}
