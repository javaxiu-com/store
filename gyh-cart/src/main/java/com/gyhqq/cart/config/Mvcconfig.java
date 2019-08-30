package com.gyhqq.cart.config;

import com.gyhqq.cart.interceptors.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 把拦截器UserInterceptor放入容器中的配置类
 */
@Configuration
public class Mvcconfig implements WebMvcConfigurer {
    /**
     * 增加一个拦截器UserInterceptor,把拦截器放入容器中
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //什么样的请求才拦截
        registry.addInterceptor(new UserInterceptor()).addPathPatterns("/**");
    }
}
