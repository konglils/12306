package cn.nispring.rail12306.controller;

import cn.nispring.rail12306.model.Station;
import cn.nispring.rail12306.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/stations")
    public List<Station> getStations() {
        return stationService.getAll();
    }
}
