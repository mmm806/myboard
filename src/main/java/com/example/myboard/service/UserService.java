package com.example.myboard.service;

import com.example.myboard.dao.UserDao;
import com.example.myboard.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // lombokdl final 필드를 초기화하는 생성자를 자동으로 생성한다.
public class UserService {
    private final UserDao userDao;


    @Transactional // 보통 서비스에서는 이걸 붙여서 하나의 트랜잭션으로 처리한다.
    public User addUser(String name, String email, String password) {
        User user1 = userDao.getUser(email);
        if(user1 != null) {
            throw new RuntimeException("이미 가입된 이메일 입니다.");
        }
        User user = userDao.addUser(email, name, password);
        userDao.mappingUserRole(user.getUserId());
        return user;

    }


    @Transactional
    public User getUser(String email) {
        return userDao.getUser(email);
    }


    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        return userDao.getRoles(userId);
    }
}
