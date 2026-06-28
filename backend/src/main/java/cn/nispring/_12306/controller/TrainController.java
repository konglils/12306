package cn.nispring._12306.controller;

import cn.nispring._12306.model.Station;
import cn.nispring._12306.model.Train;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@RestController
public class TrainController {

    @GetMapping("/trains/{trainCode}")
    public Train getTrain(@PathVariable String trainCode) {
        return new Train("C5843/C5842", "CRH3A-A", List.of(
                new Station("ZYE", "C5843", 0, LocalTime.MIN, 0, LocalTime.of(9, 20)),
                new Station("KQW", "C5843", 0, LocalTime.of(10, 5), 0, LocalTime.of(10, 9)),
                new Station("LAE", "C5842", 0, LocalTime.of(10, 28), 0, LocalTime.of(10, 30)),
                new Station("HNE", "C5842", 0, LocalTime.of(10, 42), 0, LocalTime.MIN)
        ));
    }
}
