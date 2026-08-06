package com.s_giken.training.webapp.authentication;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserAuthRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserAuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // gmailログイン
    public boolean searchGmailuser(String gmail) {
        String sql = "SELECT EXISTS (SELECT 1 FROM T_USER WHERE gmail = ?)";
        boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, gmail);
        return result;
    }

    // gmailユーザー一時登録
    public void addToken(String username, String password, String gmail, int token) {
        String sql = "INSERT INTO t_mailuser_add(username, passwprd, gmail, token)" + "VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql, username, password, gmail, token);
    }

}
