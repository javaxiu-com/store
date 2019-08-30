package com.gyhqq.cart.entity;

import lombok.Data;

/**
 * 购物车数据结构是双层Map: Map<string<string,string>>
 * Cart是Json对象实体类,放入内层Map的Value
 */
@Data
public class Cart {
    private Long skuId;// 商品id
    private String title;// 标题
    private String image;// 图片
    private Long price;// 加入购物车时的价格 ,单位是分
    private Integer num;// 购买数量
    private String ownSpec;// 商品规格参数
}