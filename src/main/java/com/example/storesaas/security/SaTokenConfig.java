package com.example.storesaas.security;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.example.storesaas.common.constants.ApiRoutes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 配置 SaToken 拦截器
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> SaRouter.match("/**")
                        .notMatch(ApiRoutes.AUTH + "/**", ApiRoutes.MINI_AUTH, ApiRoutes.MINI_PUBLIC, "/v3/api-docs/**",
                                "/swagger-ui/**", "/swagger-ui.html")
                        .check(StpUtil::checkLogin)))
                .addPathPatterns("/**");
    }

    /**
     * 配置跨域
     * @param registry 跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * 配置权限认证接口
     * @return StpInterface
     */
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
