package cn.nispring._12306.controller;

import cn.nispring._12306.model.ErrorResponse;
import cn.nispring._12306.service.UserService;
import cn.nispring._12306.service.UserService.DuplicateUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest request) {
        userService.signup(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateUserException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(IllegalArgumentException e) {
        return ResponseEntity.unprocessableEntity()
                .body(new ErrorResponse(e.getMessage()));
    }

    record SignupRequest(String username, String password) {}
}
