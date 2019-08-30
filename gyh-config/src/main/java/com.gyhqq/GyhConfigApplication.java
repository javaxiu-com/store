package com.gyhqq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer  //开启配置服务
public class GyhConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(GyhConfigApplication.class,args);
    }
}
