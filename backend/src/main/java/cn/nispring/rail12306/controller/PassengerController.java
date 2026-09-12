package cn.nispring.rail12306.controller;

import cn.nispring.rail12306.exception.BusinessException;
import cn.nispring.rail12306.model.Passenger;
import cn.nispring.rail12306.model.User;
import cn.nispring.rail12306.service.PassengerService;
import cn.nispring.rail12306.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PassengerController {

    private final UserService userService;
    private final PassengerService passengerService;

    public PassengerController(UserService userService, PassengerService passengerService) {
        this.userService = userService;
        this.passengerService = passengerService;
    }

    @GetMapping("/passengers")
    public List<Passenger> getPassenger(@CookieValue("SESSIONID") String sessionToken) {
        User user = userService.getUser(sessionToken);
        if (user == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户未登录");
        }

        return passengerService.getPassenger(user);
    }

    @PostMapping("/passengers")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPassenger(@CookieValue("SESSIONID") String sessionToken,
                               @Valid @RequestBody Passenger passenger) {
        User user = userService.getUser(sessionToken);
        if (user == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户未登录");
        }

        passengerService.addPassenger(user, passenger);
    }
}
