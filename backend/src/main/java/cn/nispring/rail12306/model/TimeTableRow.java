package cn.nispring.rail12306.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public record TimeTableRow(
        String stationTelecode,
        String trainCode,
        Integer arriveDay,
        @JsonFormat(pattern = "HH:mm")
        LocalTime arriveTime,
        Integer startDay,
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime
) {
}
