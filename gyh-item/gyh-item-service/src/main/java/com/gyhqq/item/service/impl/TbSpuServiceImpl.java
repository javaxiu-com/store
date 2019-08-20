package com.gyhqq.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyhqq.item.mapper.TbSpuMapper;
import com.gyhqq.item.service.TbSpuService;
import com.gyhqq.item.entity.TbSpu;
import org.springframework.stereotype.Service;

/**
 * <p>
 * spu表，该表描述的是一个抽象性的商品，比如 iphone8 服务实现类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Service
public class TbSpuServiceImpl extends ServiceImpl<TbSpuMapper, TbSpu> implements TbSpuService {

}
