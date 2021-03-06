package cn.lili.test.elasticsearch;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.goods.entity.enums.GoodsAuthEnum;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.promotion.service.PromotionService;
import cn.lili.modules.search.entity.dos.EsGoodsAttribute;
import cn.lili.modules.search.entity.dos.EsGoodsIndex;
import cn.lili.modules.search.entity.dos.EsGoodsRelatedInfo;
import cn.lili.modules.search.entity.dto.EsGoodsSearchDTO;
import cn.lili.modules.search.repository.EsGoodsIndexRepository;
import cn.lili.modules.search.service.EsGoodsIndexService;
import cn.lili.modules.search.service.EsGoodsSearchService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author paulG
 * @since 2020/10/14
 **/
@ExtendWith(SpringExtension.class)
@SpringBootTest
class EsTest {

    @Autowired
    private EsGoodsIndexService esGoodsIndexService;

    @Autowired
    private EsGoodsIndexRepository goodsIndexRepository;

    @Autowired
    private EsGoodsSearchService goodsSearchService;

    @Autowired
    private GoodsSkuService goodsSkuService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PromotionService promotionService;


    public static void main(String[] args) {
        EsGoodsIndex goodsIndex = new EsGoodsIndex();
        goodsIndex.setGoodsName("1111");
        goodsIndex.setBuyCount(99);
        goodsIndex.setCommentNum(99);
        goodsIndex.setGrade(100D);
        goodsIndex.setHighPraiseNum(100);
        goodsIndex.setIntro("I'd like a cup of tea, please");
        goodsIndex.setIsAuth("1");
        goodsIndex.setMarketEnable("1");
        goodsIndex.setMobileIntro("I want something cold to drink");
        goodsIndex.setPoint(0);
        goodsIndex.setSelfOperated(true);
        goodsIndex.setThumbnail("picture");
        goodsIndex.setStoreCategoryPath("1");

        String ignoreField = "serialVersionUID,promotionMap,id,goodsId";

        List<EsGoodsIndex> goodsIndices = new ArrayList<>();
        Map<String, Field> fieldMap = ReflectUtil.getFieldMap(EsGoodsIndex.class);
        for (int i = 0; i < 10; i++) {
            EsGoodsIndex a = new EsGoodsIndex();
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                Object fieldValue = ReflectUtil.getFieldValue(goodsIndex, entry.getValue());
                if (fieldValue != null && !ignoreField.contains(entry.getKey())) {
                    ReflectUtil.setFieldValue(a, entry.getValue(), fieldValue);
                }
            }
            goodsIndices.add(a);
        }

