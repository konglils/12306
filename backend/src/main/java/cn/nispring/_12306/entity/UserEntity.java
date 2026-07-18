package cn.nispring._12306.entity;

import java.time.LocalDateTime;

public record UserEntity(
        Long id,
        String username,
        String password,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
