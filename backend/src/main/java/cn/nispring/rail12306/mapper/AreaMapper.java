package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.AreaEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AreaMapper {

    @Select("SELECT id, name FROM areas")
    List<AreaEntity> selectAll();

    @Insert("INSERT INTO areas (name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AreaEntity entity);

    @Update("UPDATE areas SET name = #{name} WHERE id = #{id}")
    void updateById(AreaEntity entity);

    @Delete("DELETE FROM stations WHERE id = #{id}")
    void deleteById(Long id);
}
