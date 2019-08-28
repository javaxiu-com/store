package com.gyhqq.auth.service;

import com.gyhqq.auth.config.JwtProperties;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.auth.entity.Payload;
import com.gyhqq.common.auth.entity.UserInfo;
import com.gyhqq.common.auth.utils.JwtUtils;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.CookieUtils;
import com.gyhqq.user.DTO.UserDTO;
import com.gyhqq.user.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
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

    /**
     * 验证用户登录信息
     *
     * @param request
     * @return
     */
    public UserInfo userVerify(HttpServletRequest request, HttpServletResponse response) {
        try {
            //1.从request中获取cookie
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
            //2.验证token: 验证JWT的有效性
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
            /**
             * 检查token是否已经被注销: 获取token的id，校验黑名单
             */
            String jwtId = payload.getId();
            Boolean b = redisTemplate.hasKey(jwtId);
            if(b != null && b){
                // 抛出异常，证明token无效，直接返回401
                throw new GyhException(ExceptionEnum.UNAUTHORIZED);
            }
            /**
             * token的自刷新
             */
            //token的过期时间
            Date expiration = payload.getExpiration();
            //最小刷新间隔
            Integer minRefreshInterval = prop.getUser().getMinRefreshInterval();
            //最早刷新的时间
            DateTime refreshTime = new DateTime(expiration.getTime()).minusMinutes(minRefreshInterval);
            //用当前时间和最早刷新时间比较，如果当前时间 大于 最早刷新时间 就刷新
            if (refreshTime.isBefore(System.currentTimeMillis())) {
                log.info("token需要刷新");
                log.info("old token is {}", token);
                //开始刷新
                //1、创建token
                token = JwtUtils.generateTokenExpireInMinutes(payload.getUserInfo(), prop.getPrivateKey(), prop.getUser().getExpire());
                //2、放入cookie
                CookieUtils.newCookieBuilder()
                        .response(response)
                        .httpOnly(true) // js不能操作cookie
                        .domain(prop.getUser().getCookieDomain())
                        .name(prop.getUser().getCookieName())
                        .value(token)
                        .build();
                log.info("new token is {}", token);
            }
            //3.从payload中获取自描述信息
            return payload.getUserInfo();
        } catch (Exception e) {
            // 抛出异常，证明token无效，直接返回401
            throw new GyhException(ExceptionEnum.UNAUTHORIZED, e);
        }
    }

    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 退出登录
     * @param request
     * @param response
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try{
            //1、获取token
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
            String jwtId = payload.getId();
            //过期时间
            Date expiration = payload.getExpiration();
            //计算离过期剩余的时间
            long time = expiration.getTime() - System.currentTimeMillis();
            //大于5秒，存redis
            if(time > 5000){
                redisTemplate.opsForValue().set(jwtId,"1",time, TimeUnit.MILLISECONDS); //没放前缀,直接放的jwtId,key太长影响redis性能,变短节省redis性能,值value直接用个随意数字1
            }
            //2.删除cookie
            CookieUtils.deleteCookie(prop.getUser().getCookieName(),prop.getUser().getCookieDomain(),response);
        }catch(Exception e){
            throw new GyhException(ExceptionEnum.UNAUTHORIZED,e);
        }

    }
}
