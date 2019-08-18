package com.gyhqq;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringCloudApplication
@EnableZuulProxy
public class GyhGateway {
    public static void main(String[] args) {
        SpringApplication.run(GyhGateway.class, args);
    }
}