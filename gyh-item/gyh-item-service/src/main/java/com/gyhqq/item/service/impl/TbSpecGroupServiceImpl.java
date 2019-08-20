package com.gyhqq.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyhqq.item.service.TbSpecGroupService;
import com.gyhqq.item.entity.TbSpecGroup;
import com.gyhqq.item.mapper.TbSpecGroupMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 规格参数的分组表，每个商品分类下有多个规格参数组 服务实现类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Service
public class TbSpecGroupServiceImpl extends ServiceImpl<TbSpecGroupMapper, TbSpecGroup> implements TbSpecGroupService {

}
