package com.gyhqq.item.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试SpringCloudBus
 */
@RefreshScope  //此注解用于刷新SpringCloudBus配置
@RestController
@RequestMapping("/test")
public class TestConfigController {
    /**
     * Spring从配置文件取值的注解: @Value
     */
    @Value("${gyh.msg}")  //此注解用于Spring从配置文件取值
    private String gyhmsg;

    @GetMapping("/testbus")
    public ResponseEntity<String> testBus() {
        return ResponseEntity.ok(gyhmsg);
    }

}
