package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.DiscountType;
import cn.nispring.rail12306.model.SeatType;
import cn.nispring.rail12306.model.Sex;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PassengerOrderEntity(
        Long id,
        Long orderId,
        Integer idType,
        String idNo,
        String name,
        String phoneE164,
        String email,
        String countryCode,
        LocalDate birthDate,
        Sex sex,
        LocalDate validThrough,
        DiscountType discountType,
        SeatType seatType,
        Boolean hasSeat,
        Integer seatIdx,
        String seatName,
        Integer price,
        Boolean refunded,
        LocalDateTime refundTime,
        Integer refundPrice
) {
}
