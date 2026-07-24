package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.UserEntity;
import cn.nispring.rail12306.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();
    private final ConcurrentHashMap<String, Long> sessionMap = new ConcurrentHashMap<>();

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void loadSessions() {
        for (UserEntity entity : userMapper.selectSessionTokens()) {
            if (entity.getSessionToken() != null) {
                sessionMap.put(entity.getSessionToken(), entity.getId());
            }
        }
    }

    public UserEntity signup(String username, String password) {
        if (userMapper.selectByUsername(username) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        String encoded = passwordEncoder.encode(password);
        UserEntity entity = new UserEntity(null, username, encoded, null, null, null);
        userMapper.insert(entity);
        return entity;
    }

    public UserEntity signin(String username, String password) {
        UserEntity entity = userMapper.selectByUsername(username);
        if (entity == null || !passwordEncoder.matches(password, entity.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        String token = generateSessionToken();
        updateSessionToken(null, token, entity.getId());
        entity.setSessionToken(token);
        return entity;
    }

    public void signout(String sessionToken) {
        long id = getLoginId(sessionToken);
        updateSessionToken(sessionToken, null, id);
    }

    private String generateSessionToken() {
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    private long getLoginId(String sessionToken) {
        Long id = sessionMap.get(sessionToken);
        if (id == null) {
            throw new IllegalArgumentException("用户未登录");
        } else {
            return id;
        }
    }

    private void updateSessionToken(String old, String neu, long id) {
        if (old != null) {
            sessionMap.remove(old);
        }
        if (neu != null) {
            sessionMap.put(neu, id);
        }
        userMapper.updateSessionToken(id, neu);
    }
}
