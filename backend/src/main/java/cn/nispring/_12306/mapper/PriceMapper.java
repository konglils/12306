package cn.nispring._12306.mapper;

import cn.nispring._12306.entity.PriceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PriceMapper {

    @Select("SELECT id, from_station_id, to_station_id, train_id, price_raw FROM prices")
    List<PriceEntity> selectAll();
}
