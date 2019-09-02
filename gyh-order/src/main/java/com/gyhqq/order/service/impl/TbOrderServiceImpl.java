package com.gyhqq.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.threadlocatls.UserHolder;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.common.utils.IdWorker;
import com.gyhqq.item.client.ItemClient;
import com.gyhqq.item.pojo.SkuDTO;
import com.gyhqq.order.DTO.CartDTO;
import com.gyhqq.order.DTO.OrderDTO;
import com.gyhqq.order.entity.TbOrder;
import com.gyhqq.order.entity.TbOrderDetail;
import com.gyhqq.order.entity.TbOrderLogistics;
import com.gyhqq.order.enums.OrderStatusEnum;
import com.gyhqq.order.mapper.TbOrderMapper;
import com.gyhqq.order.service.TbOrderDetailService;
import com.gyhqq.order.service.TbOrderLogisticsService;
import com.gyhqq.order.service.TbOrderService;
import com.gyhqq.user.DTO.AddressDTO;
import com.gyhqq.user.client.UserClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Service
public class TbOrderServiceImpl extends ServiceImpl<TbOrderMapper, TbOrder> implements TbOrderService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private TbOrderDetailService orderDetailService;
    @Autowired
    private TbOrderLogisticsService orderLogisticsService;
    @Autowired
    private UserClient userClient;


    @Override
    @Transactional
    public Long saveOrder(OrderDTO orderDTO) {
//        1、保存order信息
//        1.1、生成orderid
        long orderId = idWorker.nextId();
        //获取userid
        Long userId = UserHolder.getUser();

//        1.2、计算金额
        //获取 sku的集合信息
        List<CartDTO> cartDTOList = orderDTO.getCarts();
        //生成skuid的集合
        List<Long> skuIdList = cartDTOList.stream().map(CartDTO::getSkuId).collect(Collectors.toList());
        //创建skuid 和 num的map
        Map<Long, Integer> skuIdNumMap = cartDTOList.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        //获取sku的集合
        List<SkuDTO> skuDTOList = itemClient.findSkuListByIds(skuIdList);
        //循环用户要购买的商品，获取skuid，和num
        //总价
        long totalFee = 0;
        List<TbOrderDetail> orderDetailList = new ArrayList<>();
        for (SkuDTO skuDTO : skuDTOList) {
            Long skuId = skuDTO.getId();
            Long price = skuDTO.getPrice();
            Integer num = skuIdNumMap.get(skuId);
            totalFee = price * num;
            //构造orderdetail
            TbOrderDetail tbOrderDetail = new TbOrderDetail();
            tbOrderDetail.setOrderId(orderId);
            tbOrderDetail.setNum(num);
            tbOrderDetail.setTitle(skuDTO.getTitle());
            tbOrderDetail.setOwnSpec(skuDTO.getOwnSpec());
            tbOrderDetail.setImage(StringUtils.substringBefore(skuDTO.getImages(),","));
            tbOrderDetail.setPrice(price);
            tbOrderDetail.setSkuId(skuId);
            orderDetailList.add(tbOrderDetail);
        }
        //运费  包邮
        long postFee = 0;
        //实付金额 = 总金额 + 运费 - 优惠金额 :目前没有，默认是0
        long actualFee = totalFee + postFee - 0;
        TbOrder tbOrder = new TbOrder();
        tbOrder.setOrderId(orderId);
        tbOrder.setSourceType(2);
        tbOrder.setPostFee(postFee);
        tbOrder.setStatus(OrderStatusEnum.INIT.value());
        tbOrder.setActualFee(actualFee);
        tbOrder.setTotalFee(totalFee);
        tbOrder.setUserId(userId);
        tbOrder.setPaymentType(1);
        boolean b = this.save(tbOrder);
        if(!b){
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
//        2、保存orderdetail
        boolean bdetail = orderDetailService.saveBatch(orderDetailList);
        if(!b){
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
//        3、保存物流信息
        AddressDTO addressDTO = userClient.findAddressByUserIdAndId(userId, orderDTO.getAddressId());
        TbOrderLogistics tbOrderLogistics = BeanHelper.copyProperties(addressDTO, TbOrderLogistics.class);
        tbOrder.setOrderId(orderId);

        boolean bLogistic = orderLogisticsService.save(tbOrderLogistics);
        if(!b){
            throw new GyhException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //4、减库存
        itemClient.minusStock(skuIdNumMap);

        return orderId;
    }
}
