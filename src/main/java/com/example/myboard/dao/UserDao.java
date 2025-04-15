package com.example.myboard.dao;

import com.example.myboard.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private SimpleJdbcInsertOperations insertUser;

    public UserDao(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("\"user\"")
                .usingGeneratedKeyColumns("user_id")
                .usingColumns("email", "name", "password", "regdate");
    }

    @Transactional
    public User addUser(String email, String name, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRegdate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(user);
        Number number = insertUser.executeAndReturnKey(params); // 이 메소드를 실행해야 userId를 자동으로 생성할수 있다.
        int userId = number.intValue();
        user.setUserId(userId);
        return user;
    }

    @Transactional
    public void mappingUserRole(int userId) {
        String sql = "insert into user_role(user_id, role_id) values (:userId, 1)";
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        jdbcTemplate.update(sql, params);
    }

    @Transactional
    public User getUser(String email) {
        try{
        String sql = "select user_id, email, name, password, regdate from \"user\" where email = :email";
        SqlParameterSource params = new MapSqlParameterSource("email", email);
        RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
        User user = jdbcTemplate.queryForObject(sql, params, rowMapper);
        return user;
        } catch (Exception ex) {
            return null;
        }
    }


    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        String sql = "SELECT r.name " +
                "FROM user_role ur, role r " +
                "WHERE ur.role_id = r.role_id AND ur.user_id = :userId";
        List<String> roles = jdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) -> {
            return rs.getString(1);
        });
    return roles;
    }
}