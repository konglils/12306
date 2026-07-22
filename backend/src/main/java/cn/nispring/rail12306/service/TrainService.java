package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.TrainEntity;
import cn.nispring.rail12306.mapper.TrainMapper;
import cn.nispring.rail12306.model.Train;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrainService {

    private final TrainMapper trainMapper;

    private final ConcurrentHashMap<Long, Train> map = new ConcurrentHashMap<>();

    public TrainService(TrainMapper trainMapper) {
        this.trainMapper = trainMapper;
    }

    @PostConstruct
    public void loadAll() {
        for (TrainEntity train : trainMapper.selectAll()) {
            map.put(train.getId(), new Train(train.getNumber()));
        }
    }

    public Train get(Long id) {
        return map.get(id);
    }

    public Train insert(String number) {
        TrainEntity entity = new TrainEntity(null, number);
        trainMapper.insert(entity);
        return map.putIfAbsent(entity.getId(), new Train(number));
    }

    public Train update(Long id, String number) {
        TrainEntity entity = new TrainEntity(id, number);
        trainMapper.updateById(entity);
        return map.replace(id, new Train(number));
    }

    public Train remove(Long id) {
        trainMapper.deleteById(id);
        return map.remove(id);
    }
}
