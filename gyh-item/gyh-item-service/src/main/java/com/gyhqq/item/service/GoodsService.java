package com.gyhqq.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.entity.TbSpu;
import com.gyhqq.item.pojo.SpuDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    private TbSpuService spuService;

    public PageResult<SpuDTO> findSpuByPage(Integer page, Integer rows, String key, Boolean saleable) {
        Page<TbSpu> page1 = new Page<>(page, rows);
        QueryWrapper<TbSpu> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isEmpty(key)) {
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
}
