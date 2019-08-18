package com.gyhqq.item.controller;

import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.entity.TbBrand;
import com.gyhqq.item.pojo.BrandDTO;
import com.gyhqq.item.service.TbBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private TbBrandService tbBrandService;

    /**
     * 根据条件查询Brand列表 分页
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<BrandDTO>> findBrandList(@RequestParam(name = "key",required = false) String key,
                                                              @RequestParam(name = "page",defaultValue = "1") Integer page,
                                                              @RequestParam(name = "rows",defaultValue = "10") Integer rows,
                                                              @RequestParam(name = "sortBy",required = false) String sortBy,
                                                              @RequestParam(name = "desc",defaultValue = "false") Boolean desc){
        return ResponseEntity.ok(tbBrandService.findBrandList(key, page, rows, sortBy, desc));
    }

    /**
     * 保存品牌信息
     * @param tbBrand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(TbBrand tbBrand, @RequestParam(name = "cids") List<Long> cids) {
        tbBrandService.saveBrand(tbBrand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改品牌信息
     * @param tbBrand
     * @param cids
     * @return
     */
    @PutMapping
    private ResponseEntity<Void> update(TbBrand tbBrand, @RequestParam(name = "cids") List<Long> cids) {
        tbBrandService.updateBrandAndCategory(tbBrand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
