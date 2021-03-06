package cn.lili.listener;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import cn.lili.event.GoodsCommentCompleteEvent;
import cn.lili.modules.distribution.entity.dos.DistributionGoods;
import cn.lili.modules.distribution.entity.dos.DistributionSelectedGoods;
import cn.lili.modules.distribution.service.DistributionGoodsService;
import cn.lili.modules.distribution.service.DistributionSelectedGoodsService;
import cn.lili.modules.goods.entity.dos.Brand;
import cn.lili.modules.goods.entity.dos.Category;
import cn.lili.modules.goods.entity.dos.Goods;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.goods.entity.dto.GoodsCompleteMessage;
import cn.lili.modules.goods.entity.dto.GoodsParamsDTO;
import cn.lili.modules.goods.entity.enums.GoodsAuthEnum;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import cn.lili.modules.goods.service.BrandService;
import cn.lili.modules.goods.service.CategoryService;
import cn.lili.modules.goods.service.GoodsService;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.member.entity.dos.FootPrint;
import cn.lili.modules.member.entity.dos.MemberEvaluation;
import cn.lili.modules.member.service.FootprintService;
import cn.lili.modules.member.service.GoodsCollectionService;
import cn.lili.modules.search.entity.dos.EsGoodsIndex;
import cn.lili.modules.search.service.EsGoodsIndexService;
import cn.lili.modules.store.entity.dos.StoreGoodsLabel;
import cn.lili.modules.store.service.StoreGoodsLabelService;
import cn.lili.modules.store.service.StoreService;
import cn.lili.rocketmq.tags.GoodsTagsEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ????????????
 *
 * @author paulG
 * @since 2020/12/9
 **/
@Component
@Slf4j
@RocketMQMessageListener(topic = "${lili.data.rocketmq.goods-topic}", consumerGroup = "${lili.data.rocketmq.goods-group}")
public class GoodsMessageListener implements RocketMQListener<MessageExt> {

    /**
     * ES??????
     */
    @Autowired
    private EsGoodsIndexService goodsIndexService;
    /**
     * ??????
     */
    @Autowired
    private StoreService storeService;
    /**
     * ??????
     */
    @Autowired
    private GoodsService goodsService;
    /**
     * ??????Sku
     */
    @Autowired
    private GoodsSkuService goodsSkuService;
    /**
     * ????????????
     */
    @Autowired
    private FootprintService footprintService;
    /**
     * ????????????
     */
    @Autowired
    private GoodsCollectionService goodsCollectionService;
    /**
     * ????????????
     */
    @Autowired
    private List<GoodsCommentCompleteEvent> goodsCommentCompleteEvents;
    /**
     * ????????????
     */
    @Autowired
    private DistributionGoodsService distributionGoodsService;
    /**
     * ?????????-???????????????
     */
    @Autowired
    private DistributionSelectedGoodsService distributionSelectedGoodsService;
    /**
     * ??????
     */
    @Autowired
    private CategoryService categoryService;
    /**
     * ??????
     */
    @Autowired
    private BrandService brandService;
    /**
     * ??????????????????
     */
    @Autowired
    private StoreGoodsLabelService storeGoodsLabelService;

    @Override
    public void onMessage(MessageExt messageExt) {

        switch (GoodsTagsEnum.valueOf(messageExt.getTags())) {
            //????????????
            case VIEW_GOODS:
                FootPrint footPrint = JSONUtil.toBean(new String(messageExt.getBody()), FootPrint.class);
                footprintService.saveFootprint(footPrint);
                break;
            //????????????
            case GENERATOR_GOODS_INDEX:
                try {
                    String goodsJsonStr = new String(messageExt.getBody());
                    Goods goods = JSONUtil.toBean(goodsJsonStr, Goods.class);
                    updateGoodsIndex(goods);
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????????????? {}", new String(messageExt.getBody()));
                }
                break;
            case UPDATE_GOODS_INDEX:
                try {
                    String goodsIdsJsonStr = new String(messageExt.getBody());
                    List<Goods> goodsList = new ArrayList<>();
                    for (String goodsId : JSONUtil.toList(goodsIdsJsonStr, String.class)) {
                        Goods goods = goodsService.getById(goodsId);
                        goodsList.add(goods);
                    }
                    this.updateGoodsIndex(goodsList);
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????????????? {}", new String(messageExt.getBody()));
                }
                break;
            case RESET_GOODS_INDEX:
                try {
                    String goodsIdsJsonStr = new String(messageExt.getBody());
                    List<EsGoodsIndex> goodsIndices = JSONUtil.toList(goodsIdsJsonStr, EsGoodsIndex.class);
                    goodsIndexService.updateBulkIndex(goodsIndices);
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????????????? {}", new String(messageExt.getBody()));
                }
                break;
            //????????????
            case GOODS_AUDIT:
                updateGoodsNum(messageExt);
                break;
            //????????????
            case GOODS_DELETE:
                deleteGoods(messageExt);
                updateGoodsNum(messageExt);
                break;
            //????????????
            case SKU_DELETE:
                String message = new String(messageExt.getBody());
                List<String> skuIds = JSONUtil.toList(message, String.class);
                goodsCollectionService.deleteSkuCollection(skuIds);
                break;
            //????????????
            case GOODS_COLLECTION:
                storeService.updateStoreCollectionNum(new String(messageExt.getBody()));
                break;
            //????????????
            case GOODS_COMMENT_COMPLETE:
                MemberEvaluation memberEvaluation = JSONUtil.toBean(new String(messageExt.getBody()), MemberEvaluation.class);
                for (GoodsCommentCompleteEvent goodsCommentCompleteEvent : goodsCommentCompleteEvents) {
                    try {
                        goodsCommentCompleteEvent.goodsComment(memberEvaluation);
                    } catch (Exception e) {
                        log.error("??????{},???{}??????????????????????????????????????????",
                                new String(messageExt.getBody()),
                                goodsCommentCompleteEvent.getClass().getName(),
                                e);
                    }
                }
                break;
            //??????????????????
            case BUY_GOODS_COMPLETE:
                this.goodsBuyComplete(messageExt);
                break;
            default:
                log.error("?????????????????????{}", new String(messageExt.getBody()));
                break;
        }
    }

