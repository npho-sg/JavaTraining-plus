package com.s_giken.training.webapp.authentication;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserAuthRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserAuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean searchGmailuser(String gmail) {
        String sql = "SELECT EXISTS (SELECT 1 FROM T_USER WHERE gmail = ?)";
        boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, gmail);
        return result;
    }

}
