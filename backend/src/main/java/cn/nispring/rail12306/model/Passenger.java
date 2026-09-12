package cn.nispring.rail12306.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record Passenger(
        Boolean isUser,
        @NotNull(message = "证件类型不能为空") IdType idType,
        @NotBlank(message = "证件号码不能为空") String idNo,
        @NotBlank(message = "姓名不能为空") String name,
        String phone,
        String email,
        String countryCode,
        LocalDate birthDate,
        Sex sex,
        LocalDate validThrough,
        @NotNull(message = "优惠类型不能为空") DiscountType discountType,
        PassengerStatus status
) {
}
