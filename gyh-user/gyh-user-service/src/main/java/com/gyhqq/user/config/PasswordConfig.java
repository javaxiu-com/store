package com.gyhqq.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Data
@Configuration
@ConfigurationProperties(prefix = "gyh.encoder.crypt")
public class PasswordConfig {
    //使用uuid，生成秘钥
    private String secret;
    //加密的强度
    private Integer strength;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        SecureRandom random = new SecureRandom(secret.getBytes());
        return new BCryptPasswordEncoder(strength,random);
    }
}