    /**
     * ??????????????????
     *
     * @param goodsList ??????????????????
     */
    private void updateGoodsIndex(List<Goods> goodsList) {
        List<EsGoodsIndex> goodsIndices = new ArrayList<>();
        for (Goods goods : goodsList) {
            //????????????????????????&&???????????????
            List<GoodsSku> goodsSkuList = this.goodsSkuService.list(new LambdaQueryWrapper<GoodsSku>().eq(GoodsSku::getGoodsId, goods.getId()).gt(GoodsSku::getQuantity, 0));
            if (goods.getIsAuth().equals(GoodsAuthEnum.PASS.name())
                    && goods.getMarketEnable().equals(GoodsStatusEnum.UPPER.name())
                    && Boolean.FALSE.equals(goods.getDeleteFlag())) {
                goodsSkuList.forEach(goodsSku -> {
                    EsGoodsIndex goodsIndex = this.settingUpGoodsIndexData(goods, goodsSku);
                    goodsIndices.add(goodsIndex);
                });
            }
            //??????????????????????????????es?????????????????????????????????????????????
            else {
                for (GoodsSku goodsSku : goodsSkuList) {
                    EsGoodsIndex esGoodsOld = goodsIndexService.findById(goodsSku.getId());
                    if (esGoodsOld != null) {
                        goodsIndexService.deleteIndexById(goodsSku.getId());
                    }
                }
            }
        }
        goodsIndexService.updateBulkIndex(goodsIndices);
    }

