package cn.nispring.rail12306.controller;

import cn.nispring.rail12306.annotation.RequireSignin;
import cn.nispring.rail12306.entity.PassengerEntity;
import cn.nispring.rail12306.model.IdType;
import cn.nispring.rail12306.model.Passenger;
import cn.nispring.rail12306.model.User;
import cn.nispring.rail12306.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping("/passengers")
    public List<Passenger> getPassenger(@RequireSignin User user) {
        return passengerService.getPassenger(user);
    }

    @PostMapping("/passengers")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPassenger(@RequireSignin User user, @Valid @RequestBody Passenger passenger) {
        PassengerEntity entity = passengerService.makeEntity(passenger);
        entity.setUserId(user.id());
        entity.setIsUser(false);
        passengerService.addPassenger(entity);
    }

    @PatchMapping("/passengers")
    public void updatePassenger(@RequireSignin User user, @Valid @RequestBody Passenger passenger) {
        passengerService.updatePassenger(user, passenger);
    }

    @DeleteMapping("/passengers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@RequireSignin User user, IdType idType, String idNo) {
        passengerService.deletePassenger(user, idType, idNo);
    }
}
