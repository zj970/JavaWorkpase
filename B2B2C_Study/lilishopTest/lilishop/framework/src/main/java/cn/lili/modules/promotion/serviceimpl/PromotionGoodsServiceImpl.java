package cn.lili.modules.promotion.serviceimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.lili.common.enums.PromotionTypeEnum;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.utils.BeanUtil;
import cn.lili.common.utils.DateUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.goods.entity.dto.GoodsSearchParams;
import cn.lili.modules.goods.entity.enums.GoodsAuthEnum;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.order.cart.entity.vo.CartSkuVO;
import cn.lili.modules.order.cart.entity.vo.FullDiscountVO;
import cn.lili.modules.promotion.entity.dos.PromotionGoods;
import cn.lili.modules.promotion.entity.dos.SeckillApply;
import cn.lili.modules.promotion.entity.dto.BasePromotion;
import cn.lili.modules.promotion.entity.dto.PromotionGoodsDTO;
import cn.lili.modules.promotion.entity.enums.CouponScopeTypeEnum;
import cn.lili.modules.promotion.entity.enums.PromotionStatusEnum;
import cn.lili.modules.promotion.entity.vos.CouponVO;
import cn.lili.modules.promotion.entity.vos.PromotionGoodsSearchParams;
import cn.lili.modules.promotion.entity.vos.SeckillVO;
import cn.lili.modules.promotion.mapper.PromotionGoodsMapper;
import cn.lili.modules.promotion.service.PromotionGoodsService;
import cn.lili.modules.promotion.service.SeckillApplyService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ???????????????????????????
 *
 * @author Chopper
 * @since 2021/3/18 9:22 ??????
 */
@Service
@Transactional(rollbackFor = Exception.class)

public class PromotionGoodsServiceImpl extends ServiceImpl<PromotionGoodsMapper, PromotionGoods> implements PromotionGoodsService {

    /**
     * Mongo
     */
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * Redis
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * ??????????????????
     */
    @Autowired
    private SeckillApplyService seckillApplyService;
    /**
     * ????????????
     */
    @Autowired
    private GoodsSkuService goodsSkuService;


    @Override
    public void removePromotionGoods(List<PromotionGoods> promotionGoodsList, PromotionTypeEnum promotionType) {
        for (PromotionGoods promotionGoods : promotionGoodsList) {
            promotionGoods.setDeleteFlag(true);
        }
        updateBatchById(promotionGoodsList);
    }

    @Override
    public List<PromotionGoods> findNowSkuPromotion(String skuId) {

        GoodsSku sku = goodsSkuService.getGoodsSkuByIdFromCache(skuId);
        if (sku == null) {
            return new ArrayList<>();
        }

        List<PromotionGoods> promotionGoods = new ArrayList<>(this.list(new LambdaQueryWrapper<PromotionGoods>()
                .eq(PromotionGoods::getSkuId, skuId)
//                .ge(PromotionGoods::getStartTime, new Date())
                .eq(PromotionGoods::getPromotionStatus, PromotionStatusEnum.START.name())));


        //?????????????????????????????????????????????????????????
        Query query = new Query();
        query.addCriteria(Criteria.where("promotionStatus").is(PromotionStatusEnum.START.name()));
        query.addCriteria(Criteria.where("startTime").lte(System.currentTimeMillis()));
        List<FullDiscountVO> fullDiscountVOS = mongoTemplate.find(query, FullDiscountVO.class);
        for (FullDiscountVO fullDiscountVO : fullDiscountVOS) {
            if (fullDiscountVO.getPromotionGoodsList() == null &&
                    sku.getStoreId().equals(fullDiscountVO.getStoreId())) {
                PromotionGoods p = new PromotionGoods(sku);
                p.setPromotionId(fullDiscountVO.getId());
                p.setPromotionStatus(fullDiscountVO.getPromotionStatus());
                p.setPromotionType(PromotionTypeEnum.FULL_DISCOUNT.name());
                p.setStartTime(fullDiscountVO.getStartTime());
                p.setEndTime(fullDiscountVO.getEndTime());
                promotionGoods.add(p);
            }
        }
        //??????????????????????????????????????????????????????????????????????????????????????????
        List<CouponVO> couponVOS = mongoTemplate.find(query, CouponVO.class);
        for (CouponVO couponVO : couponVOS) {
            boolean aLLScopeType = (couponVO.getPromotionGoodsList() == null
                    && couponVO.getScopeType().equals(CouponScopeTypeEnum.ALL.name())
                    && (("0").equals(couponVO.getStoreId()) || sku.getStoreId().equals(couponVO.getStoreId())));
            if (aLLScopeType) {
                PromotionGoods p = new PromotionGoods(sku);
                p.setPromotionId(couponVO.getId());
                p.setPromotionStatus(couponVO.getPromotionStatus());
                p.setPromotionType(PromotionTypeEnum.COUPON.name());
                p.setStartTime(couponVO.getStartTime());
                p.setEndTime(couponVO.getEndTime());
                promotionGoods.add(p);
            }
        }
        return promotionGoods;
    }