    /**
     * ??????????????????
     *
     * @param goods ????????????
     */
    private void updateGoodsIndex(Goods goods) {
        //????????????????????????&&???????????????
        List<GoodsSku> goodsSkuList = this.goodsSkuService.list(new LambdaQueryWrapper<GoodsSku>().eq(GoodsSku::getGoodsId, goods.getId()));
        if (goods.getIsAuth().equals(GoodsAuthEnum.PASS.name())
                && goods.getMarketEnable().equals(GoodsStatusEnum.UPPER.name())
                && Boolean.FALSE.equals(goods.getDeleteFlag())) {
            this.generatorGoodsIndex(goods, goodsSkuList);
        }
        //??????????????????????????????es?????????????????????????????????????????????
        else {
            for (GoodsSku goodsSku : goodsSkuList) {
                EsGoodsIndex esGoodsOld = goodsIndexService.findById(goodsSku.getId());
                if (esGoodsOld != null) {
                    goodsIndexService.deleteIndexById(goodsSku.getId());
                }
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param goods        ????????????
     * @param goodsSkuList ??????sku??????
     */
    private void generatorGoodsIndex(Goods goods, List<GoodsSku> goodsSkuList) {
        for (GoodsSku goodsSku : goodsSkuList) {
            EsGoodsIndex esGoodsOld = goodsIndexService.findById(goodsSku.getId());
            EsGoodsIndex goodsIndex = this.settingUpGoodsIndexData(goods, goodsSku);
            //????????????????????????0?????????es????????????
            if (goodsSku.getQuantity() > 0 && esGoodsOld == null) {
                log.info("?????????????????? {}", goodsIndex);
                this.goodsIndexService.addIndex(goodsIndex);
            } else if (goodsSku.getQuantity() > 0 && esGoodsOld != null) {
                goodsIndexService.updateIndex(goodsIndex);
            }
        }
    }

    private EsGoodsIndex settingUpGoodsIndexData(Goods goods, GoodsSku goodsSku) {
        EsGoodsIndex goodsIndex = new EsGoodsIndex(goodsSku);
        if (goods.getParams() != null && !goods.getParams().isEmpty()) {
            List<GoodsParamsDTO> goodsParamDTOS = JSONUtil.toList(goods.getParams(), GoodsParamsDTO.class);
            goodsIndex = new EsGoodsIndex(goodsSku, goodsParamDTOS);
        }
        goodsIndex.setIsAuth(goods.getIsAuth());
        goodsIndex.setMarketEnable(goods.getMarketEnable());
        this.settingUpGoodsIndexOtherParam(goodsIndex);
        return goodsIndex;
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param goodsIndex ??????????????????
     */
    private void settingUpGoodsIndexOtherParam(EsGoodsIndex goodsIndex) {
        List<Category> categories = categoryService.listByIdsOrderByLevel(Arrays.asList(goodsIndex.getCategoryPath().split(",")));
        if (!categories.isEmpty()) {
            goodsIndex.setCategoryNamePath(ArrayUtil.join(categories.stream().map(Category::getName).toArray(), ","));
        }
        Brand brand = brandService.getById(goodsIndex.getBrandId());
        if (brand != null) {
            goodsIndex.setBrandName(brand.getName());
            goodsIndex.setBrandUrl(brand.getLogo());
        }
        if (goodsIndex.getStoreCategoryPath() != null && CharSequenceUtil.isNotEmpty(goodsIndex.getStoreCategoryPath())) {
            List<StoreGoodsLabel> storeGoodsLabels = storeGoodsLabelService.listByStoreIds(Arrays.asList(goodsIndex.getStoreCategoryPath().split(",")));
            if (!storeGoodsLabels.isEmpty()) {
                goodsIndex.setStoreCategoryNamePath(ArrayUtil.join(storeGoodsLabels.stream().map(StoreGoodsLabel::getLabelName).toArray(), ","));
            }
        }
    }


    /**
     * ????????????
     * 1.???????????????????????????
     * 2.???????????????-????????????????????????
     * 3.??????????????????
     *
     * @param messageExt ??????
     */
    private void deleteGoods(MessageExt messageExt) {
        Goods goods = JSONUtil.toBean(new String(messageExt.getBody()), Goods.class);

        //????????????????????????
        DistributionGoods distributionGoods = distributionGoodsService.getOne(new LambdaQueryWrapper<DistributionGoods>()
                .eq(DistributionGoods::getGoodsId, goods.getId()));

        //??????????????????????????????
        distributionSelectedGoodsService.remove(new LambdaQueryWrapper<DistributionSelectedGoods>()
                .eq(DistributionSelectedGoods::getDistributionGoodsId, distributionGoods.getId()));

        //??????????????????
        distributionGoodsService.removeById(distributionGoods.getId());
    }

    /**
     * ??????????????????
     *
     * @param messageExt ?????????
     */
    private void updateGoodsNum(MessageExt messageExt) {

        Goods goods;
        try {
            goods = JSONUtil.toBean(new String(messageExt.getBody()), Goods.class);
            //????????????????????????
            assert goods != null;
            storeService.updateStoreGoodsNum(goods.getStoreId());
        } catch (Exception e) {
            log.error("??????MQ???????????????{}", messageExt.toString());
        }
    }

    /**
     * ??????????????????
     * 1.????????????????????????
     * 2.??????SKU????????????
     * 3.????????????????????????
     *
     * @param messageExt ?????????
     */
    private void goodsBuyComplete(MessageExt messageExt) {
        String goodsCompleteMessageStr = new String(messageExt.getBody());
        List<GoodsCompleteMessage> goodsCompleteMessageList = JSONUtil.toList(JSONUtil.parseArray(goodsCompleteMessageStr), GoodsCompleteMessage.class);
        for (GoodsCompleteMessage goodsCompleteMessage : goodsCompleteMessageList) {
            Goods goods = goodsService.getById(goodsCompleteMessage.getGoodsId());
            if (goods != null) {
                //????????????????????????
                if (goods.getBuyCount() == null) {
                    goods.setBuyCount(0);
                }
                int buyCount = goods.getBuyCount() + goodsCompleteMessage.getBuyNum();
                goodsService.update(new LambdaUpdateWrapper<Goods>()
                        .eq(Goods::getId, goodsCompleteMessage.getGoodsId())
                        .set(Goods::getBuyCount, buyCount));
            } else {
                log.error("??????Id???[" + goodsCompleteMessage.getGoodsId() + "??????????????????????????????????????????");
            }
            GoodsSku goodsSku = goodsSkuService.getById(goodsCompleteMessage.getSkuId());
            if (goodsSku != null) {
                //????????????????????????
                if (goodsSku.getBuyCount() == null) {
                    goodsSku.setBuyCount(0);
                }
                int buyCount = goodsSku.getBuyCount() + goodsCompleteMessage.getBuyNum();
                goodsSku.setBuyCount(buyCount);
                goodsSkuService.update(goodsSku);
                goodsIndexService.updateIndex(goodsCompleteMessage.getSkuId(), new EsGoodsIndex().setBuyCount(buyCount));
            } else {
                log.error("??????SkuId???[" + goodsCompleteMessage.getGoodsId() + "??????????????????????????????????????????");
            }
        }
    }
}
