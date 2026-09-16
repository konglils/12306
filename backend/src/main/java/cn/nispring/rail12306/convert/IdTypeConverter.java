package cn.nispring.rail12306.convert;

import cn.nispring.rail12306.model.IdType;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 让 IdType 可以从 URL 参数（如 ?idType=1）直接解析。
 * Spring 默认把字符串转枚举是按枚举常量名（Enum.valueOf）进行的，不认 IdType 的数字 code；
 * Spring Boot 会自动把上下文里的 Converter Bean 注册进 MVC 的转换服务，
 * 因此 @RequestParam / @PathVariable 的 IdType 参数都会走这里。
 */
@Component
public class IdTypeConverter implements Converter<String, IdType> {

    @Override
    public IdType convert(@NonNull String source) {
        if (source.isBlank()) {
            return null;
        }
        String trimmed = source.trim();
        return IdType.fromCode(Integer.parseInt(trimmed));
    }
}