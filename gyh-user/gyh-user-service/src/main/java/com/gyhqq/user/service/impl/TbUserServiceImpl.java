package com.gyhqq.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.constants.MQConstants;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.common.utils.RegexUtils;
import com.gyhqq.user.DTO.UserDTO;
import com.gyhqq.user.entity.TbUser;
import com.gyhqq.user.mapper.TbUserMapper;
import com.gyhqq.user.service.TbUserService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * data  要校验的数据
     * type  要校验的数据的类型  1-用户名  2-手机号
     *
     * @param data
     * @param type
     * @return
     */
    @Override
    public Boolean check(String data, Integer type) {
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        switch (type) {
            case 1:
                queryWrapper.lambda().eq(TbUser::getUsername, data);
                break;
            case 2:
                queryWrapper.lambda().eq(TbUser::getPhone, data);
                break;
            default:
                throw new GyhException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        //通过条件查询结果
        int count = this.count(queryWrapper);
        return count == 0;
    }

    private static final String KEY_PREFIX = "user:verify:phone:";

    @Override
    public void sendCode(String phone) {
        if (!RegexUtils.isPhone(phone)) {
            throw new GyhException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
//        1、生成一个随机码
        String code = RandomStringUtils.randomNumeric(6);
//        2、把随机码放入redis，并设置过期时间
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 3, TimeUnit.MINUTES);
//        3、把随机码和手机号，发送给rabbitmq
        Map<String, String> msg = new HashMap();
        msg.put("phone", phone);
        msg.put("code", code);
        amqpTemplate.convertAndSend(
                MQConstants.Exchange.SMS_EXCHANGE_NAME,
                MQConstants.RoutingKey.VERIFY_CODE_KEY,
                msg);
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void register(TbUser user, String code) {
        //检查手机号码是否已经注册

        //检查用户名是否已经存在

        //验证code
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!cacheCode.equals(code)) {
            throw new GyhException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        //加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //保存数据库
        boolean b = this.save(user);
        if (!b) {
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //删除redis中的code值
        redisTemplate.delete(KEY_PREFIX + user.getPhone());
    }

    @Override
    public UserDTO queryUserByPassword(String username, String password) {
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbUser::getUsername, username);
        TbUser tbUser = this.getOne(queryWrapper);
        if (tbUser == null) {
            throw new GyhException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        String dbPwd = tbUser.getPassword();
        boolean b = passwordEncoder.matches(password, dbPwd);
        if (!b) {
            throw new GyhException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        return BeanHelper.copyProperties(tbUser, UserDTO.class);
    }
}
