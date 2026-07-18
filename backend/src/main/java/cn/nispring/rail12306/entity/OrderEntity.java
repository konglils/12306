package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderEntity(
        Long id,
        Long userId,
        LocalDate trainDate,
        Long trainId,
        Long fromStationId,
        Long toStationId,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime expireAt,
        LocalDateTime paidAt
) {
}
