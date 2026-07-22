package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.TrainEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TrainMapper {

    @Select("SELECT id, number FROM trains")
    List<TrainEntity> selectAll();

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO trains (number) VALUES (#{number})")
    int insert(TrainEntity train);

    @Update("UPDATE trains SET number = #{number} WHERE id = #{id}")
    int updateById(TrainEntity train);

    @Delete("DELETE FROM trains WHERE id = #{id}")
    int deleteById(Long id);
}
