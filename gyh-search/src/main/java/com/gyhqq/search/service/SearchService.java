package com.gyhqq.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.enums.ExceptionEnum;
import com.gyhqq.common.utils.BeanHelper;
import com.gyhqq.common.utils.JsonUtils;
import com.gyhqq.common.vo.PageResult;
import com.gyhqq.item.client.ItemClient;
import com.gyhqq.item.pojo.*;
import com.gyhqq.search.dto.GoodsDTO;
import com.gyhqq.search.dto.SearchRequest;
import com.gyhqq.search.pojo.Goods;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * 构造Goods,一个SPU对应一个Goods,一个Goods代表ES中的一条数据! 代表一条@Document(indexName = "goods", type = "docs", shards = 1, replicas = 1)
     * 调用该方法一次,生成一条@Document...
     * 生成Goods需要信息:
     *    第一：分批查询spu的服务，已经写过。
     *    第二：根据spuId查询sku的服务，已经写过
     *    第三：根据spuId查询SpuDetail的服务，已经写过
     *    第四：根据商品分类id，查询商品分类，没写过。需要一个根据多级分类id查询分类的接口
     *    第五：查询分类下可以用来搜索的规格参数：写过
     *    第六：根据id查询品牌，没写过
     * @param spuDTO
     * @return
     */
    public Goods createGoods(SpuDTO spuDTO){
        Long spuId = spuDTO.getId();
        //1.构造All: 商品相关搜索信息的拼接：名称、分类、品牌、规格信息等
        //BrandDTO brandDTO = itemClient.findById(spuDTO.getBrandId());
        String categoryName = spuDTO.getCategoryName();
        String spuName = spuDTO.getName();
        String all = categoryName + spuDTO.getBrandName()+spuName;

        //3.从item中获取skus
        //因为我们最终存入goods.setSkus()中的是json字符串,而map类型好转json,所以这里转换成map
        List<Map<String,Object>> skusMapList = new ArrayList<>();
        List<SkuDTO> skuDTOList = itemClient.findSkuListBySpuId(spuId);
        for(SkuDTO skuDTO:skuDTOList){
            Map<String,Object> map = new HashMap<>();
            map.put("id",skuDTO.getId());
            map.put("title",skuDTO.getTitle());
            map.put("image", StringUtils.substringBefore(skuDTO.getImages(),","));
            map.put("price",skuDTO.getPrice());
            skusMapList.add(map);
        }

        //4.构造price 的集合
        Set<Long> price = skuDTOList.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());
        //5.获取规格参数的名字的集合: 获取规格参数key，来自于SpecParam中当前分类下的需要搜索的规格
        List<SpecParamDTO> specParamList = itemClient.findParamList(null, spuDTO.getCid3(), true);
        //6.获取spudetal的值: 获取规格参数的值，来自于spuDetail
        SpuDetailDTO spuDetailDTO = itemClient.findSpuDetail(spuId);
        //6.1.获取通用规格参数
        String genericSpec = spuDetailDTO.getGenericSpec();
        //构造通用规格参数的map key - 参数的id ，value -- 规格的值
        Map<Long, Object> genericMap = JsonUtils.toMap(genericSpec, Long.class, Object.class);
        //6.2.获取特殊规格参数
        String specialSpec = spuDetailDTO.getSpecialSpec();
        Map<Long, List<Object>> specialMap = JsonUtils.nativeRead(specialSpec, new TypeReference<Map<Long, List<Object>>>() {
        });
        //把名字和值放一起
        //7.规格参数名字和值的map:  key -规格参数的名字  value -值
        Map<String,Object> specs = new HashMap<>();
        for(SpecParamDTO paramDTO:specParamList){
            Long paramId = paramDTO.getId();
            String key = paramDTO.getName();
            Object value = null;
            //判断是否通用
            if(paramDTO.getGeneric()){
                //通用规格
                value = genericMap.get(paramId);
            }else{
                //特有规格
                value = specialMap.get(paramId);
            }
            //判断是否是数字类型
            if(paramDTO.getIsNumeric()){
                /**
                 * 是数字类型,分段,见下面chooseSegment方法
                 */
                chooseSegment(value,paramDTO);
            }
            //添加到specs
            specs.put(key,value);
        }

        //2.根据上面方法构造Goods
        Goods goods = new Goods();
        //从spu对象中拷贝与goods对象中属性名一致的属性
        goods.setAll(all);      //商品相关搜索信息的拼接：标题、分类、品牌、规格信息等
        goods.setSkus(JsonUtils.toString(skusMapList));  //spu下的所有sku的JSON数组
        goods.setPrice(price);  //当前spu下所有sku的价格的集合
        goods.setSpecs(specs);  //当前spu的规格参数
        goods.setSubTitle(spuDTO.getSubTitle());
        goods.setCreateTime(spuDTO.getCreateTime().getTime());
        goods.setCategoryId(spuDTO.getCid3());
        goods.setBrandId(spuDTO.getBrandId());
        goods.setId(spuId);
        //返回Goods
        return goods;
    }

    /**
     * 过滤参数中有一类比较特殊，就是数值区间
     * 所以我们在存入时要进行处理：
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(Object value, SpecParamDTO p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 用户输入 关键字，进行搜索
     * @param request
     */
    public PageResult<GoodsDTO> search(SearchRequest request){
        //SpringDataES不够用,所以这里用es原生的方法: NativeSearchQueryBuilder
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //1.过滤返回的列
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //2.构造查询的条件
        //queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND)); //设定只能搜索某种!
        queryBuilder.withQuery(basicQuery(request));
        //3.构造翻页的信息: 因为索引库默认page=0,而我们api中page默认值是1,所以要减1
        int page  = request.getPage() -1;
        queryBuilder.withPageable(PageRequest.of(page,10));
        //4.进行查询
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
        //总条数
        long total = aggregatedPage.getTotalElements();
        //总页数
        int totalPages = aggregatedPage.getTotalPages();
        List<Goods> goodsList = aggregatedPage.getContent();
        if(CollectionUtils.isEmpty(goodsList)){
            throw  new GyhException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<GoodsDTO> goodsDTOS = BeanHelper.copyWithCollection(goodsList, GoodsDTO.class);
        return  new PageResult<GoodsDTO>(total,Long.valueOf(String.valueOf(totalPages)),goodsDTOS);
    }

    /**
     * 查询过滤的项
     * @param request
     * @return
     */
    public Map<String,List<?>> getFilter(SearchRequest request){
        Map<String,List<?>> filterMap = new LinkedHashMap<>();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //过滤返回的列
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
        //构造查询的条件
        //queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        queryBuilder.withQuery(basicQuery(request));
        queryBuilder.withPageable(PageRequest.of(0,1));
        //处理聚合操作
        String categoryAgg = "categoryAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAgg).field("categoryId"));
        String  brandAgg = "brandAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAgg).field("brandId"));
        //进行查询
        AggregatedPage<Goods> result = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
        Aggregations aggregations = result.getAggregations();
        LongTerms categoryTerms = aggregations.get(categoryAgg);
        List<CategoryDTO> categoryDTOList = handlerCategory(categoryTerms,filterMap); //(调下面方法)拿聚合得到的id去查值,转成name:传入categoryTerms,返回filterMap
        LongTerms brandTerms = aggregations.get(brandAgg);
        handlerBrand(brandTerms,filterMap); //(调下面方法)拿聚合得到的id去查值,转成包含图片和名字的对象:brandTerms,返回filterMap
        //只有当分类的返回值是一个的时候，才去聚合规格参数
        if(!CollectionUtils.isEmpty(categoryDTOList) && categoryDTOList.size()==1){
            //规格参数的过滤条件获取
            handlerSpec(request,filterMap,categoryDTOList.get(0).getId()); //(调下面方法)操作规格参数的过滤条件
        }
        return filterMap;
    }

    /**
     * 操作规格参数的 过滤条件
     * @param request
     * @param filterMap
     */
    private void handlerSpec(SearchRequest request, Map<String, List<?>> filterMap,Long cid) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //过滤返回的列
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
        //构造查询的条件
        //queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        queryBuilder.withQuery(basicQuery(request));

        queryBuilder.withPageable(PageRequest.of(0,1));
        //获取规格参数的名字
        List<SpecParamDTO> paramList = itemClient.findParamList(null, cid, true);
        for (SpecParamDTO paramDTO : paramList) {
            //规格参数的名字
            String specName = paramDTO.getName();
            //ES中要进行聚合的列的名字
            String fieldName = "specs."+specName;
            queryBuilder.addAggregation(AggregationBuilders.terms(specName).field(fieldName));
        }
        AggregatedPage<Goods> result = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
        Aggregations aggregations = result.getAggregations();
        for (SpecParamDTO paramDTO : paramList) {
            String name = paramDTO.getName();  //有BUG,下面try{}
            try {
                //规格参数的名字
                String specName = paramDTO.getName();
                StringTerms stringTerms = aggregations.get(specName);
                //获取聚合的桶
                List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
//                List<String> aggList = new ArrayList<>();
//                for (StringTerms.Bucket bucket : buckets) {
//                    String aggValue = bucket.getKeyAsString();
//                    aggList.add(aggValue);
//                }
                List<String> aggList = buckets.stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
                filterMap.put(specName, aggList);
            } catch (Exception e) {
               System.out.println(name);
               e.printStackTrace();
            }
        }
    }

    /**
     * 优化提取出来的代码: 构造查询的条件
     * 基本查询
     * 包含关键词 match查询
     * 和过滤条件
     * @param request
     * @return
     */
    public QueryBuilder basicQuery(SearchRequest request){
        //构造bool查询 must  filter
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //构造bool查询的must条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        //key - 分类、品牌、规格参数的名字  value - 用户选择过滤条件
        Map<String, String> filterMap = request.getFilterMap();

        for (String key : filterMap.keySet()) {
            String value = filterMap.get(key);
            //es中的 列名字
            String filterName = "specs."+key;
            if(key.equals("分类")){
                filterName = "categoryId";
            }else if(key.equals("品牌")){
                filterName = "brandId";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(filterName,value));
        }
        return boolQueryBuilder;
    }
    /**
     * 上面代码也可以用下面Map.Entry<String, String>方法来循环!
     * private QueryBuilder buildBasicQuery(SearchRequest request) {
     *     // 构建布尔查询
     *     BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
     *     // 构建基本的match查询
     *     queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
     *     // 构建过滤条件
     *     Map<String, String> filters = request.getFilters();
     *     if(!CollectionUtils.isEmpty(filters)) {
     *         for (Map.Entry<String, String> entry : filters.entrySet()) {
     *             // 获取过滤条件的key
     *             String key = entry.getKey();
     *             // 规格参数的key要做前缀specs.
     *             if ("分类".equals(key)) {
     *                 key = "categoryId";
     *             } else if ("品牌".equals(key)) {
     *                 key = "brandId";
     *             } else {
     *                 key = "specs." + key;
     *             }
     *             // value
     *             String value = entry.getValue();
     *             // 添加过滤条件
     *             queryBuilder.filter(QueryBuilders.termQuery(key, value));
     *         }
     *     }
     *     return queryBuilder;
     * }
     */

    /**
     * (调下面方法)拿聚合得到的id去查值,转成包含图片和名字的对象:brandTerms,返回filterMap
     * @param brandTerms
     * @param filterMap
     */
    private void handlerBrand(LongTerms brandTerms, Map<String, List<?>> filterMap) {
        List<LongTerms.Bucket> buckets = brandTerms.getBuckets();
        List<Long> brandIds = buckets.stream().map(LongTerms.Bucket::getKeyAsNumber)
                .map(Number::longValue)
                .collect(Collectors.toList());
        List<BrandDTO> brandDTOList = itemClient.findBrandListByIds(brandIds);
        filterMap.put("品牌",brandDTOList);
    }

    /**
     * (调下面方法)拿聚合得到的id去查值,转成name:传入categoryTerms,返回filterMap
     * @param categoryTerms
     * @param filterMap
     * @return
     */
    private List<CategoryDTO> handlerCategory(LongTerms categoryTerms, Map<String, List<?>> filterMap) {
        List<LongTerms.Bucket> buckets = categoryTerms.getBuckets();
        List<Long> cids = new ArrayList<>();
        for (LongTerms.Bucket bucket : buckets) {
            long categoryId = bucket.getKeyAsNumber().longValue();
            cids.add(categoryId);
        }
        List<CategoryDTO> categoryDTOList = itemClient.findCateogrySByCids(cids);
        filterMap.put("分类",categoryDTOList);
        return categoryDTOList;
    }
}

