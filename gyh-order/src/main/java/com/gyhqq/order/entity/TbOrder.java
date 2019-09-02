package com.gyhqq.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TbOrder extends Model<TbOrder> {

private static final long serialVersionUID=1L;

    /**
     * 订单id
     */
    @TableId(value = "order_id",type = IdType.INPUT)
    private Long orderId;

    /**
     * 总金额，单位为分
     */
    private Long totalFee;

    /**
     * 实付金额。单位:分。如:20007，表示:200元7分
     */
    private Long actualFee;

    /**
     * 优惠活动id，多个以，隔开
     */
    private String promotionIds;

    /**
     * 支付类型，1、在线支付，2、货到付款
     */
    private Integer paymentType;

    /**
     * 邮费。单位:分。如:20007，表示:200元7分
     */
    private Long postFee;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 发票类型(0无发票1普通发票，2电子发票，3增值税发票)
     */
    private Integer invoiceType;

    /**
     * 订单来源：1:app端，2：pc端，3：微信端
     */
    private Integer sourceType;

    /**
     * 订单的状态，1、未付款 2、已付款,未发货 3、已发货,未确认 4、确认收货，交易成功 5、交易取消，订单关闭 6、交易结束，已评价
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 发货时间
     */
    private Date consignTime;

    /**
     * 交易完成时间
     */
    private Date endTime;

    /**
     * 交易关闭时间
     */
    private Date closeTime;

    /**
     * 评价时间
     */
    private Date commentTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.orderId;
    }

}
