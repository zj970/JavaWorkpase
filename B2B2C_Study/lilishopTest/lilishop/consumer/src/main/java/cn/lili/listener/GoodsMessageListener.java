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
 * 商品消息
 *
 * @author paulG
 * @since 2020/12/9
 **/
@Component
@Slf4j
@RocketMQMessageListener(topic = "${lili.data.rocketmq.goods-topic}", consumerGroup = "${lili.data.rocketmq.goods-group}")
public class GoodsMessageListener implements RocketMQListener<MessageExt> {

    /**
     * ES商品
     */
    @Autowired
    private EsGoodsIndexService goodsIndexService;
    /**
     * 店铺
     */
    @Autowired
    private StoreService storeService;
    /**
     * 商品
     */
    @Autowired
    private GoodsService goodsService;
    /**
     * 商品Sku
     */
    @Autowired
    private GoodsSkuService goodsSkuService;
    /**
     * 用户足迹
     */
    @Autowired
    private FootprintService footprintService;
    /**
     * 商品收藏
     */
    @Autowired
    private GoodsCollectionService goodsCollectionService;
    /**
     * 商品评价
     */
    @Autowired
    private List<GoodsCommentCompleteEvent> goodsCommentCompleteEvents;
    /**
     * 分销商品
     */
    @Autowired
    private DistributionGoodsService distributionGoodsService;
    /**
     * 分销员-商品关联表
     */
    @Autowired
    private DistributionSelectedGoodsService distributionSelectedGoodsService;
    /**
     * 分类
     */
    @Autowired
    private CategoryService categoryService;
    /**
     * 品牌
     */
    @Autowired
    private BrandService brandService;
    /**
     * 店铺商品分类
     */
    @Autowired
    private StoreGoodsLabelService storeGoodsLabelService;

    @Override
    public void onMessage(MessageExt messageExt) {

        switch (GoodsTagsEnum.valueOf(messageExt.getTags())) {
            //查看商品
            case VIEW_GOODS:
                FootPrint footPrint = JSONUtil.toBean(new String(messageExt.getBody()), FootPrint.class);
                footprintService.saveFootprint(footPrint);
                break;
            //生成索引
            case GENERATOR_GOODS_INDEX:
                try {
                    String goodsJsonStr = new String(messageExt.getBody());
                    Goods goods = JSONUtil.toBean(goodsJsonStr, Goods.class);
                    updateGoodsIndex(goods);
                } catch (Exception e) {
                    log.error("生成商品索引事件执行异常，商品信息 {}", new String(messageExt.getBody()));
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
                    log.error("更新商品索引事件执行异常，商品信息 {}", new String(messageExt.getBody()));
                }
                break;
            case RESET_GOODS_INDEX:
                try {
                    String goodsIdsJsonStr = new String(messageExt.getBody());
                    List<EsGoodsIndex> goodsIndices = JSONUtil.toList(goodsIdsJsonStr, EsGoodsIndex.class);
                    goodsIndexService.updateBulkIndex(goodsIndices);
                } catch (Exception e) {
                    log.error("重置商品索引事件执行异常，商品信息 {}", new String(messageExt.getBody()));
                }
                break;
            //审核商品
            case GOODS_AUDIT:
                updateGoodsNum(messageExt);
                break;
            //删除商品
            case GOODS_DELETE:
                deleteGoods(messageExt);
                updateGoodsNum(messageExt);
                break;
            //规格删除
            case SKU_DELETE:
                String message = new String(messageExt.getBody());
                List<String> skuIds = JSONUtil.toList(message, String.class);
                goodsCollectionService.deleteSkuCollection(skuIds);
                break;
            //收藏商品
            case GOODS_COLLECTION:
                storeService.updateStoreCollectionNum(new String(messageExt.getBody()));
                break;
            //商品评价
            case GOODS_COMMENT_COMPLETE:
                MemberEvaluation memberEvaluation = JSONUtil.toBean(new String(messageExt.getBody()), MemberEvaluation.class);
                for (GoodsCommentCompleteEvent goodsCommentCompleteEvent : goodsCommentCompleteEvents) {
                    try {
                        goodsCommentCompleteEvent.goodsComment(memberEvaluation);
                    } catch (Exception e) {
                        log.error("评价{},在{}业务中，状态修改事件执行异常",
                                new String(messageExt.getBody()),
                                goodsCommentCompleteEvent.getClass().getName(),
                                e);
                    }
                }
                break;
            //购买商品完成
            case BUY_GOODS_COMPLETE:
                this.goodsBuyComplete(messageExt);
                break;
            default:
                log.error("商品执行异常：{}", new String(messageExt.getBody()));
                break;
        }
    }

