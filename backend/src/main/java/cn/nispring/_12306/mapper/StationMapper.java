package cn.nispring._12306.mapper;

import cn.nispring._12306.entity.StationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StationMapper {

    @Select("SELECT id, telecode, name FROM stations")
    List<StationEntity> selectAll();
}
