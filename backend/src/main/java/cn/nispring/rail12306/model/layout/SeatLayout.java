package cn.nispring.rail12306.model.layout;

import cn.nispring.rail12306.model.SeatType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SeatLayout(
        SeatType type,
        @JsonProperty("coach") List<Coach> coaches
) {
}
