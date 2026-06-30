package cn.nispring._12306.controller;

import cn.nispring._12306.model.Train;
import cn.nispring._12306.service.RailCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainController {

    private final RailCache railCache;

    public TrainController(RailCache railCache) {
        this.railCache = railCache;
    }

    @GetMapping("/trains")
    public Train getTrain(@RequestParam String code) {
        return railCache.getTrain(code);
    }
}
