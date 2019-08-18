package com.gyhqq.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gyhqq.item.entity.TbBrand;
import com.gyhqq.item.entity.TbCategory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 Mapper 接口
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
public interface TbCategoryMapper extends BaseMapper<TbCategory> {
    @Select("select b.id,b.name,b.letter from tb_category_brand a inner join  tb_brand b on a.brand_id = b.id where a.brand_id = #{cid}")
    List<TbCategory> selectBrandJoinCategory(@Param("cid") Long cid);
}
