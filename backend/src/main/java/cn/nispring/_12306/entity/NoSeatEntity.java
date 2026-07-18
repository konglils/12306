package cn.nispring._12306.entity;

import cn.nispring._12306.model.SeatType;

import java.time.LocalDate;

public record NoSeatEntity(
        LocalDate trainDate,
        Long trainId,
        SeatType seatType,
        Integer segmentIdx,
        Integer remaining
) {
}
