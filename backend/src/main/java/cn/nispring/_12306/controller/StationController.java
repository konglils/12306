package cn.nispring._12306.controller;

import cn.nispring._12306.service.RailCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StationController {

    private final RailCache railCache;

    public StationController(RailCache railCache) {
        this.railCache = railCache;
    }

    @GetMapping("/stations")
    public Map<String, String> getStations() {
        return railCache.getStations();
    }
}
