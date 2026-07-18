package cn.nispring._12306.model.layout;

import cn.nispring._12306.model.SeatType;
import cn.nispring._12306.model.layout.Coach;

import java.util.List;

public record SeatLayout(
        SeatType type,
        Integer column,
        List<Coach> coaches
) {
}
