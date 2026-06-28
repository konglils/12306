package cn.nispring._12306.controller;

import cn.nispring._12306.model.Seat;
import cn.nispring._12306.model.Ticket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class TicketController {

    @GetMapping("/tickets")
    public List<Ticket> queryTickets(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date) {

        var rng = ThreadLocalRandom.current();

        return List.of(
                new Ticket("G1", LocalTime.of(6, 30), 0, LocalTime.of(11, 24),
                        List.of(
                                new Seat("商务", 23150, rng.nextInt(0, 100)),
                                new Seat("一等", 10580, rng.nextInt(0, 100)),
                                new Seat("二等", 6610, rng.nextInt(0, 100)),
                                new Seat("无座", 6610, rng.nextInt(0, 100))
                        )),
                new Ticket("G11", LocalTime.of(10, 3), 0, LocalTime.of(14, 39),
                        List.of(
                                new Seat("商务", 23150, rng.nextInt(0, 100)),
                                new Seat("一等", 10580, rng.nextInt(0, 100)),
                                new Seat("二等", 6610, rng.nextInt(0, 100)),
                                new Seat("无座", 6610, rng.nextInt(0, 100))
                        ))
        );
    }
}
