package com.gyhqq.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyhqq.item.service.TbApplicationService;
import com.gyhqq.item.entity.TbApplication;
import com.gyhqq.item.mapper.TbApplicationMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务信息表，记录微服务的id，名称，密文，用来做服务认证 服务实现类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Service
public class TbApplicationServiceImpl extends ServiceImpl<TbApplicationMapper, TbApplication> implements TbApplicationService {

}
