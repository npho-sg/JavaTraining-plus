package com.s_giken.training.webapp.authentication;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.UUID;

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
    public UUID tempRegist(String username, String password, String gmail, String token) {
        String sql = "INSERT INTO t_gmailuser_add(username, password, gmail, token)"
                + "VALUES(?, ?, ?, ?) RETURNING auth_id";
        UUID authid = jdbcTemplate.queryForObject(sql, UUID.class, username, password, gmail, token);
        return authid;
    }

    // 登録時トークン確認
    public boolean authToken(String token, UUID authid) {
        String sql = "SELECT EXISTS(SELECT 1 FROM t_gmailuser_add WHERE token = ? AND token = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, token, authid);
    }

    public void authCommit(UUID authid) {
        String sql = "SET app.gmail_update = 'ON';" + "INSERT INTO t_user (username, password, enable, gmail)"
                + "SELECT username, password, TRUE, gmail" + "FROM t_gmailuser_add WHERE authid = ?";
        jdbcTemplate.update(sql, authid);

    }

}
