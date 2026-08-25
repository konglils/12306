package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.AreaEntity;
import cn.nispring.rail12306.entity.StopEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StopMapper {

    @Delete("DELETE FROM stops WHERE train_date < CURRENT_DATE()")
    void deleteOld();

    @Insert("""
            <script>
            INSERT INTO stops
                (train_date, train_id, stop_idx, station_id, train_code, arrive_day, arrive_time, start_day, start_time)
            VALUES
            <foreach collection="list" item="stop" separator=",">
                (#{stop.trainDate}, #{stop.trainId}, #{stop.stopIdx}, #{stop.stationId}, #{stop.trainCode}, #{stop.arriveDay}, #{stop.arriveTime}, #{stop.startDay}, #{stop.startTime})
            </foreach>
            </script>
            """)
    int insertBatch(List<StopEntity> stops);
}
