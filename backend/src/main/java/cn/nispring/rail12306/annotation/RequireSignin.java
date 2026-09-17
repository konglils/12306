package cn.nispring.rail12306.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在 User 类型的方法参数上：从 SESSIONID cookie 解析当前登录用户并注入，
 * 未登录时抛出 401 业务异常。
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireSignin {
}
