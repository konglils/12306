package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.DiscountType;
import cn.nispring.rail12306.model.IdType;
import cn.nispring.rail12306.model.PassengerStatus;
import cn.nispring.rail12306.model.Sex;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PassengerEntity(
        Long userId,
        Boolean isUser,
        IdType idType,
        String idNo,
        String name,
        String phoneE164,
        String email,
        String countryCode,
        LocalDate birthDate,
        Sex sex,
        LocalDate validThrough,
        DiscountType discountType,
        PassengerStatus status,
        LocalDateTime updatedAt
) {
}
