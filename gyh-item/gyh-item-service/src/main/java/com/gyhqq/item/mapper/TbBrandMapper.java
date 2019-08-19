package com.gyhqq.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyhqq.item.entity.TbBrand;
import com.gyhqq.item.pojo.BrandDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 Mapper 接口
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
public interface TbBrandMapper extends BaseMapper<TbBrand> {

    @Select("SELECT a.id, a.name, a.letter FROM tb_brand a, tb_category_brand b WHERE a.id= b.brand_id AND b.category_id = #{cid}")
    List<TbBrand> selectBrandListJoinCategoryId(Long cid);
}
