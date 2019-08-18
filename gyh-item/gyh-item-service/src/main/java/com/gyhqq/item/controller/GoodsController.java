package com.gyhqq.item.controller;

import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.pojo.SpuDTO;
import com.gyhqq.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询所有商品
     * 分页查询: 需要用PageResult
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    @RequestMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDTO>> findSpuByPage(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(name = "rows", defaultValue = "5") Integer rows,
                                                            @RequestParam(name = "key", required = false) String key,
                                                            @RequestParam(name = "saleable", required = false) Boolean saleable) {
        return ResponseEntity.ok(goodsService.findSpuByPage(page, rows, key, saleable));
    }
}
