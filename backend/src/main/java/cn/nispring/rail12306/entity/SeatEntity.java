package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.SeatType;

import java.time.LocalDate;

public record SeatEntity(
        LocalDate trainDate,
        Long trainId,
        SeatType seatType,
        Integer segmentIdx,
        byte[] graph
) {
}
