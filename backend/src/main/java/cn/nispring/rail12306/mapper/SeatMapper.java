package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.SeatEntity;
import cn.nispring.rail12306.model.SeatType;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SeatMapper {

    @Delete("DELETE FROM seats WHERE train_date < CURRENT_DATE()")
    int deleteOld();

    @Insert("""
            <script>
            INSERT INTO seats
                (train_date, train_id, seat_type, segment_idx, graph)
            VALUES
            <foreach collection="list" item="entity" separator=",">
                (#{entity.trainDate}, #{entity.trainId}, #{entity.seatType}, #{entity.segmentIdx}, #{entity.graph})
            </foreach>
            </script>
            """)
    void insertBatch(List<SeatEntity> entities);

    @Select("SELECT EXISTS(SELECT 1 FROM seats WHERE train_date = #{date})")
    boolean existsByDate(LocalDate date);

    @Select("SELECT train_date, train_id, seat_type, segment_idx, graph " +
            "FROM seats " +
            "WHERE train_date = #{date} AND train_id = #{trainId} AND seat_type = #{seatType} AND " +
            "segment_idx >= #{fromIdx} AND segment_idx <= #{toIdx} " +
            "ORDER BY segment_idx")
    List<SeatEntity> select(LocalDate date, Long trainId, SeatType seatType, Integer fromIdx, Integer toIdx);
}
