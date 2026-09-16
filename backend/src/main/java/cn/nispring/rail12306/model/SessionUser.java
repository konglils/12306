package cn.nispring.rail12306.model;

public record SessionUser(
        Long id,
        String username,
        String sessionToken
) {
}
