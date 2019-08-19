package com.gyhqq.item.entity;

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
public class TbSpuDetail extends Model<TbSpuDetail> {

private static final long serialVersionUID=1L;
    /**
     * 这儿需要加上@TableId(value = "spu_id", type = IdType.INPUT)注解,
     * 因为数据库中该字段没有设置自动增长,用MybatisPlus就会有坑!!!
     */
    @TableId(value = "spu_id", type = IdType.INPUT)
    private Long spuId;

    /**
     * 商品描述信息
     */
    private String description;

    /**
     * 通用规格参数数据
     */
    private String genericSpec;

    /**
     * 特有规格参数及可选值信息，json格式
     */
    private String specialSpec;

    /**
     * 包装清单
     */
    private String packingList;

    /**
     * 售后服务
     */
    private String afterService;

    private Date createTime;

    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.spuId;
    }

}
