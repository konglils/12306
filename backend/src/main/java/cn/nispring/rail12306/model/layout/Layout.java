package cn.nispring.rail12306.model.layout;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Layout(
        @JsonProperty("carInfo") List<CarInfo> carInfos,
        @JsonProperty("seatLayout") List<SeatLayout> seatLayouts
) {
}
