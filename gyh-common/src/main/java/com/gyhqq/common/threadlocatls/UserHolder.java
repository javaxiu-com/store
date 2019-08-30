package com.gyhqq.common.threadlocatls;

/**
 * 定义容器,储存用户数据-->购物车
 */
public class UserHolder {

    private static final ThreadLocal<Long> tl = new ThreadLocal();

    public static void setUser(Long userId){
        tl.set(userId);
    }

    public static Long getUser(){
        return tl.get();
    }

    public static void deleteUser(){
        tl.remove();
    }
}
