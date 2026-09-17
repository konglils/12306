package cn.nispring.rail12306.config;

import cn.nispring.rail12306.annotation.RequireSigninArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ResolverConfig implements WebMvcConfigurer {

    private final RequireSigninArgumentResolver requireSigninArgumentResolver;

    public ResolverConfig(RequireSigninArgumentResolver requireSigninArgumentResolver) {
        this.requireSigninArgumentResolver = requireSigninArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(requireSigninArgumentResolver);
    }
}
