package com.gyhqq.cart.controller;

import com.gyhqq.cart.entity.Cart;
import com.gyhqq.cart.service.CartService;
import com.gyhqq.common.threadlocatls.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 增加购物车内的商品
     * 参数是Json所以用@RequestBody
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 获取用户的购物车数据
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> getCart(){
        return ResponseEntity.ok(cartService.getUserCart());
    }

    /**
     * 修改商品数量
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public  ResponseEntity<Void> update(@RequestParam(name = "id")Long skuId,
                                        @RequestParam(name = "num") Integer num){
        cartService.update(skuId,num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    /**
     * 删除购物车数据
     * @param skuId
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long skuId){
        cartService.delete(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 合并购物车
     * @param cartList
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<Void> addBatchCart(@RequestBody List<Cart> cartList){

        cartService.addBatchCart(cartList);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
