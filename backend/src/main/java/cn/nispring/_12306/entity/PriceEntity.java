package cn.nispring._12306.entity;

public record PriceEntity(
        Long id,
        Long fromStationId,
        Long toStationId,
        Long trainId,
        String priceRaw
) {
}
