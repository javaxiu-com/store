package com.gyhqq.page.service;

import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.item.client.ItemClient;
import com.gyhqq.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PageService {

    @Autowired
    private ItemClient itemClient;

    public Map<String,Object> loadData(Long spuId){
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
        List<Long> cids = spuDTO.getCategoryIds();
        //categories：商品分类对象集合
        List<CategoryDTO> categoryDTOList = itemClient.findCateogrySByCids(cids);
        //brand：品牌对象
        BrandDTO brandDTO = itemClient.findById(spuDTO.getBrandId());
        //spuName：应该 是spu表中的name属性
        String spuName = spuDTO.getName();
        //subTitle：spu中 的副标题
        String subTitle = spuDTO.getSubTitle();
        //detail：商品详情SpuDetail
        SpuDetailDTO spuDetail = itemClient.findSpuDetail(spuId);
        //skus：商品spu下的sku集合
        List<SkuDTO> skuDTOList = itemClient.findSkuListBySpuId(spuId);
        //specs：规格参数这个比较 特殊
        //获取规格参数的名字
        List<SpecParamDTO> paramList = itemClient.findParamList(null, spuDTO.getCid3(), null);
        //key - gorupid  value  这个groupid 下的所用的规格参数
        Map<Long, List<SpecParamDTO>> groupParamListMap = paramList.stream().collect(Collectors.groupingBy(SpecParamDTO::getGroupId));
        //查询规格参数的分组信息,并没有包含 规格参数的
        List<SpecGroupDTO> groupDTOList = itemClient.findGroupByCid(spuDTO.getCid3());
        for (SpecGroupDTO groupDTO : groupDTOList) {
            groupDTO.setParams(groupParamListMap.get(groupDTO.getId()));
        }
        Map map = new HashMap();
        map.put("detail",spuDetail);
        map.put("skus",skuDTOList);
        map.put("specs",groupDTOList);
        map.put("categories",categoryDTOList);
        map.put("brand",brandDTO);
        map.put("spuName",spuName);
        map.put("subTitle",subTitle);
        return map;
    }


    @Autowired
    private SpringTemplateEngine templateEngine;
    private final String dirPath = "C:\\App\\Nginx\\nginx-1.16.0\\html\\item\\";
    public void createHtml(Long spuId){

        Map<String, Object> map = this.loadData(spuId);
        //获取上下文
        Context context = new Context();
        //给上下文 赋值
        context.setVariables(map);

        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir, spuId + ".html");
        try {
            PrintWriter printWriter = new PrintWriter(file,"UTF-8");
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除文件
     * @param spuId
     */
    public void delHtml(Long spuId) {
        File file = new File(dirPath, spuId + ".html");
        boolean delete = file.delete();
        System.out.println("delere==="+delete);
        if(file.exists()){
            if (!file.delete()) {
                log.error("【静态页服务】静态页删除失败，商品id：{}", spuId);
                throw new GyhException(ExceptionEnum.FILE_WRITER_ERROR);
            }
        }
    }
}
