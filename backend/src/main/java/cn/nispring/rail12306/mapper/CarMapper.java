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
    int insert(CarEntity car);

    @Update("UPDATE cars SET style = #{style}, code = #{code} WHERE id = #{id}")
    int updateById(CarEntity car);

    @Delete("DELETE FROM cars WHERE id = #{id}")
    int deleteById(Long id);
}
