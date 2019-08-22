package com.gyhqq.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gyhqq.item.entity.TbCategory;
import com.gyhqq.item.pojo.CategoryDTO;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
public interface TbCategoryService extends IService<TbCategory> {

    List<CategoryDTO> findCategoryListByParentId(Long pid);

    List<CategoryDTO> findByBrandId(Long brandId);

    String findCategoryListByCids(List<Long> cids);

    List<CategoryDTO> findCateogrySByCids(List<Long> cids);
}
