package com.gyhqq.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.entity.TbBrand;
import com.gyhqq.item.pojo.BrandDTO;

import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
public interface TbBrandService extends IService<TbBrand> {

    PageResult<BrandDTO> findBrandList(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    void saveBrand(TbBrand tbBrand, List<Long> cids);

    void updateBrandAndCategory(TbBrand tbBrand, List<Long> cids);

    List<BrandDTO> findBrandByCid(Long cid);

}
