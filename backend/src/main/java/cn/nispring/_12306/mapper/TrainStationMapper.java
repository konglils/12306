package cn.nispring._12306.mapper;

import cn.nispring._12306.entity.TrainStationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TrainStationMapper {

    @Select("SELECT id, train_id, station_id, train_code, sequence, " +
            "arrive_day, arrive_time, start_day, start_time " +
            "FROM train_stations ORDER BY train_id, sequence")
    List<TrainStationEntity> selectAll();
}
