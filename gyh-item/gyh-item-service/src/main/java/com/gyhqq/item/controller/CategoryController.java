package com.gyhqq.item.controller;

import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.item.entity.TbBrand;
import com.gyhqq.item.pojo.BrandDTO;
import com.gyhqq.item.pojo.CategoryDTO;
import com.gyhqq.item.service.TbBrandService;
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

    @Autowired
    private TbBrandService brandService;

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

    /**
     * 根据商品分类id，查询商品分类，没写过(这里做搜索微服务需要用到)。需要一个根据多级分类id查询分类的接口
     * 根据id的集合查询商品分类:
     * @param cids 商品分类的id集合
     * @return 分类集合
     */
    @GetMapping("/list")
    public ResponseEntity<String> findCategoryListByCids(@RequestParam(name = "cids") List<Long> cids) {
        return ResponseEntity.ok(categoryService.findCategoryListByCids(cids));
    }

    /**
     * 根据id查询品牌，没写过(这里做搜索微服务需要用到)。
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> findBrandById(@PathVariable(name = "id") Long id) {
        TbBrand tbBrand = brandService.getById(id);
        if (tbBrand == null) {
            throw new GyhException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        BrandDTO brandDTO = BeanHelper.copyProperties(tbBrand, BrandDTO.class);
        return ResponseEntity.ok(brandDTO);
    }

    /**
     * 根据分类id集合，查询分类信息集合(这里做handlerCategory方法需要用到)
     * @param cids
     * @return
     */
    @GetMapping("/categoryList")
    public ResponseEntity<List<CategoryDTO>> findCateogrySByCids(@RequestParam(name = "cids") List<Long> cids){
        return ResponseEntity.ok(categoryService.findCateogrySByCids(cids));
    }

}
