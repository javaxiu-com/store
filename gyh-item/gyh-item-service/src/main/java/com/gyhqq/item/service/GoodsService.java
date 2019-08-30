package com.gyhqq.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.constants.MQConstants;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.entity.*;
import com.gyhqq.item.pojo.SkuDTO;
import com.gyhqq.item.pojo.SpuDTO;
import com.gyhqq.item.pojo.SpuDetailDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private TbSpuService spuService;

    public PageResult<SpuDTO> findSpuByPage(Integer page, Integer rows, String key, Boolean saleable) {
        Page<TbSpu> page1 = new Page<>(page, rows);
        QueryWrapper<TbSpu> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.lambda().like(TbSpu::getName, key);
        }
        if (saleable != null) {
            queryWrapper.lambda().eq(TbSpu::getSaleable, saleable);
        }
        IPage<TbSpu> spuIPage = spuService.page(page1, queryWrapper);
        if (spuIPage == null || CollectionUtils.isEmpty(spuIPage.getRecords())) {
            throw new GyhException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //构造返回值
        List<SpuDTO> spuDTOList = BeanHelper.copyWithCollection(spuIPage.getRecords(), SpuDTO.class);
        handlerCategoryAndBrandName(spuDTOList);
        return new PageResult<>(spuIPage.getTotal(), spuIPage.getPages(), spuDTOList);
    }

    /**
     * 商品列表分类和品牌不显示,
     * 把取得的id转换为name名称
     */
    @Autowired
    private TbCategoryService CategoryService;

    @Autowired
    private TbBrandService brandService;

    private void handlerCategoryAndBrandName(List<SpuDTO> spuDTOList) {
        for (SpuDTO spuDTO : spuDTOList) {
            List<Long> categoryIds = spuDTO.getCategoryIds();
            Collection<TbCategory> tbCategories = CategoryService.listByIds(categoryIds);
            String categoryName = tbCategories.stream().map(TbCategory::getName).collect(Collectors.joining("/"));
            spuDTO.setCategoryName(categoryName);
            //处理品牌名称
            TbBrand tbBrand = brandService.getById(spuDTO.getBrandId());
            spuDTO.setBrandName(tbBrand.getName());
        }
    }

    /**
     * 保存商品
     */
    @Autowired
    private TbSpuDetailService spuDetailService;
    @Autowired
    private TbSkuService skuService;

    @Transactional(rollbackFor = Exception.class)
    public void saveGoods(SpuDTO spuDTO) {
        //保存Spu表
        //转换出tbSpu
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        boolean bSpu = spuService.save(tbSpu);
        if (!bSpu) {
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        //保存SpuDetail: 需要先获取spuId
        Long spuId = tbSpu.getId();
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        TbSpuDetail spuDetail = BeanHelper.copyProperties(spuDetailDTO, TbSpuDetail.class);
        //给TbSpuDetail表设置上它们的主表id
        spuDetail.setSpuId(spuId);
        /**
         * 这儿需要去TbSpuDetail加上@TableId(value = "spu_id", type = IdType.INPUT)注解,
         * 因为数据库中该字段没有设置自动增长,用MybatisPlus就会有坑!!!
         */
        boolean saveSpuDetail = spuDetailService.save(spuDetail);
        if (!saveSpuDetail) {
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        //保存Sku-->一对多,批量保存!
        List<SkuDTO> skus = spuDTO.getSkus();
        /**
         * 方法一:
         */
//        List<TbSku> tbSkus = BeanHelper.copyWithCollection(skus, TbSku.class);
//        for (TbSku tbSku : tbSkus) {
//            tbSku.setSpuId(spuId);
//        }
        /**
         * 方法二:
         * map是映射,也就是循环
         */
        List<TbSku> tbSkuList = skus.stream().map(skuDTO -> {
            skuDTO.setSpuId(spuId);
            return BeanHelper.copyProperties(skuDTO, TbSku.class);
        }).collect(Collectors.toList());
        boolean saveBatch = skuService.saveBatch(tbSkuList);
        if (!saveBatch) {
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    @Autowired
    private AmqpTemplate amqpTemplate;
    /**
     * 更新上下架,需要事务
     *
     * @param spuId
     * @param saleable
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleable(Long spuId, Boolean saleable) {
        //更新spu
        TbSpu tbSpu = new TbSpu().setId(spuId).setSaleable(saleable);
        boolean bSpu = spuService.updateById(tbSpu);
        if (!bSpu) {
            throw new GyhException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        //更新sku
        UpdateWrapper<TbSku> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(TbSku::getSpuId, spuId);
        updateWrapper.lambda().set(TbSku::getEnable, saleable);
        boolean update = skuService.update(updateWrapper);
        if (!update) {
            throw new GyhException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        /**
         * 发送消息到中间件
         */
        //说明交换机
        String itemSaleable = MQConstants.RoutingKey.ITEM_DOWN_KEY;
        //判断是否上下架
        if(saleable){
            //说明RoutingKey
            itemSaleable = MQConstants.RoutingKey.ITEM_UP_KEY;
        }
        amqpTemplate.convertAndSend(MQConstants.Exchange.ITEM_EXCHANGE_NAME, itemSaleable,spuId);
    }

    public SpuDetailDTO findSpuDetail(Long spuId) {
        TbSpuDetail tbSpuDetail = spuDetailService.getById(spuId);
        if (tbSpuDetail == null) {
            throw new GyhException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbSpuDetail, SpuDetailDTO.class);
    }

    public List<SkuDTO> findSkuBySpuId(Long spuId) {
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSku::getSpuId, spuId);
        List<TbSku> skuList = skuService.list(queryWrapper);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new GyhException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(skuList, SkuDTO.class);
    }

    /**
     * 修改商品:(需要事务)
     * 修改商品上下架，更新spu信息，同时需要更新sku
     * @param spuDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(SpuDTO spuDTO) {
        //修改spu表
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        boolean b = spuService.updateById(tbSpu);
        if (!b) {
            throw new GyhException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        //修改spuDetail表
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDetail, TbSpuDetail.class);
        boolean b1 = spuDetailService.updateById(tbSpuDetail);
        if (!b1) {
            throw new GyhException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        //删除sku表
        Long spuId = spuDTO.getId();
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSku::getSpuId,spuId);
        boolean remove = skuService.remove(queryWrapper);
        //int delete = skuService.getBaseMapper().delete(queryWrapper); //用该方法删除能知道删除了多少条!
        if (!remove) {
            throw new GyhException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
        //新增sku表
        List<SkuDTO> skus = spuDTO.getSkus();
        List<TbSku> tbSkuList = skus.stream().map(skuDTO -> {
            skuDTO.setSpuId(spuId);
            return BeanHelper.copyProperties(skuDTO, TbSku.class);
        }).collect(Collectors.toList());
        boolean b2 = skuService.saveBatch(tbSkuList);
        if (!b2) {
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    public SpuDTO findSpuById(Long spuId) {
        TbSpu tbSpu = spuService.getById(spuId);
        if (tbSpu == null) {
            throw new GyhException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        SpuDTO spuDTO = BeanHelper.copyProperties(tbSpu, SpuDTO.class);
        return spuDTO;
    }

    public List<SkuDTO> findSkuListByIds(List<Long> ids) {
        Collection<TbSku> tbSkuCollection = skuService.listByIds(ids);
        if(CollectionUtils.isEmpty(tbSkuCollection)){
            throw new GyhException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<SkuDTO> skuDTOList = tbSkuCollection.stream().map(tbSku -> {
            return BeanHelper.copyProperties(tbSku, SkuDTO.class);
        }).collect(Collectors.toList());
        return skuDTOList;
    }
}
