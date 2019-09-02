package com.gyhqq.user.controller;

import com.gyhqq.user.DTO.AddressDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddressController {

    /**
     * 获取用户的 地址信息
     * @param userId
     * @param id
     * @return
     */
    @GetMapping("/address")
    public ResponseEntity<AddressDTO> findAddressByUserIdAndId(@RequestParam(name = "userId") Long userId,
                                                              @RequestParam(name = "id") Long id){
        AddressDTO address = new AddressDTO();
        address.setId(1L);
        address.setStreet("北京市 海淀区");
        address.setCity("北京");
        address.setDistrict("海淀区");
        address.setAddressee("海淀区");
        address.setPhone("15800000000");
        address.setProvince("北京");
        address.setPostcode("010000");
        address.setIsDefault(true);
        return ResponseEntity.ok(address);
    }

}
