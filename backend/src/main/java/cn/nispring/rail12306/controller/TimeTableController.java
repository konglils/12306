package cn.nispring.rail12306.controller;

import cn.nispring.rail12306.model.TimeTableRow;
import cn.nispring.rail12306.service.TimeTableService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class TimeTableController {

    private final TimeTableService timeTableService;

    public TimeTableController(TimeTableService timeTableService) {
        this.timeTableService = timeTableService;
    }

    @GetMapping("/timetable")
    public List<TimeTableRow> getTimeTable(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, String code) {
        return timeTableService.getTable(date, code);
    }
}
