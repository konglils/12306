package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.StationEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StationMapper {

    @Select("SELECT id, area_id, telecode, name FROM stations")
    List<StationEntity> selectAll();

    @Insert("INSERT INTO stations (area_id, telecode, name) VALUES (#{areaId}, #{telecode}, #{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(StationEntity entity);

    @Update("UPDATE stations SET area_id = #{areaId}, telecode = #{telecode}, name = #{name} WHERE id = #{id}")
    void updateById(StationEntity entity);

    @Delete("DELETE FROM stations WHERE id = #{id}")
    void deleteById(Long id);
}
