package cn.nispring.rail12306.model;

public record Seat(
        SeatType type,
        int price,
        int remaining
) {
}
