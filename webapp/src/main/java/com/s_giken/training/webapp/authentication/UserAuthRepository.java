package com.s_giken.training.webapp.authentication;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

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
    public UUID tempRegist(String username, String password, String gmail, String token) {
        String sql = "INSERT INTO t_gmailuser_add(username, password, gmail, token)"
                + "VALUES(?, ?, ?, ?) RETURNING auth_id";
        UUID authid = jdbcTemplate.queryForObject(sql, UUID.class, username, password, gmail, token);
        return authid;
    }

    // 登録時トークン確認
    public String authToken(String token, UUID authid) {

        SecureRandom random = new SecureRandom();
        String newtoken;

        String sql = "SELECT token, expiration FROM t_gmailuser_add WHERE authid = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, authid);
        String dbtoken = (String) result.get("token");
        LocalDateTime expiration = ((Timestamp) result.get("expiration")).toLocalDateTime();

        if (expiration.isBefore(LocalDateTime.now())) {
            newtoken = String.format("%06d", random.nextInt(1_000_000));
            sql = "UPDATE t_gmailuser_add SET token = ?, expiration = ?, limitcount = 0 WHERE authid = ?";
            jdbcTemplate.update(sql, newtoken, Timestamp.valueOf(LocalDateTime.now().plusMinutes(5)), authid);
            return "expiration_out";
        }

        if (!dbtoken.equals(token)) {
            sql = "SELECT limitcount FROM t_gmailuser_add WHERE authid = ?";
            Integer limit = jdbcTemplate.queryForObject(sql, Integer.class, authid);
            if (limit < 2) {
                sql = "UPDATE t_gmailuser_add SET limitcount = limitcount + 1 WHERE authid = ?";
                jdbcTemplate.update(sql, authid);
                return "different_token";
            }
            newtoken = String.format("%06d", random.nextInt(1_000_000));
            sql = "UPDATE t_gmailuser_add SET token = ?, expiration = ?, limitcount = 0 WHERE authid = ?";
            jdbcTemplate.update(sql, newtoken, Timestamp.valueOf(LocalDateTime.now().plusMinutes(5)), authid);
            return "limit_over";
        }

        return "success";
    }

    public void authCommit(UUID authid) {
        String sql = "SET app.gmail_update = 'ON';" + "INSERT INTO t_user (username, password, enable, gmail)"
                + "SELECT username, password, TRUE, gmail" + "FROM t_gmailuser_add WHERE authid = ?";
        jdbcTemplate.update(sql, authid);
    }

    public void deleteNotneeded(UUID authid) {
        String sql = "DELETE FROM t_gmailuser_add WHERE authid = ?";
        jdbcTemplate.update(sql, authid);
    }

}
