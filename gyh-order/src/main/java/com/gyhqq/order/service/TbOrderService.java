package com.gyhqq.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gyhqq.order.DTO.OrderDTO;
import com.gyhqq.order.entity.TbOrder;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
public interface TbOrderService extends IService<TbOrder> {
    Long saveOrder(OrderDTO orderDTO);
}
