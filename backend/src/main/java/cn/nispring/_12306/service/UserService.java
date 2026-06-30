package cn.nispring._12306.service;

import cn.nispring._12306.entity.UserEntity;
import cn.nispring._12306.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9_]{6,30}$");

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserMapper userMapper, BCryptPasswordEncoder encoder) {
        this.userMapper = userMapper;
        this.encoder = encoder;
    }

    public void signup(String username, String password) {
        if (!PATTERN.matcher(username).matches() || !PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Invalid username or password format");
        }
        if (userMapper.selectByUsername(username) != null) {
            throw new DuplicateUserException("Username already exists");
        }
        var hashed = encoder.encode(password);
        userMapper.insert(new UserEntity(null, username, hashed));
    }

    public UserEntity signin(String username, String password) {
        if (!PATTERN.matcher(username).matches() || !PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Invalid username or password format");
        }
        var user = userMapper.selectByUsername(username);
        if (user == null || !encoder.matches(password, user.password())) {
            throw new BadCredentialsException("Bad credentials");
        }
        return user;
    }

    public UserEntity findById(Long id) {
        return userMapper.selectById(id);
    }

    public static class DuplicateUserException extends RuntimeException {
        public DuplicateUserException(String message) { super(message); }
    }

    public static class BadCredentialsException extends RuntimeException {
        public BadCredentialsException(String message) { super(message); }
    }
}
