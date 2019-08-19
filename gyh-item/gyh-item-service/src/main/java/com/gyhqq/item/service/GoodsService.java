package com.gyhqq.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.entity.TbSku;
import com.gyhqq.item.entity.TbSpu;
import com.gyhqq.item.entity.TbSpuDetail;
import com.gyhqq.item.pojo.SkuDTO;
import com.gyhqq.item.pojo.SpuDTO;
import com.gyhqq.item.pojo.SpuDetailDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
        return new PageResult<>(spuIPage.getTotal(), spuIPage.getPages(), spuDTOList);
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

}
