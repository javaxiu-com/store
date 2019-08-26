package com.gyhqq.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gyhqq.user.entity.TbUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
public interface TbUserService extends IService<TbUser> {

    Boolean check(String data, Integer type);

    void sendCode(String phone);

    void register(TbUser user, String code);
}
