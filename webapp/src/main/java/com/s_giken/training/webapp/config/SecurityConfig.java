package com.s_giken.training.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * Spring Securityの設定クラス
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
        /**
         * Spring Securityの設定
         *
         * @param http HttpSecurityオブジェクト
         * @return SecurityFilterChainオブジェクト
         * @throws Exception 例外全般
         */
        @Bean
        SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(Customizer.withDefaults())
                                .headers((header) -> header.frameOptions((frame) -> frame.sameOrigin()))
                                .formLogin((form) -> form.loginPage("/login").permitAll())
                                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/userauth/google", true))
                                .logout(LogoutConfigurer::permitAll)
                                .authorizeHttpRequests((authorize) -> authorize
                                                // 特例として認証を無視するURL
                                                .requestMatchers(
                                                                PathPatternRequestMatcher
                                                                                .withDefaults()
                                                                                .matcher("/.well-known/**"),
                                                                PathPatternRequestMatcher
                                                                                .withDefaults()
                                                                                .matcher("/image/**"),
                                                                PathPatternRequestMatcher
                                                                                .withDefaults()
                                                                                .matcher("/css/**"))
                                                .permitAll()
                                                // 特例以外のURLは要認証
                                                .anyRequest()
                                                .authenticated());

                return http.build();
        }

        /**
         * パスワードをArgon2idでハッシュ化するオブジェクトを生成する
         *
         * @return パスワードをハッシュ化するエンコーダーのオブジェクト
         */
        @Bean
        PasswordEncoder passwordEncoder() {
                return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        }
}