    @Override
    public void updatePromotion(CartSkuVO cartSkuVO) {
        Date date = DateUtil.getCurrentDayEndTime();
        //????????????????????????????????????????????????????????????????????????
        if (cartSkuVO.getUpdatePromotionTime().before(date)) {
            List<PromotionGoods> promotionGoods = this.findNowSkuPromotion(cartSkuVO.getGoodsSku().getId());
            cartSkuVO.setPromotions(promotionGoods);
            //?????????????????????
            cartSkuVO.setUpdatePromotionTime(date);
        }
    }

    @Override
    public List<PromotionGoods> getPromotionGoods(String skuId) {
        long currTime = DateUtil.getDateline();
        String currDate = DateUtil.toString(currTime, DateUtil.STANDARD_DATE_NO_UNDERLINE_FORMAT);
        LambdaQueryWrapper<PromotionGoods> queryWrapper = new LambdaQueryWrapper<PromotionGoods>()
                .eq(PromotionGoods::getSkuId, skuId)
                .le(PromotionGoods::getStartTime, currTime)
                .ge(PromotionGoods::getEndTime, currDate)
                .ne(PromotionGoods::getPromotionType, PromotionTypeEnum.PINTUAN.name())
                .ne(PromotionGoods::getPromotionType, PromotionTypeEnum.SECKILL.name());
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<PromotionGoodsDTO> getPromotionGoods(PromotionGoodsSearchParams searchParams, PageVO pageVo) {
        IPage<PromotionGoodsDTO> promotionGoodsPage = new Page<>();
        LambdaQueryWrapper<PromotionGoods> queryChainWrapper = searchParams.queryWrapper();
        List<PromotionGoodsDTO> promotionGoodsList = new ArrayList<>();
        Page<PromotionGoods> page = this.page(PageUtil.initPage(pageVo), queryChainWrapper);
        promotionGoodsPage.setSize(page.getSize());
        promotionGoodsPage.setTotal(page.getTotal());
        promotionGoodsPage.setPages(page.getPages());
        for (PromotionGoods promotionGoods : page.getRecords()) {
            PromotionGoodsDTO promotionGoodsDTO = this.wrapperPromotionGoodsDTO(promotionGoods);
            promotionGoodsList.add(promotionGoodsDTO);
        }
        promotionGoodsPage.setRecords(promotionGoodsList);
        return promotionGoodsPage;
    }

    @Override
    public IPage<PromotionGoodsDTO> getCurrentPromotionGoods(String promotionType, PageVO pageVo) {
        IPage<PromotionGoodsDTO> promotionGoodsPage = new Page<>();
        promotionGoodsPage.setSize(pageVo.getPageSize());
        promotionGoodsPage.setCurrent(pageVo.getPageNumber());
        Date now = new Date();
        Query query = new Query();
        query.addCriteria(Criteria.where("startTime").lte(now));
        query.addCriteria(Criteria.where("endTime").gte(now));
        List<PromotionGoodsDTO> promotionGoodsDTOList = new ArrayList<>();
        int total = 0;
        //???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        switch (PromotionTypeEnum.valueOf(promotionType)) {
            case FULL_DISCOUNT:
                List<FullDiscountVO> fullDiscountVOS = this.mongoTemplate.find(query, FullDiscountVO.class);
                this.setFullDiscountPromotionGoods(promotionGoodsPage, fullDiscountVOS, pageVo);
                break;
            case COUPON:
                List<CouponVO> couponVOS = this.mongoTemplate.find(query, CouponVO.class);
                for (CouponVO couponVO : couponVOS) {
                    if (couponVO != null && couponVO.getPromotionGoodsList() == null) {
                        IPage<PromotionGoodsDTO> page = this.getAllGoodsSkuToPromotionGoodsByPage(couponVO.getStoreId(), couponVO, pageVo);
                        promotionGoodsDTOList.addAll(page.getRecords());
                        total += page.getTotal();
                    }
                }
                promotionGoodsPage.setRecords(promotionGoodsDTOList.subList(0, pageVo.getPageSize()));
                promotionGoodsPage.setTotal(total);
                break;
            case SECKILL:
            case POINTS_GOODS:
                return promotionGoodsPage;
            default:
                break;
        }
        if (promotionGoodsPage.getRecords() == null || promotionGoodsPage.getRecords().isEmpty()) {
            promotionGoodsPage = this.getGoodsSkuToPromotionGoodsByPage(promotionType, pageVo);
        }
        return promotionGoodsPage;
    }


