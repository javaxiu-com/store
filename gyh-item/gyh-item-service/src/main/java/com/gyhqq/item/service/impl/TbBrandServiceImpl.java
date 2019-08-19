package com.gyhqq.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.entity.TbBrand;
import com.gyhqq.item.entity.TbCategoryBrand;
import com.gyhqq.item.mapper.TbBrandMapper;
import com.gyhqq.item.pojo.BrandDTO;
import com.gyhqq.item.service.TbBrandService;
import com.gyhqq.item.service.TbCategoryBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务实现类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Service
public class TbBrandServiceImpl extends ServiceImpl<TbBrandMapper, TbBrand> implements TbBrandService {

    @Autowired
    private TbCategoryBrandService tbCategoryBrandService;

    //由于要分页,返回的集合,同时还要返回分页信息,所以自己在common封装了PageResult-->包含返回的集合和总条数,总页数
    @Override
    public PageResult<BrandDTO> findBrandList(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //构造条件page和queryWrapper
        Page<TbBrand> page1 = new Page<>(page, rows);
        QueryWrapper<TbBrand> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.lambda().like(TbBrand::getLetter, key);
        }
        if (!StringUtils.isEmpty(sortBy)) {
            if (desc) {
                queryWrapper.orderByDesc(sortBy);
            } else {
                queryWrapper.orderByAsc(sortBy);
            }
        }
        //查询
        IPage<TbBrand> brandIPage = this.page(page1, queryWrapper);
        //返回前先判断下
        if (brandIPage == null || CollectionUtils.isEmpty(brandIPage.getRecords())) {
            throw new GyhException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //转换构造返回的集合
        List<BrandDTO> brandDTOList = BeanHelper.copyWithCollection(brandIPage.getRecords(), BrandDTO.class);
        //返回
        return new PageResult<BrandDTO>(page1.getTotal(), page1.getPages(), brandDTOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) //保存了两张表,需要事务
    public void saveBrand(TbBrand tbBrand, List<Long> cids) {
        //保存品牌信息
        boolean save = this.save(tbBrand);
        if (!save) {
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //拿到品牌id去保存中间表
        Long brandId = tbBrand.getId();
        //保存中间表.批量
//        ArrayList<TbCategoryBrand> list = new ArrayList<>();
//        for (Long cid : cids) {
//            TbCategoryBrand tbCategoryBrand = new TbCategoryBrand();
//            tbCategoryBrand.setBrandId(brandId);
//            tbCategoryBrand.setCategoryId(cid);
//            list.add(tbCategoryBrand);
//        }
        //优雅写法
        List<TbCategoryBrand> list = cids.stream().map(cid -> { //map映射一下
            //用流来把tbCategoryBrand放入到list里面
            TbCategoryBrand tbCategoryBrand = new TbCategoryBrand();
            tbCategoryBrand.setCategoryId(cid);
            tbCategoryBrand.setBrandId(brandId);
            return tbCategoryBrand;
        }).collect(Collectors.toList()); //收集一下,转换为list
        boolean saveBatch = tbCategoryBrandService.saveBatch(list);
        if (!saveBatch) {
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) //保存了两张表,需要事务
    public void updateBrandAndCategory(TbBrand tbBrand, List<Long> cids) {
        boolean brandB = this.updateById(tbBrand);
        if (!brandB) {
            throw new GyhException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        Long brandId = tbBrand.getId();
        if (!CollectionUtils.isEmpty(cids)) {
            //先删除
            QueryWrapper<TbCategoryBrand> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TbCategoryBrand::getBrandId, brandId);
            boolean removeB = tbCategoryBrandService.remove(queryWrapper);
            if (!removeB) {
                throw new GyhException(ExceptionEnum.DELETE_OPERATION_FAIL);
            }
            //新增
            List list = new ArrayList<TbCategoryBrand>();
            for (Long cid : cids) {
                TbCategoryBrand tbCategoryBrand = new TbCategoryBrand();
                tbCategoryBrand.setBrandId(brandId);
                tbCategoryBrand.setCategoryId(cid);
                list.add(tbCategoryBrand);
            }
            tbCategoryBrandService.saveBatch(list);
        }
    }

    /**
     * 根据cid查询品牌信息
     * 有中间表,两个表联查,需要自己创建方法写sql
     *
     * @param cid
     * @return
     */
    @Override
    public List<BrandDTO> findBrandByCid(Long cid) {
        List<TbBrand> brandDTOList = this.getBaseMapper().selectBrandListJoinCategoryId(cid);
        if (CollectionUtils.isEmpty(brandDTOList)) {
            throw new GyhException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(brandDTOList, BrandDTO.class);
    }
}
