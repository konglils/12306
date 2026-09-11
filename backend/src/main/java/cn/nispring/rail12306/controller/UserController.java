package cn.nispring.rail12306.controller;

import cn.nispring.rail12306.model.SessionUser;
import cn.nispring.rail12306.model.User;
import cn.nispring.rail12306.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        return userService.signup(username, password);
    }

    @PostMapping("/sessions")
    public User signin(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String username = body.get("username");
        String password = body.get("password");
        SessionUser user = userService.signin(username, password);

        Cookie cookie = new Cookie("SESSIONID", user.sessionToken());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(86400);
        response.addCookie(cookie);

        return new User(user.id(), user.username());
    }

    @GetMapping("/session")
    public User getUser(@CookieValue(value = "SESSIONID", required = false) String sessionToken) {
        return userService.getUser(sessionToken);
    }

    @DeleteMapping("/session")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signout(@CookieValue("SESSIONID") String sessionToken, HttpServletResponse response) {
        userService.signout(sessionToken);

        Cookie cookie = new Cookie("SESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
