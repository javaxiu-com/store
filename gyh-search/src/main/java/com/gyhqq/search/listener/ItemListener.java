package com.gyhqq.search.listener;

import com.gyhqq.common.constants.MQConstants;
import com.gyhqq.item.client.ItemClient;
import com.gyhqq.item.pojo.SpuDTO;
import com.gyhqq.search.dao.GoodsRepository;
import com.gyhqq.search.pojo.Goods;
import com.gyhqq.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component  //放入Spring Ioc容器
public class ItemListener {

    @Autowired
    private SearchService searchService;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private GoodsRepository repository;

    /**
     * 上架
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name= MQConstants.Queue.SEARCH_ITEM_UP,durable = "true"),
            exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_UP_KEY
    ))
    public void itemUp(Long spuId){
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
        Goods goods = searchService.createGoods(spuDTO);
        repository.save(goods);
    }

    /**
     * 下架
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name= MQConstants.Queue.SEARCH_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_DOWN_KEY
    ))
    public void itemDown(Long spuId){
        System.out.println("删除spuid=="+spuId);
        repository.deleteById(spuId);
    }
}
