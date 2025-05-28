package org.example.minichatjavaweb.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.minichatjavaweb.entities.User;

import java.sql.SQLException;

@Slf4j
public class UserRepository extends BaseRepository {

    public static final String KEY = "repository.user";

    public User findByUsername(String username) {
        String sql = "select id, username, password, salt from user where username = ?";
        try {
            return executeQueryOne(sql, User.class, username);
        } catch (SQLException e) {
            log.error("no user username: {}", username, e);
            return null;
        }
    }
}
