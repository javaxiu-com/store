package com.gyhqq.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyhqq.item.service.TbCategoryBrandService;
import com.gyhqq.item.entity.TbCategoryBrand;
import com.gyhqq.item.mapper.TbCategoryBrandMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品分类和品牌的中间表，两者是多对多关系 服务实现类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Service
public class TbCategoryBrandServiceImpl extends ServiceImpl<TbCategoryBrandMapper, TbCategoryBrand> implements TbCategoryBrandService {

}
