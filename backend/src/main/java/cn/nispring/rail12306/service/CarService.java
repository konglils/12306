package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.CarEntity;
import cn.nispring.rail12306.mapper.CarMapper;
import cn.nispring.rail12306.model.Car;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CarService {

    private final CarMapper carMapper;

    private final ConcurrentHashMap<Long, Car> map = new ConcurrentHashMap<>();

    public CarService(CarMapper carMapper) {
        this.carMapper = carMapper;
    }

    @PostConstruct
    public void loadAll() {
        for (CarEntity car : carMapper.selectAll()) {
            map.put(car.getId(), new Car(car.getStyle(), car.getCode()));
        }
    }

    public Car get(Long id) {
        return map.get(id);
    }

    public Car insert(String style, String code) {
        CarEntity entity = new CarEntity(null, style, code);
        carMapper.insert(entity);
        return map.putIfAbsent(entity.getId(), new Car(style, code));
    }

    public Car update(Long id, String style, String code) {
        carMapper.updateById(new CarEntity(id, style, code));
        return map.replace(id, new Car(style, code));
    }

    public Car remove(Long id) {
        carMapper.deleteById(id);
        return map.remove(id);
    }
}
