package cn.nispring.rail12306.model;

import java.util.List;

public record Train(
        String trainCodes,
        String style,
        List<Station> stations
) {
}
