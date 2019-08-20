package com.gyhqq.search.dao;

import com.gyhqq.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 把goods的集合写入es:
 * 需要创建一个springDataES的repository-->在dao包里面的GoodsRepository接口!
 * 这个接口需要继承springDataES的针对springDataES的工具类ElasticsearchRepository
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {

}
