package com.yuu.ymall.web.api.service.impl;

import com.google.common.collect.Lists;
import com.yuu.ymall.commons.dto.BaseResult;
import com.yuu.ymall.commons.redis.RedisCacheManager;
import com.yuu.ymall.commons.utils.MapperUtil;
import com.yuu.ymall.domain.TbItem;
import com.yuu.ymall.domain.TbItemCat;
import com.yuu.ymall.domain.TbItemDesc;
import com.yuu.ymall.web.api.domain.ESItem;
import com.yuu.ymall.web.api.dto.CateProductsResult;
import com.yuu.ymall.web.api.dto.CategoryProductPageInfo;
import com.yuu.ymall.web.api.dto.ProductDet;
import com.yuu.ymall.web.api.mapper.TbItemCatMapper;
import com.yuu.ymall.web.api.mapper.TbItemDescMapper;
import com.yuu.ymall.web.api.mapper.TbItemMapper;
import com.yuu.ymall.web.api.repositories.ItemRepository;
import com.yuu.ymall.web.api.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author by Yuu
 * @classname ProductServiceImpl
 * @date 2019/7/3 18:52
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Value("${PRODUCT_ITEM}")
    private String PRODUCT_ITEM;

    @Value("${ITEM_EXPIRE}")
    private int ITEM_EXPIRE;

    @Value("${CATE_PRODUCTS}")
    private String CATE_PRODUCTS;

    @Override
    public BaseResult getProductDet(Long productId) {

        // Redis ?????????????????? key
        String redisKey = PRODUCT_ITEM + ":" + productId;

        // ?????????
        String redisJson = (String) redisCacheManager.get(redisKey);
        if (redisJson != null) {
            try {
                ProductDet productDet = MapperUtil.json2pojo(redisJson, ProductDet.class);
                // ????????????????????????
                redisCacheManager.expire(redisKey, ITEM_EXPIRE);
                return BaseResult.success(productDet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ????????????????????????????????????
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(productId);
        if (tbItem != null) {
            ProductDet productDet = new ProductDet();
            productDet.setProductId(tbItem.getId());
            productDet.setProductName(tbItem.getTitle());
            productDet.setSubTitle(tbItem.getSellPoint());
            productDet.setSalePrice(tbItem.getPrice());
            if (tbItem.getLimitNum() > tbItem.getNum()) {
                tbItem.setLimitNum(tbItem.getNum());
            }
            productDet.setLimitNum(tbItem.getLimitNum());
            productDet.setNum(tbItem.getNum());
            productDet.setProductImageBig(tbItem.getImages()[0]);

            // ??????????????????
            TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(productId);
            if (tbItemDesc != null) {
                productDet.setDetail(tbItemDesc.getItemDesc());
            }

            // ??????????????????
            productDet.setProductImageSmall(tbItem.getImages());

            // ???????????????????????????
            try {
                String productDetJson = MapperUtil.obj2json(productDet);
                redisCacheManager.set(redisKey, productDetJson);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return BaseResult.success(productDet);
        }

        return BaseResult.fail("??????????????????????????????");
    }

    @Override
    public BaseResult getByCategory(CategoryProductPageInfo categoryProductPageInfo) {

        CateProductsResult cateProductsResult = new CateProductsResult();

        // ?????? id
        Long cid = categoryProductPageInfo.getCid();

        // ?????? key
        String key = categoryProductPageInfo.getKey();

        // ???????????? 1 ???????????? 2 ???????????? 3 ?????????????????? 4 ??????????????????
        String sort = categoryProductPageInfo.getSort();

        // ?????????
        Integer page = categoryProductPageInfo.getPage() - 1;

        // ????????????
        Integer size = categoryProductPageInfo.getSize();

        // ?????????
        Integer priceGt = categoryProductPageInfo.getPriceGt();

        // ?????????
        Integer priceLt = categoryProductPageInfo.getPriceLte();

        // ElasticSearch ??????
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        if (cid != null) {
            // ??????????????????????????????
            List<TbItemCat> tbItemCats = tbItemCatMapper.selectTbCatByPid(cid);
            List<Long> cids = new ArrayList<>();
            for (TbItemCat tbItemCat : tbItemCats) {
                Long id = tbItemCat.getId();
                cids.add(id);
            }
            cids.add(cid);
            queryBuilder.withQuery(QueryBuilders.termsQuery("cid", cids));
        }
        if (key != null) {
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(key, "productName", "subTitle"));
            queryBuilder.withHighlightFields(new HighlightBuilder.Field("productName"));
        }
        if (priceGt != null && priceLt != null) {
            queryBuilder.withQuery(QueryBuilders.rangeQuery("salePrice").gte(priceGt).lte(priceLt));
        }
        if (StringUtils.isNotBlank(categoryProductPageInfo.getSort())) {
            if ("2".equals(sort)) {
                queryBuilder.withSort(SortBuilders.fieldSort("orderNum").order(SortOrder.DESC));
            } else if ("3".equals(sort)) {
                queryBuilder.withSort(SortBuilders.fieldSort("salePrice").order(SortOrder.ASC));
            } else if ("4".equals(sort)) {
                queryBuilder.withSort(SortBuilders.fieldSort("salePrice").order(SortOrder.DESC));
            }
        }
        queryBuilder.withPageable(PageRequest.of(page, size));

        // ????????????
        List<ESItem> esItemList = new ArrayList<>();
        long total = 0L;

        // ??????????????????
        if (key != null) {
            AggregatedPage<ESItem> esItems = elasticsearchTemplate.queryForPage(queryBuilder.build(), ESItem.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                    List<ESItem> esItems = new ArrayList<>();
                    for (SearchHit hit : response.getHits()) {
                        if (response.getHits().getHits().length <= 0) {
                            return null;
                        }
                        Map<String, Object> source = hit.getSource();
                        ESItem esItem = new ESItem();
                        Long id = (Long)source.get("id");
                        esItem.setId(id);
                        int cid = (int) source.get("cid");
                        esItem.setCid((long)cid);
                        Long productId = (Long) source.get("productId");
                        esItem.setProductId(productId);
                        String subTitle = (String) source.get("subTitle");
                        esItem.setSubTitle(subTitle);
                        Double salePrice = (Double) source.get("salePrice");
                        esItem.setSalePrice(salePrice);
                        String picUrl = (String) source.get("picUrl");
                        esItem.setPicUrl(picUrl);
                        int orderNum = (int) source.get("orderNum");
                        esItem.setOrderNum(orderNum);
                        int limit = (int) source.get("limit");
                        esItem.setLimit(limit);
                        HighlightField productName = hit.getHighlightFields().get("productName");
                        if (productName != null) {
                            esItem.setProductName(productName.fragments()[0].toString());
                        }
                        esItems.add(esItem);
                    }
                    if (esItems.size() > 0) {
                        return new AggregatedPageImpl<>((List<T>) esItems);
                    }
                    return null;
                }
            });
            if (esItems != null) {
                total = esItems.getTotalElements();
                esItemList = esItems.getContent();
            }
        }

        // ????????????
        if (StringUtils.isBlank(key) && cid == null) {
            Iterable<ESItem> esItems = itemRepository.search(queryBuilder.build());
            if (esItems != null) {
                esItemList = Lists.newArrayList(esItems);
                total = esItemList.size();
            }
        }

        // ???????????????
        if (cid != null) {
            Page<ESItem> search = itemRepository.search(queryBuilder.build());
            if (search != null) {
                esItemList = search.getContent();
                total = search.getTotalElements();
            }
        }

        cateProductsResult.setTotal(total);
        cateProductsResult.setData(esItemList);

        return BaseResult.success(cateProductsResult);
    }

    @Override
    public BaseResult getQuickSearch(String key) {
         // ??????????????????
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        // ????????????????????????
        searchQueryBuilder.withQuery(QueryBuilders.matchQuery("productName", key));
        // ?????????????????????
        int page = 0;
        int size = 5;
        // ??????????????????
        searchQueryBuilder.withPageable(PageRequest.of(page, size));
        Page<ESItem> searchs = itemRepository.search(searchQueryBuilder.build());
        List<ESItem> esItems = searchs.getContent();
        return BaseResult.success(esItems);
    }
}