    @Override
    public Integer findInnerOverlapPromotionGoods(String promotionType, String skuId, Date startTime, Date endTime, String promotionId) {
        if (promotionId != null) {
            return this.baseMapper.selectInnerOverlapPromotionGoodsWithout(promotionType, skuId, startTime, endTime, promotionId);
        } else {
            return this.baseMapper.selectInnerOverlapPromotionGoods(promotionType, skuId, startTime, endTime);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param typeEnum    ??????????????????
     * @param promotionId ????????????id
     * @param skuId       ??????skuId
     * @return ????????????????????????
     */
    @Override
    public Integer getPromotionGoodsStock(PromotionTypeEnum typeEnum, String promotionId, String skuId) {
        String promotionStockKey = PromotionGoodsService.getPromotionGoodsStockCacheKey(typeEnum, promotionId, skuId);
        String promotionGoodsStock = stringRedisTemplate.opsForValue().get(promotionStockKey);

        //???????????????????????????????????????
        if (promotionGoodsStock != null && CharSequenceUtil.isNotEmpty(promotionGoodsStock)) {
            return Convert.toInt(promotionGoodsStock);
        }
        //????????????
        else {
            //????????????????????????????????????????????????????????????0
            PromotionGoods promotionGoods = this.getPromotionGoods(typeEnum, promotionId, skuId);
            if (promotionGoods == null) {
                return 0;
            }
            //????????????????????????????????????
            stringRedisTemplate.opsForValue().set(promotionStockKey, promotionGoods.getQuantity().toString());
            return promotionGoods.getQuantity();
        }
    }

    @Override
    public List<Integer> getPromotionGoodsStock(PromotionTypeEnum typeEnum, String promotionId, List<String> skuId) {
        //????????????????????????????????????????????????????????????0
        List<PromotionGoods> promotionGoods = this.getPromotionGoods(typeEnum, promotionId, skuId);
        //????????????
        List<Integer> result = new ArrayList<>(skuId.size());
        for (String sid : skuId) {
            Integer stock = null;
            for (PromotionGoods pg : promotionGoods) {
                if (sid.equals(pg.getSkuId())) {
                    stock = pg.getQuantity();
                }
            }
            //????????????????????????????????????????????????
            if (stock == null) {
                stock = 0;
            }
            result.add(stock);
        }
        return result;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param typeEnum    ????????????
     * @param promotionId ????????????id
     * @param skuId       ??????skuId
     * @return ????????????????????????
     */
    @Override
    public PromotionGoods getPromotionGoods(PromotionTypeEnum typeEnum, String promotionId, String skuId) {
        LambdaQueryWrapper<PromotionGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PromotionGoods::getPromotionType, typeEnum.name()).eq(PromotionGoods::getPromotionId, promotionId).eq(PromotionGoods::getSkuId, skuId);
        return this.getOne(queryWrapper);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param typeEnum    ????????????
     * @param promotionId ????????????id
     * @param skuId       ??????skuId
     * @return ????????????????????????
     */
    @Override
    public List<PromotionGoods> getPromotionGoods(PromotionTypeEnum typeEnum, String promotionId, List<String> skuId) {
        LambdaQueryWrapper<PromotionGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PromotionGoods::getPromotionType, typeEnum.name()).eq(PromotionGoods::getPromotionId, promotionId)
                .in(PromotionGoods::getSkuId, skuId);
        return this.list(queryWrapper);
    }

    /**
     * ??????????????????????????????
     *
     * @param typeEnum    ??????????????????
     * @param promotionId ????????????id
     * @param skuId       ??????skuId
     * @param quantity    ????????????????????????
     */
    @Override
    public void updatePromotionGoodsStock(PromotionTypeEnum typeEnum, String promotionId, String skuId, Integer quantity) {
        String promotionStockKey = PromotionGoodsService.getPromotionGoodsStockCacheKey(typeEnum, promotionId, skuId);
        if (typeEnum.equals(PromotionTypeEnum.SECKILL)) {
            LambdaQueryWrapper<SeckillApply> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SeckillApply::getSeckillId, promotionId).eq(SeckillApply::getSkuId, skuId);
            SeckillApply seckillApply = seckillApplyService.getOne(queryWrapper);
            if (seckillApply == null) {
                throw new ServiceException(ResultCode.SECKILL_NOT_EXIST_ERROR);
            }
            LambdaUpdateWrapper<SeckillApply> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SeckillApply::getSeckillId, promotionId).eq(SeckillApply::getSkuId, skuId);
            updateWrapper.set(SeckillApply::getQuantity, quantity);
            seckillApplyService.update(updateWrapper);
            SeckillVO seckillVO = mongoTemplate.findById(promotionId, SeckillVO.class);
            if (seckillVO != null && seckillApply.getPromotionApplyStatus() != null) {
                for (SeckillApply apply : seckillVO.getSeckillApplyList()) {
                    if (apply.getSkuId().equals(skuId)) {
                        apply.setQuantity(quantity);
                    }
                }
                this.mongoTemplate.save(seckillVO);
            }
        } else {
            LambdaUpdateWrapper<PromotionGoods> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(PromotionGoods::getPromotionType, typeEnum.name()).eq(PromotionGoods::getPromotionId, promotionId).eq(PromotionGoods::getSkuId, skuId);
            updateWrapper.set(PromotionGoods::getQuantity, quantity);
            this.update(updateWrapper);
        }

        stringRedisTemplate.opsForValue().set(promotionStockKey, quantity.toString());
    }

