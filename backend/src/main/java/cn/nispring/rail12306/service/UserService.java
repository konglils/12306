package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.UserEntity;
import cn.nispring.rail12306.exception.BusinessException;
import cn.nispring.rail12306.mapper.UserMapper;
import cn.nispring.rail12306.model.SessionUser;
import cn.nispring.rail12306.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    private final UserMapper userMapper;

    /// 当前登录的用户
    private final ConcurrentHashMap<String, User> sessionMap = new ConcurrentHashMap<>();

    public UserService(UserMapper userMapper) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userMapper = userMapper;
    }

    @PostConstruct
    public void loadSessions() {
        for (UserEntity entity : userMapper.selectSessionTokens()) {
            if (entity.getSessionToken() != null) {
                sessionMap.put(entity.getSessionToken(), new User(entity.getId(), entity.getUsername()));
            }
        }
    }

    public User signup(String username, String password) {
        if (userMapper.selectByUsername(username) != null) {
            throw new BusinessException(HttpStatus.CONFLICT, "用户名已存在");
        }

        String encoded = passwordEncoder.encode(password);
        UserEntity entity = new UserEntity(null, username, encoded, null, null, null);
        userMapper.insert(entity);
        return new User(entity.getId(), entity.getUsername());
    }

    public SessionUser signin(String username, String password) {
        UserEntity entity = userMapper.selectByUsername(username);
        if (entity == null || !passwordEncoder.matches(password, entity.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        String token = generateSessionToken();

        userMapper.updateSessionToken(entity.getId(), token);
        if (entity.getSessionToken() != null) {
            sessionMap.remove(entity.getSessionToken());
        }
        sessionMap.put(token, new User(entity.getId(), entity.getUsername()));
        return new SessionUser(entity.getId(), entity.getUsername(), token);
    }

    public void signout(String sessionToken) {
        User user = getUser(sessionToken);
        if (user == null) {
            return;
        }
        userMapper.updateSessionToken(user.id(), null);
        sessionMap.remove(sessionToken);
    }

    public User getUser(String sessionToken) {
        if (sessionToken == null) {
            return null;
        } else {
            return sessionMap.getOrDefault(sessionToken, null);
        }
    }

    private String generateSessionToken() {
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }
}
