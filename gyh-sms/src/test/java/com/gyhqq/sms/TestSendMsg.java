package com.gyhqq.sms;

import com.gyhqq.common.constants.MQConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSendMsg {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 测试发送短息
     */
    @Test
    public void sendMsg(){
        //构造消息的内容
        Map<String,String> map = new HashMap<>();
        map.put("phone","18439434385");
        map.put("code","123456");
        //把map放入mq队列消息,发送消息
        amqpTemplate.convertAndSend(MQConstants.Exchange.SMS_EXCHANGE_NAME,
                MQConstants.RoutingKey.VERIFY_CODE_KEY,
                map);
    }
}
