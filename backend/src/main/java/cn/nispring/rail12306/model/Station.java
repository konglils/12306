package cn.nispring.rail12306.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public record Station(
        String telecode,
        String trainCode,
        int arriveDay,
        @JsonFormat(pattern = "HH:mm")
        LocalTime arriveTime,
        int startDay,
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime
) {
}
