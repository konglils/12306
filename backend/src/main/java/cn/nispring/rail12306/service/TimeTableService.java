package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.StopEntity;
import cn.nispring.rail12306.exception.BusinessException;
import cn.nispring.rail12306.mapper.StopMapper;
import cn.nispring.rail12306.model.TimeTableRow;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeTableService {

    private final StopMapper stopMapper;
    private final StationService stationService;

    public TimeTableService(StopMapper stopMapper, StationService stationService) {
        this.stopMapper = stopMapper;
        this.stationService = stationService;
    }

    public List<TimeTableRow> getTable(LocalDate date, String code) {
        Long id = stopMapper.selectIdByCode(date, code);
        List<StopEntity> entities = stopMapper.selectById(date, id);
        List<TimeTableRow> table = entities.stream().map(entity -> new TimeTableRow(
                stationService.get(entity.getStationId()).telecode(),
                entity.getTrainCode(),
                entity.getArriveDay(),
                entity.getArriveTime(),
                entity.getStartDay(),
                entity.getStartTime()
        )).toList();

        if (table.isEmpty()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "未找到车次");
        } else {
            return table;
        }
    }
}
