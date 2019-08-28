package com.gyhqq.auth.service;

import com.gyhqq.auth.config.JwtProperties;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.auth.entity.UserInfo;
import com.gyhqq.common.auth.utils.JwtUtils;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.CookieUtils;
import com.gyhqq.user.DTO.UserDTO;
import com.gyhqq.user.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    @Autowired
    private JwtProperties prop;

    @Autowired
    private UserClient userClient;

    private static final String USER_ROLE = "role_user";

    /**
     * 处理登录业务
     */
    public void login(String username, String password, HttpServletResponse response) {
        try {
            // 查询用户,获取用户信息
            UserDTO user = userClient.queryUserByUsernameAndPassword(username, password);
            // 生成userInfo, 没写权限功能，暂时都用guest: 使用不敏感信息组成一个自描述信息
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), USER_ROLE);
            // 生成token,使用JWT
            String token = JwtUtils.generateTokenExpireInMinutes(userInfo, prop.getPrivateKey(), prop.getUser().getExpire());
            // 写入cookie,把token放入cookie
            CookieUtils.newCookieBuilder()
                    .response(response) // response,用于写cookie
                    .httpOnly(true) // 保证安全防止XSS攻击，不允许JS操作cookie
                    .domain(prop.getUser().getCookieDomain()) // 设置domain
                    .name(prop.getUser().getCookieName())
                    .value(token) // 设置cookie名称和值
                    .build();// 写cookie
        } catch (Exception e) {
            throw new GyhException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }
}
