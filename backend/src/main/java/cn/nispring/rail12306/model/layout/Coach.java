package cn.nispring.rail12306.model.layout;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Coach(
        String name,
        @JsonProperty("seat") List<String> seats
) {
}
