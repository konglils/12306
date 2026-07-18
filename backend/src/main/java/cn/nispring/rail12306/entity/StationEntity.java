package cn.nispring.rail12306.entity;

public record StationEntity(
        Long id,
        Long areaId,
        String telecode,
        String name
) {
}
