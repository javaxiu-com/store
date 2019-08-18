package com.gyhqq.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//@Configuration
//public class GlobalCORSConfig {
//
//    @Bean
//    public CorsFilter corsConfig() {
////      1.添加cors的配置信息
//        CorsConfiguration cf = new CorsConfiguration();
////      允许访问的域
//        cf.addAllowedOrigin("http://manage.store.com");
////      是否允许发送cookie
//        cf.setAllowCredentials(true);
////      允许的请求方式
//        cf.addAllowedMethod(HttpMethod.GET);
//        cf.addAllowedMethod(HttpMethod.POST);
//        cf.addAllowedMethod(HttpMethod.PUT);
//        cf.addAllowedMethod(HttpMethod.DELETE);
//        cf.addAllowedMethod(HttpMethod.HEAD);
////      允许的头信息
//        cf.addAllowedHeader("*");
////      访问有效期
//        cf.setMaxAge(3600L);
////      2.添加映射路径，我们拦截一切请求
//        UrlBasedCorsConfigurationSource urlbase = new UrlBasedCorsConfigurationSource();
//        urlbase.registerCorsConfiguration("/**", cf);
////      3.返回新的CORSFilter
//        return new CorsFilter(urlbase);
//    }
//
//}

@Configuration
@EnableConfigurationProperties(CORSProperties.class)
public class GlobalCORSConfig {
    @Bean
    public CorsFilter corsFilter(CORSProperties prop) {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //1) 允许的域,不要写*，否则cookie就无法使用了
        prop.getAllowedOrigins().forEach(config::addAllowedOrigin);
        //2) 是否发送Cookie信息
        config.setAllowCredentials(prop.getAllowedCredentials());
        //3) 允许的请求方式
        prop.getAllowedMethods().forEach(config::addAllowedMethod);
        // 4）允许的头信息
        prop.getAllowedHeaders().forEach(config::addAllowedHeader);
        // 5）配置有效期
        config.setMaxAge(prop.getMaxAge());

        //2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration(prop.getFilterPath(), config);

        //3.返回新的CORSFilter.
        return new CorsFilter(configSource);
    }
}
