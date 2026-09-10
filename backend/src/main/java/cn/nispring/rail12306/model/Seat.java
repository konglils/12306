package cn.nispring.rail12306.model;

public record Seat(
        SeatType type,
        boolean hasSeat,
        int price,
        int remaining
) {
}
