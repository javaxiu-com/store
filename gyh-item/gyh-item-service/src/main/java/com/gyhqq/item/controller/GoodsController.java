package com.gyhqq.item.controller;

import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.pojo.SkuDTO;
import com.gyhqq.item.pojo.SpuDTO;
import com.gyhqq.item.pojo.SpuDetailDTO;
import com.gyhqq.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 修改商品:
     * 修改商品上下架，更新spu信息，同时需要更新sku
     * @param spuDTO
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO) {
        goodsService.updateGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品上下架,两张表
     * @param spuId
     * @param saleable
     * @return
     */
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> updateSaleable(@RequestParam(name = "id",required = true) Long spuId,
                                               @RequestParam(name = "saleable",required = true) Boolean saleable) {
        goodsService.updateSaleable(spuId, saleable);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询SpuDetail接口: 修改商品回显数据!
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> findSpuDetail(@RequestParam(name = "id", required = true) Long spuId) {
        return ResponseEntity.ok(goodsService.findSpuDetail(spuId));
    }

    /**
     * 根据spuId查询Sku集合接口: 修改商品回显数据!
     * @param spuId
     * @return
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<SkuDTO>> findSkuBySpuId(@RequestParam(name = "id") Long spuId) {
        return ResponseEntity.ok(goodsService.findSkuBySpuId(spuId));
    }

    /**
     * 根据id 查询spu
     * @param spuId
     * @return
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> findSpuById(@PathVariable(name = "id") Long spuId){
        return ResponseEntity.ok(goodsService.findSpuById(spuId));
    }

}
