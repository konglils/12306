package cn.nispring.rail12306.annotation;

import cn.nispring.rail12306.exception.BusinessException;
import cn.nispring.rail12306.model.User;
import cn.nispring.rail12306.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.WebUtils;

/// 为带 @RequireSignin 注解的 User 参数解析当前登录用户
@Component
public class RequireSigninArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String SESSION_COOKIE = "SESSIONID";

    private final UserService userService;

    public RequireSigninArgumentResolver(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequireSignin.class)
                && User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public User resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Cookie cookie = request == null ? null : WebUtils.getCookie(request, SESSION_COOKIE);
        User user = cookie == null ? null : userService.getUser(cookie.getValue());
        if (user == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户未登录");
        }
        return user;
    }
}
