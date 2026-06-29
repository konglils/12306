package cn.nispring._12306.controller;

import cn.nispring._12306.entity.UserEntity;
import cn.nispring._12306.model.ErrorResponse;
import cn.nispring._12306.service.UserService;
import cn.nispring._12306.service.UserService.BadCredentialsException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    private final UserService userService;

    public SessionController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request, HttpSession session) {
        UserEntity user = userService.signin(request.username(), request.password());
        session.setAttribute("userId", user.id());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(IllegalArgumentException e) {
        return ResponseEntity.unprocessableEntity()
                .body(new ErrorResponse(e.getMessage()));
    }

    record LoginRequest(String username, String password) {}
}
