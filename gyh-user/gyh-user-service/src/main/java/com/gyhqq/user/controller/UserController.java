package com.gyhqq.user.controller;

import com.gyhqq.user.entity.TbUser;
import com.gyhqq.user.service.TbUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private TbUserService userService;

    /**
     * 数据验证功能
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> check(@PathVariable(name = "data") String data,
                                         @PathVariable(name = "type") Integer type){
        return ResponseEntity.ok(userService.check(data,type));
    }

    /**
     * 用redis临时保存验证码
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Boolean> sendCode(@RequestParam(name = "phone") String phone){
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户注册
     * @param user
     * @param code
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(TbUser user, @RequestParam("code") String code){
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
