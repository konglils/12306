package cn.nispring.rail12306.model.layout;

import cn.nispring.rail12306.model.SeatType;

import java.util.List;

public record SeatLayout(
        SeatType type,
        Integer column,
        List<Coach> coaches
) {
}
