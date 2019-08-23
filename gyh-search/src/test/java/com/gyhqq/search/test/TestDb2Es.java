package com.gyhqq.search.test;

import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.client.ItemClient;
import com.gyhqq.item.pojo.SpuDTO;
import com.gyhqq.search.dao.GoodsRepository;
import com.gyhqq.search.pojo.Goods;
import com.gyhqq.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDb2Es {
    @Autowired
    private SearchService searchService;

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private GoodsRepository repository;

    @Test
    public void db2es(){
        /**
         * 方法一: 正确
         */
        int page = 1, rows = 100, size = 0;
        do {
            try {
                // 查询spu
                PageResult<SpuDTO> result = itemClient.findSpuByPage(page, rows, null, true);
                // 取出spu
                List<SpuDTO> items = result.getItems();
                // 转换
                List<Goods> goodsList = items
                        .stream().map(searchService::createGoods)
                        .collect(Collectors.toList());
                repository.saveAll(goodsList);
                page++;
                size = items.size();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } while (size == 100);
    }

    /**
     * 方法二: 有错误
     */
//        int page = 1;
//        int rows = 100;
//        while(true){
//            PageResult<SpuDTO> spuByPage = itemClient.findSpuByPage(page, rows, null, true);
//            if(spuByPage == null || CollectionUtils.isEmpty(spuByPage.getItems())){
//                break;
//            }
//            List<SpuDTO> spuDTOList = spuByPage.getItems();
////            List<Goods> goodsList = new ArrayList<>();
////            for(SpuDTO spuDTO:spuDTOList){
////                Goods goods = searchService.createGoods(spuDTO);
////                goodsList.add(goods);
////            }
//            List<Goods> goodsList = spuDTOList.stream().map(searchService::createGoods).collect(Collectors.toList());
//            /**
//             * 把goods的集合写入es:
//             * 需要创建一个springDataES的repository-->在dao包里面的GoodsRepository接口!
//             * 这个接口需要继承springDataES的针对springDataES的工具类ElasticsearchRepository
//             */
//            repository.saveAll(goodsList);
//            if(spuDTOList.size()<rows){
//                break;
//            }
//            page++;
//        }
//    }

    /**
     * 在这里执行上面测试之前我们需要先创建索引和Mapping(为了自己定义Mapping)
     * 我们可以通过kibana来创建索引库及映射：
     * 启动kibana,执行下面代码(记得删除注释):
     */
    /**
     * PUT /goods
     * {
     *   "settings": {
     *     "number_of_shards": 1,
     *     "number_of_replicas": 1
     *   },
     *   "mappings": {
     *     "docs":{ //type
     *       "properties": {
     *         "id":{
     *           "type": "keyword"
     *         },
     *         "subTitle":{
     *           "type": "keyword",
     *           "index": false
     *         },
     *         "skus":{
     *           "type": "keyword",
     *           "index": false
     *         },
     *         "all":{
     *           "type": "text",
     *           "analyzer": "ik_max_word"
     *         }
     *       },
     *       "dynamic_templates": [     //动态模板,只要没在上面描述,就走动态模板,发现是字符串,就把你的映射信息改成keyword
     *         {
     *           "strings": {
     *             "match_mapping_type": "string",
     *             "mapping": {
     *               "type": "keyword"  //不分词
     *             }
     *           }
     *         }
     *       ]
     *     }
     *   }
     * }
     */

}
