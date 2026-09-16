package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.PriceEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PriceMapper {

    @Delete("DELETE FROM prices WHERE train_date < CURRENT_DATE()")
    void deleteOld();

    @Insert("""
            <script>
            INSERT INTO prices
                (train_date, from_area_id, to_area_id, train_id, from_station_id, to_station_id,
                 from_stop_idx, to_stop_idx, seat_type, has_seat, price)
            VALUES
            <foreach collection="list" item="price" separator=",">
                (#{price.trainDate}, #{price.fromAreaId}, #{price.toAreaId}, #{price.trainId},
                 #{price.fromStationId}, #{price.toStationId}, #{price.fromStopIdx}, #{price.toStopIdx},
                 #{price.seatType}, #{price.hasSeat}, #{price.price})
            </foreach>
            </script>
            """)
    void insertBatch(List<PriceEntity> prices);

    @Select("SELECT EXISTS(SELECT 1 FROM prices WHERE train_date = #{date})")
    boolean existsByDate(LocalDate date);

    @Select("SELECT " +
            "train_date, from_area_id, to_area_id, train_id, from_station_id, to_station_id, " +
            "from_stop_idx, to_stop_idx, seat_type, has_seat, price " +
            "FROM prices " +
            "WHERE train_date = #{date} AND from_area_id = #{fromAreaId} AND to_area_id = #{toAreaId} " +
            "ORDER BY train_id")
    List<PriceEntity> select(LocalDate date, Long fromAreaId, Long toAreaId);
}
