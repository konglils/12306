package cn.nispring._12306.controller;

import cn.nispring._12306.model.Ticket;
import cn.nispring._12306.service.RailCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TicketController {

    private final RailCache railCache;

    public TicketController(RailCache railCache) {
        this.railCache = railCache;
    }

    @GetMapping("/tickets")
    public List<Ticket> queryTickets(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date) {
        return railCache.queryTickets(from, to);
    }
}
