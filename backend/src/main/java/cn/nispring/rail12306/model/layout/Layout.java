package cn.nispring.rail12306.model.layout;

import java.util.List;

public record Layout(
        List<CarInfo> carInfos,
        List<SeatLayout> seatLayouts
) {
}
