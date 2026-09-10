package cn.nispring.rail12306.controller;

import cn.nispring.rail12306.model.Ticket;
import cn.nispring.rail12306.service.TicketService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public List<Ticket> getTickets(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, long from, long to) {
        return ticketService.getTickets(date, from, to);
    }
}
