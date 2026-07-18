package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.SeatType;

import java.time.LocalDate;

public record PriceEntity(
        LocalDate trainDate,
        Long fromAreaId,
        Long toAreaId,
        Long trainId,
        Long fromStationId,
        Long toStationId,
        Integer fromStopIdx,
        Integer toStopIdx,
        SeatType seatType,
        Boolean hasSeat,
        Integer price
) {
}
