package cn.nispring._12306.entity;

import java.time.LocalTime;

public record TrainStationEntity(
        Long id,
        Long trainId,
        Long stationId,
        String trainCode,
        int sequence,
        int arriveDay,
        LocalTime arriveTime,
        int startDay,
        LocalTime startTime
) {
}
