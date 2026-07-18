package cn.nispring._12306.model.layout;

import java.util.List;

public record Coach(
        String name,
        Boolean mute,
        List<String> number
) {
}
