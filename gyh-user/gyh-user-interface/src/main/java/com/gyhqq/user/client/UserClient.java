package com.gyhqq.user.client;

import com.gyhqq.user.DTO.AddressDTO;
import com.gyhqq.user.DTO.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {
    /**
     * 根据用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    @GetMapping("query")
    UserDTO queryUserByUsernameAndPassword(@RequestParam("username") String username, @RequestParam("password") String password);


    /**
     * 获取用户的 地址信息
     * @param userId
     * @param id
     * @return
     */
    @GetMapping("/address")
    AddressDTO findAddressByUserIdAndId(@RequestParam(name = "userId") Long userId,
                                        @RequestParam(name = "id") Long id);
}