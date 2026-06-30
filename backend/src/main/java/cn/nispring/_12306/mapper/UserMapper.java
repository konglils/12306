package cn.nispring._12306.mapper;

import cn.nispring._12306.entity.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password FROM users WHERE username = #{username}")
    UserEntity selectByUsername(String username);

    @Select("SELECT id, username, password FROM users WHERE id = #{id}")
    UserEntity selectById(Long id);

    @Insert("INSERT INTO users (username, password) VALUES (#{username}, #{password})")
    int insert(UserEntity user);
}
