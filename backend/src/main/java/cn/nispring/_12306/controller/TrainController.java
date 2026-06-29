package cn.nispring._12306.controller;

import cn.nispring._12306.model.Train;
import cn.nispring._12306.service.RailCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainController {

    private final RailCache railCache;

    public TrainController(RailCache railCache) {
        this.railCache = railCache;
    }

    @GetMapping("/trains/{trainCode}")
    public Train getTrain(@PathVariable String trainCode) {
        return railCache.getTrain(trainCode);
    }
}
