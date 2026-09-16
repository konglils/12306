package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.PassengerEntity;
import cn.nispring.rail12306.model.IdType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PassengerMapper {

    @Select("""
            SELECT user_id, is_user, id_type, id_no, name, phone_e164, email, country_code, birth_date,
                   sex, valid_through, discount_type, status, updated_at
            FROM passengers
            WHERE user_id = #{userId} AND id_type = #{idType} AND id_no = #{idNo}
            """)
    PassengerEntity selectById(Long userId, IdType idType, String idNo);

    @Select("""
            SELECT user_id, is_user, id_type, id_no, name, phone_e164, email, country_code, birth_date,
                   sex, valid_through, discount_type, status, updated_at
            FROM passengers
            WHERE user_id = #{userId}
            """)
    List<PassengerEntity> selectByUser(Long userId);

    // 一定要用 FOR UPDATE 把相关行锁住，防止同事务有其他请求写入
    @Select("SELECT EXISTS(" +
            "SELECT 1 FROM passengers WHERE id_type = #{idType} AND id_no = #{idNo} AND is_user = TRUE FOR UPDATE" +
            ")")
    boolean existsUserById(IdType idType, String idNo);

    @Insert("""
            INSERT INTO passengers
                (user_id, is_user, id_type, id_no, name, phone_e164, email,
                 country_code, birth_date, sex, valid_through, discount_type, status)
            VALUES
                (#{userId}, #{isUser}, #{idType}, #{idNo}, #{name}, #{phoneE164}, #{email},
                 #{countryCode}, #{birthDate}, #{sex}, #{validThrough}, #{discountType}, #{status})
            """)
    void insert(PassengerEntity entity);

    @Update("""
            UPDATE passengers
            SET phone_e164 = #{phoneE164}, email = #{email}, country_code = #{countryCode}, birth_date = #{birthDate},
                sex = #{sex}, valid_through = #{validThrough}, discount_type = #{discountType}
            WHERE user_id = #{userId} AND id_type = #{idType} AND id_no = #{idNo}
            """)
    int updateById(PassengerEntity entity);

    @Delete("DELETE FROM passengers WHERE user_id = #{userId} AND id_type = #{idType} AND id_no = #{idNo}")
    int deleteById(Long userId, IdType idType, String idNo);
}
