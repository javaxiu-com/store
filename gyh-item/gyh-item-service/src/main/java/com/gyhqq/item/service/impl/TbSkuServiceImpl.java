package com.gyhqq.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyhqq.item.service.TbSkuService;
import com.gyhqq.item.entity.TbSku;
import com.gyhqq.item.mapper.TbSkuMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8 服务实现类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Service
public class TbSkuServiceImpl extends ServiceImpl<TbSkuMapper, TbSku> implements TbSkuService {

}