    private void setFullDiscountPromotionGoods(IPage<PromotionGoodsDTO> promotionGoodsPage, List<FullDiscountVO> fullDiscountVOS, PageVO pageVo) {
        List<PromotionGoodsDTO> promotionGoodsDTOList = new ArrayList<>();
        int total = 0;
        for (FullDiscountVO fullDiscountVO : fullDiscountVOS) {
            if (fullDiscountVO != null && fullDiscountVO.getPromotionGoodsList() == null) {
                IPage<PromotionGoodsDTO> page = this.getAllGoodsSkuToPromotionGoodsByPage(fullDiscountVO.getStoreId(), fullDiscountVO, pageVo);
                promotionGoodsDTOList.addAll(page.getRecords());
                total += page.getTotal();
            }
        }
        if (!fullDiscountVOS.isEmpty() && !promotionGoodsDTOList.isEmpty()) {
            promotionGoodsPage.setRecords(promotionGoodsDTOList.subList(0, pageVo.getPageSize()));
            promotionGoodsPage.setTotal(total);
        }
    }

    private IPage<PromotionGoodsDTO> getGoodsSkuToPromotionGoodsByPage(String promotionType, PageVO pageVo) {
        Date date = new Date();
        IPage<PromotionGoodsDTO> promotionGoodsPage = new Page<>();
        LambdaQueryWrapper<PromotionGoods> queryChainWrapper = new LambdaQueryWrapper<PromotionGoods>()
                .eq(PromotionGoods::getPromotionType, PromotionTypeEnum.valueOf(promotionType).name())
                .le(PromotionGoods::getStartTime, date).ge(PromotionGoods::getEndTime, date);
        List<PromotionGoodsDTO> promotionGoodsList = new ArrayList<>();
        Page<PromotionGoods> page = this.page(PageUtil.initPage(pageVo), queryChainWrapper);
        promotionGoodsPage.setTotal(page.getTotal());
        promotionGoodsPage.setPages(page.getPages());
        List<PromotionGoods> records = page.getRecords();
        for (PromotionGoods promotionGoods : records) {
            PromotionGoodsDTO promotionGoodsDTO = this.wrapperPromotionGoodsDTO(promotionGoods);
            promotionGoodsList.add(promotionGoodsDTO);
        }
        promotionGoodsPage.setRecords(promotionGoodsList);
        return promotionGoodsPage;
    }

