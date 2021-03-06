package cn.lili.modules.store.serviceimpl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.lili.common.enums.ResultCode;
import cn.lili.common.exception.ServiceException;
import cn.lili.common.security.AuthUser;
import cn.lili.common.security.context.UserContext;
import cn.lili.common.utils.BeanUtil;
import cn.lili.common.utils.StringUtils;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.goods.entity.dos.Goods;
import cn.lili.modules.goods.entity.enums.GoodsAuthEnum;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import cn.lili.modules.goods.service.GoodsService;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.dos.StoreCollection;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.member.service.StoreCollectionService;
import cn.lili.modules.page.service.PageDataService;
import cn.lili.modules.store.entity.dos.Store;
import cn.lili.modules.store.entity.dos.StoreDetail;
import cn.lili.modules.store.entity.dto.*;
import cn.lili.modules.store.entity.enums.StoreStatusEnum;
import cn.lili.modules.store.entity.vos.StoreSearchParams;
import cn.lili.modules.store.entity.vos.StoreVO;
import cn.lili.modules.store.mapper.StoreMapper;
import cn.lili.modules.store.service.StoreDetailService;
import cn.lili.modules.store.service.StoreService;
import cn.lili.mybatis.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ?????????????????????
 *
 * @author pikachu
 * @since 2020-03-07 16:18:56
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    /**
     * ??????
     */
    @Autowired
    private MemberService memberService;
    /**
     * ??????
     */
    @Autowired
    private GoodsService goodsService;
    /**
     * ??????SKU
     */
    @Autowired
    private GoodsSkuService goodsSkuService;
    /**
     * ????????????
     */
    @Autowired
    private StoreDetailService storeDetailService;
    /**
     * ??????
     */
    @Autowired
    private PageDataService pageDataService;
    /**
     * ????????????
     */
    @Autowired
    private StoreCollectionService storeCollectionService;

    @Override
    public IPage<StoreVO> findByConditionPage(StoreSearchParams storeSearchParams, PageVO page) {
        return this.baseMapper.getStoreList(PageUtil.initPage(page), storeSearchParams.queryWrapper());
    }

    @Override
    public StoreVO getStoreDetail() {
        AuthUser currentUser = UserContext.getCurrentUser();
        StoreVO storeVO = this.baseMapper.getStoreDetail(currentUser.getStoreId());
        storeVO.setNickName(currentUser.getNickName());
        return storeVO;
    }

    @Override
    public Store add(AdminStoreApplyDTO adminStoreApplyDTO) {

        //??????????????????????????????
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.eq("store_name", adminStoreApplyDTO.getStoreName());
        if (this.getOne(queryWrapper) != null) {
            throw new ServiceException(ResultCode.STORE_NAME_EXIST_ERROR);
        }

        Member member = memberService.getById(adminStoreApplyDTO.getMemberId());
        //????????????????????????
        if (member == null) {
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        //????????????????????????
        if (member.getHaveStore()) {
            throw new ServiceException(ResultCode.STORE_APPLY_DOUBLE_ERROR);
        }

        //????????????
        Store store = new Store(member, adminStoreApplyDTO);
        this.save(store);

        //??????????????????????????????????????????????????????????????????????????????????????????
        StoreDetail storeDetail = new StoreDetail(store, adminStoreApplyDTO);

        storeDetailService.save(storeDetail);

        //????????????-????????????
        memberService.update(new LambdaUpdateWrapper<Member>()
                .eq(Member::getId, member.getId())
                .set(Member::getHaveStore, true)
                .set(Member::getStoreId, store.getId()));
        return store;

    }

    @Override
    public Store edit(StoreEditDTO storeEditDTO) {
        if (storeEditDTO != null) {
            //???????????????????????????
            Store storeTmp = getOne(new QueryWrapper<Store>().eq("store_name", storeEditDTO.getStoreName()));
            if (storeTmp != null && !StringUtils.equals(storeTmp.getId(), storeEditDTO.getStoreId())) {
                throw new ServiceException(ResultCode.STORE_NAME_EXIST_ERROR);
            }
            //????????????????????????
            updateStoreDetail(storeEditDTO);
            //??????????????????
            return updateStore(storeEditDTO);
        } else {
            throw new ServiceException(ResultCode.STORE_NOT_EXIST);
        }
    }

    /**
     * ????????????????????????
     *
     * @param storeEditDTO ??????????????????
     */
    private Store updateStore(StoreEditDTO storeEditDTO) {
        Store store = this.getById(storeEditDTO.getStoreId());
        if (store != null) {
            BeanUtil.copyProperties(storeEditDTO, store);
            store.setId(storeEditDTO.getStoreId());
            this.updateById(store);
        }
        return store;
    }

    /**
     * ????????????????????????
     *
     * @param storeEditDTO ??????????????????
     */
    private void updateStoreDetail(StoreEditDTO storeEditDTO) {
        StoreDetail storeDetail = new StoreDetail();
        BeanUtil.copyProperties(storeEditDTO, storeDetail);
        storeDetailService.update(storeDetail, new QueryWrapper<StoreDetail>().eq("store_id", storeEditDTO.getStoreId()));
    }

    @Override
    public boolean audit(String id, Integer passed) {
        Store store = this.getById(id);
        if (store == null) {
            throw new ServiceException(ResultCode.STORE_NOT_EXIST);
        }
        if (passed == 0) {
            store.setStoreDisable(StoreStatusEnum.OPEN.value());
            //??????????????????
            pageDataService.addStorePageData(store.getId());
            //???????????? ??????????????????
            Member member = memberService.getById(store.getMemberId());
            member.setHaveStore(true);
            member.setStoreId(id);
            memberService.updateById(member);
            //????????????????????????
            storeDetailService.update(new LambdaUpdateWrapper<StoreDetail>()
                    .eq(StoreDetail::getStoreId, id)
                    .set(StoreDetail::getSettlementDay, new DateTime()));
        } else {
            store.setStoreDisable(StoreStatusEnum.REFUSED.value());
        }

        return this.updateById(store);
    }

    @Override
    public boolean disable(String id) {
        Store store = this.getById(id);
        if (store != null) {
            store.setStoreDisable(StoreStatusEnum.CLOSED.value());

            //???????????????????????????
            goodsService.underStoreGoods(id);
            return this.updateById(store);
        }

        throw new ServiceException(ResultCode.STORE_NOT_EXIST);
    }

    @Override
    public boolean enable(String id) {
        Store store = this.getById(id);
        if (store != null) {
            store.setStoreDisable(StoreStatusEnum.OPEN.value());
            return this.updateById(store);
        }
        throw new ServiceException(ResultCode.STORE_NOT_EXIST);
    }

    @Override
    public boolean applyFirstStep(StoreCompanyDTO storeCompanyDTO) {
        //???????????????????????????
        Store store = getStoreByMember();
        //??????????????????????????????????????????
        if (!Optional.ofNullable(store).isPresent()) {
            Member member = memberService.getById(UserContext.getCurrentUser().getId());
            store = new Store(member);
            BeanUtil.copyProperties(storeCompanyDTO, store);
            this.save(store);
            StoreDetail storeDetail = new StoreDetail();
            storeDetail.setStoreId(store.getId());
            BeanUtil.copyProperties(storeCompanyDTO, storeDetail);
            return storeDetailService.save(storeDetail);
        } else {
            BeanUtil.copyProperties(storeCompanyDTO, store);
            this.updateById(store);
            //??????????????????????????????????????????????????????????????????????????????????????????
            StoreDetail storeDetail = storeDetailService.getStoreDetail(store.getId());
            BeanUtil.copyProperties(storeCompanyDTO, storeDetail);
            return storeDetailService.updateById(storeDetail);
        }
    }

    @Override
    public boolean applySecondStep(StoreBankDTO storeBankDTO) {

        //???????????????????????????
        Store store = getStoreByMember();
        StoreDetail storeDetail = storeDetailService.getStoreDetail(store.getId());
        //???????????????????????????
        BeanUtil.copyProperties(storeBankDTO, storeDetail);
        return storeDetailService.updateById(storeDetail);
    }

    @Override
    public boolean applyThirdStep(StoreOtherInfoDTO storeOtherInfoDTO) {
        //???????????????????????????
        Store store = getStoreByMember();
        BeanUtil.copyProperties(storeOtherInfoDTO, store);
        this.updateById(store);

        StoreDetail storeDetail = storeDetailService.getStoreDetail(store.getId());
        //???????????????????????????
        BeanUtil.copyProperties(storeOtherInfoDTO, storeDetail);
        //????????????????????????
        storeDetail.setGoodsManagementCategory(storeOtherInfoDTO.getGoodsManagementCategory());
        //????????????????????????????????????????????????????????????
        storeDetail.setStockWarning(10);
        //????????????????????????
        storeDetailService.updateById(storeDetail);
        //??????????????????,??????????????????
        store.setStoreName(storeOtherInfoDTO.getStoreName());
        store.setStoreDisable(StoreStatusEnum.APPLYING.name());
        store.setStoreCenter(storeOtherInfoDTO.getStoreCenter());
        store.setStoreDesc(storeOtherInfoDTO.getStoreDesc());
        store.setStoreLogo(storeOtherInfoDTO.getStoreLogo());
        return this.updateById(store);
    }

    @Override
    public Integer auditNum() {
        LambdaQueryWrapper<Store> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Store::getStoreDisable, StoreStatusEnum.APPLYING.name());
        return this.count(queryWrapper);
    }

    @Override
    public Integer storeNum() {
        LambdaQueryWrapper<Store> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Store::getStoreDisable, StoreStatusEnum.OPEN.name());
        return this.count(queryWrapper);
    }

    @Override
    public Integer todayStoreNum() {
        LambdaQueryWrapper<Store> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Store::getStoreDisable, StoreStatusEnum.OPEN.name());
        queryWrapper.ge(Store::getCreateTime, DateUtil.beginOfDay(new DateTime()));
        return this.count(queryWrapper);
    }

    @Override
    public void updateStoreGoodsNum(String storeId) {
        //????????????????????????????????????????????????
        Integer goodsNum = goodsService.count(new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStoreId, storeId)
                .eq(Goods::getIsAuth, GoodsAuthEnum.PASS.name())
                .eq(Goods::getMarketEnable, GoodsStatusEnum.UPPER.name()));
        //????????????????????????
        this.update(new LambdaUpdateWrapper<Store>()
                .set(Store::getGoodsNum, goodsNum)
                .eq(Store::getId, storeId));
    }

    @Override
    public void updateStoreCollectionNum(String goodsId) {
        String storeId = goodsSkuService.getById(goodsId).getStoreId();
        //????????????????????????
        Integer collectionNum = storeCollectionService.count(new LambdaQueryWrapper<StoreCollection>()
                .eq(StoreCollection::getStoreId, storeId));
        //????????????????????????
        this.update(new LambdaUpdateWrapper<Store>()
                .set(Store::getCollectionNum, collectionNum)
                .eq(Store::getId, storeId));
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    private Store getStoreByMember() {
        LambdaQueryWrapper<Store> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Store::getMemberId, UserContext.getCurrentUser().getId());
        return this.getOne(lambdaQueryWrapper);
    }

}