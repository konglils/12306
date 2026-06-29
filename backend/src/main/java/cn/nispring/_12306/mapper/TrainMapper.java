package cn.nispring._12306.mapper;

import cn.nispring._12306.entity.TrainEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TrainMapper {

    @Select("SELECT id, style FROM trains")
    List<TrainEntity> selectAll();
}
