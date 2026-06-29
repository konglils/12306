package cn.nispring._12306.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.List;

public record Ticket(
        String trainCode,
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,
        int arriveDay,
        @JsonFormat(pattern = "HH:mm")
        LocalTime arriveTime,
        List<Seat> seats
) {
}
