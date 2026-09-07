package com.s_giken.training.webapp.authentication;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;

@Controller
public class AuthenticationController {

    private final UserAuthRepository userAuthRepository;
    private final AuthenticationService authenticationService;

    public AuthenticationController(
            UserAuthRepository userAuthRepository,
            AuthenticationService authenticationService) {
        this.userAuthRepository = userAuthRepository;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/userauth/google")
    public String googleLogin(@AuthenticationPrincipal OAuth2User oauthUser, HttpServletRequest request) {

        String gmail = oauthUser.getAttribute("email");

        if (!userAuthRepository.searchGmailuser(gmail)) {
            SecurityContextHolder.clearContext();
            request.getSession().invalidate();

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
    public String authToken(@RequestParam UUID authid, @RequestParam String token) {

        String result = userAuthRepository.authToken(token, authid);

        if (result.equals("timeout_or_notfound")) {
            return "redirect:/authentication/gmailauth?timeoutORnotfound&authid=" + authid;
        } else if (result.equals("different_token")) {
            return "redirect:/authentication/gmailauth?tokenError&authid=" + authid;
        } else if (result.equals("limit_over")) {
            return "redirect:/authentication/gmailauth?limitError&authid=" + authid;
        } else if (result.equals("expiration_out")) {
            return "redirect:/authentication/gmailauth?timeoverError&authid=" + authid;
        }
        userAuthRepository.authCommit(authid);
        userAuthRepository.deleteNotneeded(authid);
        return "authentication/authsuccess";
    }

    // ユーザー作成時の入口
    @Value("${app.password}")
    private String propertiesPass;
    @PostMapping("/inter/auth")
    @ResponseBody
    public String auth(HttpServletRequest request,
            @RequestParam String appPass,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String gmail) {
        System.out.println("######## INTER AUTH START ########");
        String ip = request.getRemoteAddr();
        if (!ip.equals("127.0.0.1") && !ip.equals("::1") && !ip.equals("0:0:0:0:0:0:0:1")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (!propertiesPass.equals(appPass)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        String token = authenticationService.excute(username, password, gmail);
        return token;

    }
}