    /**
     * 更新商品索引
     *
     * @param goodsList 商品列表消息
     */
    private void updateGoodsIndex(List<Goods> goodsList) {
        List<EsGoodsIndex> goodsIndices = new ArrayList<>();
        for (Goods goods : goodsList) {
            //如果商品通过审核&&并且已上架
            List<GoodsSku> goodsSkuList = this.goodsSkuService.list(new LambdaQueryWrapper<GoodsSku>().eq(GoodsSku::getGoodsId, goods.getId()).gt(GoodsSku::getQuantity, 0));
            if (goods.getIsAuth().equals(GoodsAuthEnum.PASS.name())
                    && goods.getMarketEnable().equals(GoodsStatusEnum.UPPER.name())
                    && Boolean.FALSE.equals(goods.getDeleteFlag())) {
                goodsSkuList.forEach(goodsSku -> {
                    EsGoodsIndex goodsIndex = this.settingUpGoodsIndexData(goods, goodsSku);
                    goodsIndices.add(goodsIndex);
                });
            }
            //如果商品状态值不支持es搜索，那么将商品信息做下架处理
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
     * 更新商品索引
     *
     * @param goods 商品消息
     */
    private void updateGoodsIndex(Goods goods) {
        //如果商品通过审核&&并且已上架
        List<GoodsSku> goodsSkuList = this.goodsSkuService.list(new LambdaQueryWrapper<GoodsSku>().eq(GoodsSku::getGoodsId, goods.getId()));
        if (goods.getIsAuth().equals(GoodsAuthEnum.PASS.name())
                && goods.getMarketEnable().equals(GoodsStatusEnum.UPPER.name())
                && Boolean.FALSE.equals(goods.getDeleteFlag())) {
            this.generatorGoodsIndex(goods, goodsSkuList);
        }
        //如果商品状态值不支持es搜索，那么将商品信息做下架处理
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
     * 生成商品索引
     *
     * @param goods        商品信息
     * @param goodsSkuList 商品sku信息
     */
    private void generatorGoodsIndex(Goods goods, List<GoodsSku> goodsSkuList) {
        for (GoodsSku goodsSku : goodsSkuList) {
            EsGoodsIndex esGoodsOld = goodsIndexService.findById(goodsSku.getId());
            EsGoodsIndex goodsIndex = this.settingUpGoodsIndexData(goods, goodsSku);
            //如果商品库存不为0，并且es中有数据
            if (goodsSku.getQuantity() > 0 && esGoodsOld == null) {
                log.info("生成商品索引 {}", goodsIndex);
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
     * 设置商品索引的其他参数（非商品自带）
     *
     * @param goodsIndex 商品索引信息
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
     * 删除商品
     * 1.更新店铺的商品数量
     * 2.删除分销员-分销商品绑定关系
     * 3.删除分销商品
     *
     * @param messageExt 消息
     */
    private void deleteGoods(MessageExt messageExt) {
        Goods goods = JSONUtil.toBean(new String(messageExt.getBody()), Goods.class);

        //删除获取分销商品
        DistributionGoods distributionGoods = distributionGoodsService.getOne(new LambdaQueryWrapper<DistributionGoods>()
                .eq(DistributionGoods::getGoodsId, goods.getId()));

        //删除分销商品绑定关系
        distributionSelectedGoodsService.remove(new LambdaQueryWrapper<DistributionSelectedGoods>()
                .eq(DistributionSelectedGoods::getDistributionGoodsId, distributionGoods.getId()));

        //删除分销商品
        distributionGoodsService.removeById(distributionGoods.getId());
    }

    /**
     * 修改商品数量
     *
     * @param messageExt 信息体
     */
    private void updateGoodsNum(MessageExt messageExt) {

        Goods goods;
        try {
            goods = JSONUtil.toBean(new String(messageExt.getBody()), Goods.class);
            //更新店铺商品数量
            assert goods != null;
            storeService.updateStoreGoodsNum(goods.getStoreId());
        } catch (Exception e) {
            log.error("商品MQ信息错误：{}", messageExt.toString());
        }
    }

    /**
     * 商品购买完成
     * 1.更新商品购买数量
     * 2.更新SKU购买数量
     * 3.更新索引购买数量
     *
     * @param messageExt 信息体
     */
    private void goodsBuyComplete(MessageExt messageExt) {
        String goodsCompleteMessageStr = new String(messageExt.getBody());
        List<GoodsCompleteMessage> goodsCompleteMessageList = JSONUtil.toList(JSONUtil.parseArray(goodsCompleteMessageStr), GoodsCompleteMessage.class);
        for (GoodsCompleteMessage goodsCompleteMessage : goodsCompleteMessageList) {
            Goods goods = goodsService.getById(goodsCompleteMessage.getGoodsId());
            if (goods != null) {
                //更新商品购买数量
                if (goods.getBuyCount() == null) {
                    goods.setBuyCount(0);
                }
                int buyCount = goods.getBuyCount() + goodsCompleteMessage.getBuyNum();
                goodsService.update(new LambdaUpdateWrapper<Goods>()
                        .eq(Goods::getId, goodsCompleteMessage.getGoodsId())
                        .set(Goods::getBuyCount, buyCount));
            } else {
                log.error("商品Id为[" + goodsCompleteMessage.getGoodsId() + "的商品不存在，更新商品失败！");
            }
            GoodsSku goodsSku = goodsSkuService.getById(goodsCompleteMessage.getSkuId());
            if (goodsSku != null) {
                //更新商品购买数量
                if (goodsSku.getBuyCount() == null) {
                    goodsSku.setBuyCount(0);
                }
                int buyCount = goodsSku.getBuyCount() + goodsCompleteMessage.getBuyNum();
                goodsSku.setBuyCount(buyCount);
                goodsSkuService.update(goodsSku);
                goodsIndexService.updateIndex(goodsCompleteMessage.getSkuId(), new EsGoodsIndex().setBuyCount(buyCount));
            } else {
                log.error("商品SkuId为[" + goodsCompleteMessage.getGoodsId() + "的商品不存在，更新商品失败！");
            }
        }
    }
}
