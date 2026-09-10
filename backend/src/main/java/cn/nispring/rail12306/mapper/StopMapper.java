package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.StopEntity;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
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
    void insertBatch(List<StopEntity> stops);

    @Select("SELECT EXISTS(SELECT 1 FROM stops WHERE train_date = #{date})")
    boolean existsByDate(LocalDate date);

    @Select("SELECT train_id FROM stops WHERE train_date = #{date} AND train_code = #{code} LIMIT 1")
    Long selectIdByCode(LocalDate date, String code);

    @Select("SELECT " +
            "train_date, train_id, stop_idx, station_id, train_code, arrive_day, arrive_time, start_day, start_time " +
            "FROM stops " +
            "WHERE train_date = #{date} AND train_id = #{trainId} " +
            "ORDER BY stop_idx")
    List<StopEntity> selectByTrainId(LocalDate date, Long trainId);

    @Select("SELECT " +
            "train_date, train_id, stop_idx, station_id, train_code, arrive_day, arrive_time, start_day, start_time " +
            "FROM stops " +
            "WHERE train_date = #{date} AND train_id = #{trainId} AND stop_idx = #{stopIdx}")
    StopEntity selectByStopIdx(LocalDate date, Long trainId, Integer stopIdx);
}
