package com.example.storesaas.security;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> SaRouter.match("/**")
                        .notMatch("/api/auth/**", "/api/mini/auth/**", "/v3/api-docs/**",
                                "/swagger-ui/**", "/swagger-ui.html")
                        .check(StpUtil::checkLogin)))
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public StpInterface stpInterface() {
        return new StpInterface() {
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                Object value = StpUtil.getSessionByLoginId(loginId).get("loginUser");
                if (value instanceof LoginUser loginUser) {
                    return loginUser.permissions();
                }
                return Collections.emptyList();
            }

            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                return Collections.emptyList();
            }
        };
    }
}
