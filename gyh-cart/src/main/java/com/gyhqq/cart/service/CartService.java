package com.gyhqq.cart.service;

import com.gyhqq.cart.entity.Cart;
import com.gyhqq.common.threadlocatls.UserHolder;
import com.gyhqq.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Redis的Key命名规约
     */
    private static final String KEY_PREFIX = "ly:cart:uid:";
    /**
     * 保存 购物车数据
     * @param cart
     */
    public void addCart(Cart cart) {
        //redis的key
        String key = KEY_PREFIX + UserHolder.getUser();
        //hash 的key
        String hashKey = cart.getSkuId().toString();
        //获取hash操作的对象,并绑定用户id-->相当于把Redis的Key已经定义好了,只需要操作hash里面的值就行了
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(key);

        //本次用户选择的数量
        Integer num = cart.getNum();
        Boolean b = boundHashOps.hasKey(hashKey);
        if(b != null && b){
            //合并数量
            String cartJson = boundHashOps.get(hashKey);
            //redis中存储的cart
            cart = JsonUtils.toBean(cartJson, Cart.class);
            cart.setNum(num+cart.getNum());
        }
        //把数据放入redis中
        redisTemplate.opsForHash().put(key,hashKey, JsonUtils.toString(cart)); //双层Map结构

    }
}
