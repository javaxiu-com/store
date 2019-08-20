package com.gyhqq.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyhqq.item.service.TbApplicationPrivilegeService;
import com.gyhqq.item.entity.TbApplicationPrivilege;
import com.gyhqq.item.mapper.TbApplicationPrivilegeMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务中间表，记录服务id以及服务能访问的目标服务的id 服务实现类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Service
public class TbApplicationPrivilegeServiceImpl extends ServiceImpl<TbApplicationPrivilegeMapper, TbApplicationPrivilege> implements TbApplicationPrivilegeService {

}
