package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.PassengerEntity;
import cn.nispring.rail12306.model.IdType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PassengerMapper {

    @Select("""
            SELECT user_id, is_user, id_type, id_no, name, phone_e164, email, country_code, birth_date,
                   sex, valid_through, discount_type, status, updated_at
            FROM passengers
            WHERE user_id = #{userId} AND id_type = #{idType} AND id_no = #{idNo}
            """)
    PassengerEntity selectById(Long userId, IdType idType, String idNo);

    @Insert("""
            INSERT INTO passengers
                (user_id, is_user, id_type, id_no, name, phone_e164, email,
                 country_code, birth_date, sex, valid_through, discount_type, status)
            VALUES
                (#{userId}, #{isUser}, #{idType}, #{idNo}, #{name}, #{phoneE164}, #{email},
                 #{countryCode}, #{birthDate}, #{sex}, #{validThrough}, #{discountType}, #{status})
            """)
    void insert(PassengerEntity entity);
}
