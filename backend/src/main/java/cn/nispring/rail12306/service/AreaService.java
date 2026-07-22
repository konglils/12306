package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.AreaEntity;
import cn.nispring.rail12306.mapper.AreaMapper;
import cn.nispring.rail12306.model.Area;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AreaService {

    private final AreaMapper areaMapper;

    private final ConcurrentHashMap<Long, Area> map = new ConcurrentHashMap<>();

    public AreaService(AreaMapper areaMapper) {
        this.areaMapper = areaMapper;
    }

    @PostConstruct
    public void loadAll() {
        for (AreaEntity area : areaMapper.selectAll()) {
            map.put(area.getId(), new Area(area.getName()));
        }
    }

    public Area get(Long id) {
        return map.get(id);
    }

    public Area insert(String name) {
        AreaEntity entity = new AreaEntity(null, name);
        areaMapper.insert(entity);
        return map.putIfAbsent(entity.getId(), new Area(name));
    }

    public Area update(Long id, String name) {
        areaMapper.updateById(new AreaEntity(id, name));
        return map.replace(id, new Area(name));
    }

    public Area remove(Long id) {
        areaMapper.deleteById(id);
        return map.remove(id);
    }
}
