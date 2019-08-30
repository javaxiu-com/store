package com.gyhqq.cart.interceptors;

import com.gyhqq.common.auth.entity.Payload;
import com.gyhqq.common.auth.entity.UserInfo;
import com.gyhqq.common.auth.utils.JwtUtils;
import com.gyhqq.common.threadlocatls.UserHolder;
import com.gyhqq.common.utils.CookieUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器-->UserHolder
 */
public class UserInterceptor implements HandlerInterceptor {
    /**
     * 从request中获取token，解析出userid
     * 放入当前线程的threadlocal中
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{
            //获取token
            String token = CookieUtils.getCookieValue(request, "GYH_TOKEN");
            //解析payload
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
            //把用户信息放入 当前线程的 threadlocal中
            UserHolder.setUser(payload.getUserInfo().getId());
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 当前线程结束后,把数据从当前线程的threadlocal中清除掉-->因为threadlocalMap中的key是弱引用,但value不是,垃圾回收期不会自动回收,会造成内存溢出!
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.deleteUser();
    }
}
