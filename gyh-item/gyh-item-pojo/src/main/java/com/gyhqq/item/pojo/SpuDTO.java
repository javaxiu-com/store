package com.gyhqq.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * spu表，该表描述的是一个抽象性的商品，比如 iphone8
 * </p>
 *
 * @author GYHQQ
 * @since 2019-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpuDTO {
    /**
     * spu id
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 副标题，一般是促销信息
     */
    private String subTitle;

    /**
     * 1级类目id
     */
    private Long cid1;

    /**
     * 2级类目id
     */
    private Long cid2;

    /**
     * 3级类目id
     */
    private Long cid3;

    /**
     * 商品所属品牌id
     */
    private Long brandId;

    /**
     * 是否上架，0下架，1上架
     */
    private Boolean saleable;

    /**
     * 添加时间
     */
    private Date createTime;

    /**
     * 在新增商品信息的业务中:
     *   SpuDTO的json格式的对象,
     *   但是要包含spuDetail和Sku集合。
     *   我们需要拓展SpuDTO，来接收其中的数据
     */
    private List<SkuDTO> skus;

    private SpuDetailDTO spuDetail;

    /**
     * 商品列表分类和品牌不显示,
     * 把取得的id转换为name名称
     */
    private String categoryName; // 商品分类名称拼接

    private String brandName;// 品牌名称

    @JsonIgnore
    public List<Long> getCategoryIds() {
        List<Long> cids = Arrays.asList(cid1, cid2, cid3);
        return cids;
    }

}
