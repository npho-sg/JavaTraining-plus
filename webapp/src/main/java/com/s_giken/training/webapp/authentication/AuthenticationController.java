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
    public String authToken(@RequestParam UUID authid, @RequestParam String token, Model model) {

        String result = userAuthRepository.authToken(token, authid);

        if (result.equals("different_token")) {
            return "redirect:/authentication/gmailauth?tokenError&authid=" + authid;
        } else if (result.equals("limit_over")) {
            return "redirect:/authentication/gmailauth?limitError&authid=" + authid;
        } else if (result.equals("expiration_over")) {
            return "redirect:/authentication/gmailauth?timeoverError&authid=" + authid;
        }
        userAuthRepository.authCommit(authid);
        userAuthRepository.deleteNotneeded(authid);
        return "redirect:/authentication/authsuccess";
    }
}