        ;
//        BeanUtil.copyProperties(goodsIndex, a);
        System.out.println(cn.hutool.core.date.DateUtil.endOfDay(new Date()));
//        ReflectUtil.getFieldValue(goodsIndex, )
//        for (Object o : ReflectUtil.getFieldsValue(goodsIndex)) {
//            if (o != null) {
//                System.out.println(o);
//            }
//        }


    }

    @Test
    void searchGoods() {
        EsGoodsSearchDTO goodsSearchDTO = new EsGoodsSearchDTO();
//       goodsSearchDTO.setKeyword("???");
//        goodsSearchDTO.setProp("IETF_HTTP/3");
//       goodsSearchDTO.setPrice("100_20000");
//       goodsSearchDTO.setStoreCatId(1L);
//       goodsSearchDTO.setBrandId(123L);
//       goodsSearchDTO.setCategoryId(2L);
//       goodsSearchDTO.setNameIds(Arrays.asList("1344113311566553088", "1344113367694729216"));
        PageVO pageVo = new PageVO();
        pageVo.setPageNumber(0);
        pageVo.setPageSize(100);
        pageVo.setOrder("desc");
        pageVo.setNotConvert(true);
        SearchPage<EsGoodsIndex> esGoodsIndices = goodsSearchService.searchGoods(goodsSearchDTO, pageVo);
        Assertions.assertNotNull(esGoodsIndices);
        esGoodsIndices.getContent().forEach(System.out::println);
//       esGoodsIndices.getContent().forEach(i -> {
//           if (i.getPromotionMap() != null){
//               String s = i.getPromotionMap().keySet().parallelStream().filter(j -> j.contains(PromotionTypeEnum.FULL_DISCOUNT.name())).findFirst().orElse(null);
//               if (s != null) {
//                   FullDiscount basePromotion = (FullDiscount) i.getPromotionMap().get(s);
//                   System.out.println(basePromotion);
//               }
//           }
//       });

    }

    @Test
    void aggregationSearch() {
        EsGoodsSearchDTO goodsSearchDTO = new EsGoodsSearchDTO();
        //goodsSearchDTO.setKeyword("??????");
        //goodsSearchDTO.setProp("??????_????????????@??????_??????Pro13s");
//       goodsSearchDTO.setCategoryId("2");
//       goodsSearchDTO.setPrice("100_20000");
        PageVO pageVo = new PageVO();
        pageVo.setPageNumber(0);
        pageVo.setPageSize(10);
        pageVo.setOrder("desc");
        EsGoodsRelatedInfo selector = goodsSearchService.getSelector(goodsSearchDTO, pageVo);
        Assertions.assertNotNull(selector);
        System.out.println(JSONUtil.toJsonStr(selector));

    }

    @Test
    void init() {
        LambdaQueryWrapper<GoodsSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoodsSku::getIsAuth, GoodsAuthEnum.PASS.name());
        queryWrapper.eq(GoodsSku::getMarketEnable, GoodsStatusEnum.UPPER.name());
        List<GoodsSku> list = goodsSkuService.list(queryWrapper);
        List<EsGoodsIndex> esGoodsIndices = new ArrayList<>();
        for (GoodsSku goodsSku : list) {
            EsGoodsIndex index = new EsGoodsIndex(goodsSku);
            Map<String, Object> goodsCurrentPromotionMap = promotionService.getGoodsCurrentPromotionMap(index);
            index.setPromotionMap(goodsCurrentPromotionMap);
            esGoodsIndices.add(index);
            stringRedisTemplate.opsForValue().set(GoodsSkuService.getStockCacheKey(goodsSku.getId()), goodsSku.getQuantity().toString());
        }
        esGoodsIndexService.initIndex(esGoodsIndices);
        Assertions.assertTrue(true);
    }

    @Test
    void addIndex() {
        List<EsGoodsAttribute> esGoodsAttributeList = new ArrayList<>();
        EsGoodsAttribute attribute = new EsGoodsAttribute();
        attribute.setType(0);
        attribute.setName("??????");
        attribute.setValue("16.1?????? 6???R5 16G 512G ?????????");
        esGoodsAttributeList.add(attribute);
        attribute = new EsGoodsAttribute();
        attribute.setType(0);
        attribute.setName("??????");
        attribute.setValue("RedmiBook 18?????? ?????????");
        esGoodsAttributeList.add(attribute);
        EsGoodsIndex goodsIndex = initGoodsIndexData("122", "0|2", "140", "142", "A142", "RedmiBook 18 ????????? ??????????????????(6???R5-4500U 16G 512G 100% sRGB?????????)??? ?????? ??????????????? ?????? ?????? ", "131", "?????????????????????", 10000D);
        goodsIndex.setAttrList(esGoodsAttributeList);

        //GoodsSku goodsSkuByIdFromCache = goodsSkuService.getGoodsSkuByIdFromCache("121");
        //EsGoodsIndex goodsIndex = new EsGoodsIndex(goodsSkuByIdFromCache);


        esGoodsIndexService.addIndex(goodsIndex);

        Assertions.assertTrue(true);
    }

    @Test
    void searchAll() {
        Iterable<EsGoodsIndex> all = goodsIndexRepository.findAll();
        Assertions.assertNotNull(all);
        all.forEach(System.out::println);
    }

    @Test
    void updateIndex() {
//       EsGoodsIndex goodsIndex = new EsGoodsIndex();
//       goodsIndex.setId("121");
//       goodsIndex.setBrandId("113");
//       goodsIndex.setGoodsId("113");
//       goodsIndex.setCategoryPath("0|1");
//       goodsIndex.setBuyCount(100);
//       goodsIndex.setCommentNum(100);
//       goodsIndex.setGoodsName("?????????HP??????66 ??????AMD???14????????????????????????????????????7nm ??????R5-4500U 16G 512G 400??????????????????????????? ???");
//       goodsIndex.setGrade(100D);
//       goodsIndex.setHighPraiseNum(100);
//       goodsIndex.setIntro("I'd like a cup of tea, please");
//       goodsIndex.setIsAuth("1");
//       goodsIndex.setMarketEnable("1");
//       goodsIndex.setMobileIntro("I want something cold to drink");
//       goodsIndex.setPoint(100);
//       goodsIndex.setPrice(100D);
//       goodsIndex.setSelfOperated(true);
//       goodsIndex.setStoreId("113");
//       goodsIndex.setStoreName("???????????????????????????");
//       goodsIndex.setStoreCategoryPath("1");
//       goodsIndex.setThumbnail("picture");
//       goodsIndex.setSn("A113");
//       Map<String, BasePromotion> promotionMap = new HashMap<>();
//       Coupon coupon = new Coupon();
//       coupon.setStoreId("113");
//       coupon.setStoreName("???????????????????????????");
//       coupon.setPromotionStatus(PromotionStatusEnum.START.name());
//       coupon.setReceivedNum(0);
//       coupon.setConsumeLimit(11D);
//       coupon.setCouponLimitNum(10);
//       coupon.setCouponName("???11???10");
//       coupon.setCouponType(CouponTypeEnum.PRICE.name());
//       coupon.setGetType(CouponGetEnum.FREE.name());
//       coupon.setPrice(10D);
//       promotionMap.put(PromotionTypeEnum.COUPON.name(), coupon);
//       goodsIndex.setPromotionMap(promotionMap);
//       List<EsGoodsAttribute> esGoodsAttributeList = new ArrayList<>();
//       EsGoodsAttribute attribute = new EsGoodsAttribute();
//       attribute.setType(0);
//       attribute.setName("??????");
//       attribute.setValue("14??????");
//       esGoodsAttributeList.add(attribute);
//       esGoodsAttributeList.add(attribute);
//       attribute = new EsGoodsAttribute();
//       attribute.setName("??????");
//       attribute.setValue("??????66?????????R5-4500 8G 256G");
//       esGoodsAttributeList.add(attribute);
//       attribute = new EsGoodsAttribute();
//       attribute.setName("??????");
//       attribute.setValue("i5 8G 512G 2G??????");
//       esGoodsAttributeList.add(attribute);
//       goodsIndex.setAttrList(esGoodsAttributeList);
//       GoodsSku goodsSkuByIdFromCache = goodsSkuService.getGoodsSkuByIdFromCache("121");
//       EsGoodsIndex goodsIndex = new EsGoodsIndex(goodsSkuByIdFromCache);
        EsGoodsIndex byId = esGoodsIndexService.findById("121");
        byId.setPromotionMap(null);
        esGoodsIndexService.updateIndex(byId);
        Assertions.assertTrue(true);
    }

    @Test
    void deleteIndex() {
        esGoodsIndexService.deleteIndex(null);
        Assertions.assertTrue(true);
    }

    @Test
    void cleanPromotion() {
        esGoodsIndexService.cleanInvalidPromotion();
        Assertions.assertTrue(true);
    }


    private EsGoodsIndex initGoodsIndexData(String brandId, String categoryPath, String goodsId, String id, String sn, String goodsName, String storeId, String storeName, Double price) {
        EsGoodsIndex goodsIndex = new EsGoodsIndex();
        goodsIndex.setBuyCount(99);
        goodsIndex.setCommentNum(99);
        goodsIndex.setGrade(100D);
        goodsIndex.setHighPraiseNum(100);
        goodsIndex.setIntro("I'd like a cup of tea, please");
        goodsIndex.setIsAuth("1");
        goodsIndex.setMarketEnable("1");
        goodsIndex.setMobileIntro("I want something cold to drink");
        goodsIndex.setPoint(0);
        goodsIndex.setSelfOperated(true);
        goodsIndex.setThumbnail("picture");
        goodsIndex.setStoreCategoryPath("1");

        goodsIndex.setId(id);
        goodsIndex.setBrandId(brandId);
        goodsIndex.setGoodsId(goodsId);
        goodsIndex.setCategoryPath(categoryPath);
        goodsIndex.setGoodsName(goodsName);
        goodsIndex.setPrice(price);
        goodsIndex.setSn(sn);
        goodsIndex.setStoreId(storeId);
        goodsIndex.setStoreName(storeName);
        return goodsIndex;
    }


}
