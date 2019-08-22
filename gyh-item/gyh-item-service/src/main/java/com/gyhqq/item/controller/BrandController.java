package com.gyhqq.item.controller;

import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.entity.TbBrand;
import com.gyhqq.item.pojo.BrandDTO;
import com.gyhqq.item.service.TbBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 根据分类id查询品牌信息
     *  新增商品回显数据!
     * @param cid
     * @return
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<BrandDTO>> findBrandByCid(@RequestParam(name = "id") Long cid) {
        return ResponseEntity.ok(tbBrandService.findBrandByCid(cid));
    }

    /**
     * 根据id 查询品牌信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> findById(@PathVariable(name = "id") Long id){
        TbBrand tbBrand = tbBrandService.getById(id);
        BrandDTO brandDTO = BeanHelper.copyProperties(tbBrand, BrandDTO.class);
        return ResponseEntity.ok(brandDTO);
    }

    /**
     * 根据id集合，查询品牌集合
     * @param brandIds
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> findBrandListByIds(@RequestParam(name = "ids") List<Long> brandIds){
        Collection<TbBrand> tbBrands = tbBrandService.listByIds(brandIds);
        if(CollectionUtils.isEmpty(tbBrands)){
            throw new GyhException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        List<BrandDTO> brandDTOList = tbBrands.stream().map(tbBrand -> {
            return BeanHelper.copyProperties(tbBrand, BrandDTO.class);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(brandDTOList);
    }
}
