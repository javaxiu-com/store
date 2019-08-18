package com.gyhqq.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyhqq.item.entity.TbCategoryBrand;

/**
 * <p>
 * 商品分类和品牌的中间表，两者是多对多关系 Mapper 接口
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
public interface TbCategoryBrandMapper extends BaseMapper<TbCategoryBrand> {

}
