package com.gyhqq.user.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserDTO{

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 注册手机号
     */
    private String phone;

}
