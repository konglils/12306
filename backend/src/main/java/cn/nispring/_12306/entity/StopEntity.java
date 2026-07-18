package cn.nispring._12306.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public record StopEntity(
        LocalDate trainDate,
        Long trainId,
        Integer stopIdx,
        Long stationId,
        String trainCode,
        Integer arriveDay,
        LocalTime arriveTime,
        Integer startDay,
        LocalTime startTime
) {
}
