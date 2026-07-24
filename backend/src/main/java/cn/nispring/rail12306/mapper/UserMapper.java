package cn.nispring.rail12306.mapper;

import cn.nispring.rail12306.entity.UserEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT id, session_token FROM users")
    List<UserEntity> selectSessionTokens();

    @Select("SELECT id, username, password, session_token, created_at, updated_at FROM users WHERE username = #{username}")
    UserEntity selectByUsername(String username);

    @Select("SELECT id, username, password, session_token, created_at, updated_at FROM users WHERE session_token = #{sessionToken}")
    UserEntity selectBySessionToken(String sessionToken);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO users (username, password) VALUES (#{username}, #{password})")
    int insert(UserEntity entity);

    @Update("UPDATE users SET session_token = #{sessionToken} WHERE id = #{id}")
    int updateSessionToken(@Param("id") Long id, @Param("sessionToken") String sessionToken);
}
