package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.PassengerEntity;
import cn.nispring.rail12306.entity.UserEntity;
import cn.nispring.rail12306.exception.BusinessException;
import cn.nispring.rail12306.mapper.PassengerMapper;
import cn.nispring.rail12306.mapper.UserMapper;
import cn.nispring.rail12306.model.SessionUser;
import cn.nispring.rail12306.model.SignUp;
import cn.nispring.rail12306.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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
    private final PassengerMapper passengerMapper;
    private final PassengerService passengerService;
    private final TransactionTemplate transactionTemplate;
    private final IdService idService;

    public UserService(UserMapper userMapper, PassengerMapper passengerMapper, PassengerService passengerService,
                       TransactionTemplate transactionTemplate, IdService idService) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userMapper = userMapper;
        this.passengerMapper = passengerMapper;
        this.passengerService = passengerService;
        this.transactionTemplate = transactionTemplate;
        this.idService = idService;
    }

    @PostConstruct
    public void loadSessions() {
        for (UserEntity entity : userMapper.selectSessionTokens()) {
            if (entity.getSessionToken() != null) {
                sessionMap.put(entity.getSessionToken(), new User(entity.getId(), entity.getUsername()));
            }
        }
    }

    public User signup(SignUp request) {
        if (!isUsernameOk(request.username())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户名格式错误");
        }
        if (!isPasswordOk(request.password())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "密码格式错误");
        }

        String encoded = passwordEncoder.encode(request.password());
        UserEntity user = new UserEntity(null, request.username(), encoded, null, null, null);

        PassengerEntity passenger = passengerService.makeEntity(request.passenger());
        passenger.setIsUser(true);

        transactionTemplate.executeWithoutResult(status -> {
            if (passengerMapper.existsUserById(request.passenger().idType(), request.passenger().idNo())) {
                throw new BusinessException(HttpStatus.CONFLICT, "其他用户已使用该身份注册");
            }
            try {
                userMapper.insert(user);
            } catch (DuplicateKeyException e) {
                throw new BusinessException(HttpStatus.CONFLICT, "用户名已存在");
            }
            passenger.setUserId(user.getId());
            passengerMapper.insert(passenger);
        });

        idService.verify(user.getId(), passenger.getIdType(), passenger.getIdNo());

        return new User(user.getId(), user.getUsername());
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

    private boolean isUsernameOk(String username) {
        return username.matches("^[A-Za-z0-9_]{6,30}$");
    }

    private boolean isPasswordOk(String password) {
        return password.matches("^[A-Za-z0-9_]{6,30}$");
    }
}
