package com.gyhqq.item.controller;

import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.entity.TbSpu;
import com.gyhqq.item.pojo.SpuDTO;
import com.gyhqq.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询所有商品
     * 分页查询: 需要用PageResult
     *
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

    /**
     * 保存商品:
     * 返回的数据用我们拓展好的SpuDTO来接收
     * 因为返回的是json数据,所以用@RequestBody
     *
     * @param spuDTO
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO) {
        goodsService.saveGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
