package com.gyhqq.common.auth.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Payload<T> {
    //JWT的id,用来标识唯一的JWT
    private String id;
    //用户自定义信息
    private T userInfo;
    //过期时间
    private Date expiration;
}
