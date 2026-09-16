package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.CarEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CarMapper {

    @Select("SELECT id, style, code FROM cars")
    List<CarEntity> selectAll();

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO cars (style, code) VALUES (#{style}, #{code})")
    void insert(CarEntity car);

    @Insert("""
            <script>
            INSERT INTO cars (id, style, code) VALUES
            <foreach collection="list" item="car" separator=",">
                (#{car.id}, #{car.style}, #{car.code})
            </foreach>
            </script>
            """)
    int insertBatch(List<CarEntity> cars);

    @Update("UPDATE cars SET style = #{style}, code = #{code} WHERE id = #{id}")
    void updateById(CarEntity car);

    @Delete("DELETE FROM cars WHERE id = #{id}")
    void deleteById(Long id);
}
