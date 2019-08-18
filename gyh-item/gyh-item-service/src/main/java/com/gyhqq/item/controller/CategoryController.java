package com.gyhqq.item.controller;

import com.gyhqq.item.pojo.CategoryDTO;
import com.gyhqq.item.service.TbCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private TbCategoryService categoryService;

    /**
     * 根据ParentId查询所有分类
     * @param pid
     * @return
     */
    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> findCategoryListByParentId(@RequestParam(name = "pid") Long pid) {
        return ResponseEntity.ok(categoryService.findCategoryListByParentId(pid));
    }

    /**
     * 根据品牌id获取分类信息: 修改品牌要回显数据
     * @param brandId
     * @return
     */
    @GetMapping("/of/brand")
    public ResponseEntity<List<CategoryDTO>> findByBrandId(@RequestParam(name = "id") Long brandId) {
        return ResponseEntity.ok(categoryService.findByBrandId(brandId));
    }
}
