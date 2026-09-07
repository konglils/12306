package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.CarLayoutEntity;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CarLayoutMapper {

    @Delete("DELETE FROM car_layouts WHERE train_date < CURRENT_DATE()")
    void deleteOld();

    @Insert("""
            <script>
            INSERT INTO car_layouts
                (train_date, train_id, car_id, layout)
            VALUES
            <foreach collection="list" item="entity" separator=",">
                (#{entity.trainDate}, #{entity.trainId}, #{entity.carId}, #{entity.layout})
            </foreach>
            </script>
            """)
    void insertBatch(List<CarLayoutEntity> entities);

    @Select("SELECT EXISTS(SELECT 1 FROM car_layouts WHERE train_date = #{date})")
    boolean existsByDate(LocalDate date);

    @Select("SELECT train_date, train_id, car_id, layout FROM car_layouts WHERE train_date = #{date} AND train_id = #{trainId}")
    CarLayoutEntity selectByTrainId(LocalDate date, Long trainId);

    @Select("SELECT train_date, train_id, car_id, layout FROM car_layouts WHERE train_date = #{date} AND car_id = #{carId}")
    List<CarLayoutEntity> selectByCarId(LocalDate date, Long carId);
}
