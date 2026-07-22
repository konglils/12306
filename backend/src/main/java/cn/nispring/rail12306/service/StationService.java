package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.StationEntity;
import cn.nispring.rail12306.mapper.StationMapper;
import cn.nispring.rail12306.model.Station;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StationService {

    private final StationMapper stationMapper;

    private final ConcurrentHashMap<Long, Station> map = new ConcurrentHashMap<>();

    public StationService(StationMapper stationMapper) {
        this.stationMapper = stationMapper;
    }

    @PostConstruct
    public void loadAll() {
        for (StationEntity entity : stationMapper.selectAll()) {
            map.put(entity.getId(), new Station(entity.getAreaId(), entity.getTelecode(), entity.getName()));
        }
    }

    public List<Station> getAll() {
        return map.values().stream().toList();
    }

    public Station get(Long id) {
        return map.get(id);
    }

    public Station insert(Station station) {
        StationEntity entity = new StationEntity(-1L, station.areaId(), station.telecode(), station.name());
        stationMapper.insert(entity);
        return map.putIfAbsent(entity.getId(), station);
    }

    public Station update(Long id, Station station) {
        stationMapper.updateById(new StationEntity(id, station.areaId(), station.telecode(), station.name()));
        return map.replace(id, station);
    }

    public Station remove(Long id) {
        stationMapper.deleteById(id);
        return map.remove(id);
    }
}
