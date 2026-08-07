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
    public void tempRegist(String username, String password, String gmail, String token) {
        String sql = "INSERT INTO t_gmailuser_add(username, password, gmail, token)"
                + "VALUES(?, ?, ?, ?) RETURNING auth_id";
        jdbcTemplate.update(sql, username, password, gmail, token);
    }

    // 登録時トークン確認
    public boolean compareToken(String gmail, String token, String uuid){
        String sql = "SELECT gmail, token, expiration, limitcount FROM t_mailuser_add WHERE gmail = ? AND token = ? AND = ? ";
        jdbcTemplate.

    }

}
