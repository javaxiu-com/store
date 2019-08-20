package com.gyhqq.item.client;

import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.pojo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("item-service")
public interface ItemClient {
    /**
     * 根据分类id集合，查询分类信息
     * @param cids
     * @return
     */
    @GetMapping("/category/list")
    String findCateogryListByCids(List<Long> cids);

    /**
     * 根据id 查询品牌信息
     * @param id
     * @return
     */
    @GetMapping("/brand/{id}")
    BrandDTO findById(@PathVariable(name = "id") Long id);

    /**
     * 分页获取spu
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    @GetMapping("/spu/page")
    PageResult<SpuDTO> findSpuByPage(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                     @RequestParam(name = "rows", defaultValue = "5") Integer rows,
                                     @RequestParam(name = "key", required = false) String key,
                                     @RequestParam(name = "saleable", required = false) Boolean saleable);

    /**
     * 查询spuDetail
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail")
    SpuDetailDTO findSpuDetail(@RequestParam(name = "id") Long spuId);

    /**
     * 根据spuid 查询sku的集合
     * @param spuId
     * @return
     */
    @GetMapping("/sku/of/spu")
    List<SkuDTO> findSkuListBySpuId(@RequestParam(name = "id") Long spuId);

    /**
     * 查询 规格参数
     * @param gid
     * @return
     */
    @GetMapping("/spec/params")
    List<SpecParamDTO> findParamList(@RequestParam(name = "gid", required = false) Long gid,
                                     @RequestParam(name = "cid", required = false) Long cid,
                                     @RequestParam(name = "searching", required = false) Boolean searching);
}