    private PromotionGoodsDTO wrapperPromotionGoodsDTO(PromotionGoods promotionGoods) {
        PromotionGoodsDTO promotionGoodsDTO = new PromotionGoodsDTO();
        GoodsSku goodsSku = goodsSkuService.getById(promotionGoods.getSkuId());
        BeanUtil.copyProperties(promotionGoods, promotionGoodsDTO);
        if (goodsSku != null) {
            promotionGoodsDTO.setGoodsId(goodsSku.getGoodsId());
            promotionGoodsDTO.setGoodsImage(goodsSku.getThumbnail());
            promotionGoodsDTO.setGoodsName(goodsSku.getGoodsName());
            promotionGoodsDTO.setOriginPrice(goodsSku.getPrice());
        }
        return promotionGoodsDTO;
    }

    private IPage<PromotionGoodsDTO> getAllGoodsSkuToPromotionGoodsByPage(String storeId, BasePromotion promotion, PageVO pageVo) {
        IPage<PromotionGoodsDTO> promotionGoodsPage = new Page<>();
        List<PromotionGoodsDTO> promotionGoodsList = new ArrayList<>();
        GoodsSearchParams searchParams = new GoodsSearchParams();
        searchParams.setStoreId(storeId);
        searchParams.setMarketEnable(GoodsStatusEnum.UPPER.name());
        searchParams.setIsAuth(GoodsAuthEnum.PASS.name());
        searchParams.setPageNumber(pageVo.getPageNumber());
        searchParams.setPageSize(pageVo.getPageSize());
        searchParams.setSort(pageVo.getSort());
        searchParams.setOrder(pageVo.getOrder());
        IPage<GoodsSku> goodsSkuByPage = goodsSkuService.getGoodsSkuByPage(searchParams);
        //?????????????????????sku?????????????????????
        for (GoodsSku goodsSku : goodsSkuByPage.getRecords()) {
            PromotionGoodsDTO promotionGoods = new PromotionGoodsDTO(goodsSku);
            promotionGoods.setGoodsImage(goodsSku.getThumbnail());
            promotionGoods.setStartTime(promotion.getStartTime());
            promotionGoods.setEndTime(promotion.getEndTime());
            promotionGoods.setTitle(promotion.getPromotionName());
            promotionGoodsList.add(promotionGoods);
        }
        promotionGoodsPage.setSize(goodsSkuByPage.getSize());
        promotionGoodsPage.setTotal(goodsSkuByPage.getTotal());
        promotionGoodsPage.setPages(goodsSkuByPage.getPages());
        promotionGoodsPage.setRecords(promotionGoodsList);
        return promotionGoodsPage;
    }

}