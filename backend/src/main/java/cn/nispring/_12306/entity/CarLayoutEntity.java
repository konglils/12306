package cn.nispring._12306.entity;

import cn.nispring._12306.model.layout.Layout;

import java.time.LocalDate;

public record CarLayoutEntity(
        LocalDate trainDate,
        Long trainId,
        Long carId,
        Layout layout
) {
}
