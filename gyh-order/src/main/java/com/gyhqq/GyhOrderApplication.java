package com.gyhqq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.gyhqq.order.mapper")
public class GyhOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GyhOrderApplication.class, args);
    }
}