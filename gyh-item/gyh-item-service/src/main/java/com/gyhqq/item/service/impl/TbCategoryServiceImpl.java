package com.gyhqq.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.item.entity.TbBrand;
import com.gyhqq.item.entity.TbCategory;
import com.gyhqq.item.mapper.TbCategoryMapper;
import com.gyhqq.item.pojo.CategoryDTO;
import com.gyhqq.item.service.TbCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务实现类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Service
public class TbCategoryServiceImpl extends ServiceImpl<TbCategoryMapper, TbCategory> implements TbCategoryService {

    @Override
    public List<CategoryDTO> findCategoryListByParentId(Long pid) {

        //设置查询条件-->QueryWrapper表达要查询哪个表,帮助设置查询条件
        QueryWrapper<TbCategory> queryWrapper = new QueryWrapper<>();
        //设置查询条件
        //queryWrapper.eq("parent_id",pid);   //不够优雅
        queryWrapper.lambda().eq(TbCategory::getParentId,pid);
        List<TbCategory> tbCategoryList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(tbCategoryList)){
            //自定义全局异常抛出
            throw  new GyhException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbCategoryList,CategoryDTO.class);
    }

    @Override
    public List<CategoryDTO> findByBrandId(Long brandId) {
        List<TbCategory> tbCategories = this.baseMapper.selectBrandJoinCategory(brandId);
        if (CollectionUtils.isEmpty(tbCategories)) {
            throw new GyhException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbCategories, CategoryDTO.class);
    }
}
