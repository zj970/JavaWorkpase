package com.yuu.ymall.web.api.service.impl;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuu.ymall.commons.consts.Consts;
import com.yuu.ymall.commons.dto.BaseResult;
import com.yuu.ymall.commons.redis.RedisCacheManager;
import com.yuu.ymall.commons.utils.IDUtil;
import com.yuu.ymall.commons.utils.MapperUtil;
import com.yuu.ymall.domain.*;
import com.yuu.ymall.web.api.common.config.AlipayConfig;
import com.yuu.ymall.web.api.common.utils.IPInfoUtil;
import com.yuu.ymall.web.api.dto.*;
import com.yuu.ymall.web.api.mapper.*;
import com.yuu.ymall.web.api.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author by Yuu
 * @classname OrderServiceImpl
 * @date 2019/7/6 2:14
 */
@Service
@Transactional(readOnly = false)
public class OrderServiceImpl implements OrderService {

    @Value("${ADD_ORDER}")
    private String ADD_ORDER;

    @Value("${CART_PRE}")
    private String CART_PRE;

    @Value("${ORDER_PAY}")
    private String ORDER_PAY;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private TbMemberMapper tbMemberMapper;

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Value("${PRODUCT_ITEM}")
    private String PRODUCT_ITEM;

    @Transactional
    @Override
    public BaseResult addOrder(OrderInfo orderInfo, HttpServletRequest request) {

        // ?????? id
        Long userId = orderInfo.getUserId();

        // ???????????????
        String username = orderInfo.getUserName();

        // ????????????
        List<CartProduct> goods = orderInfo.getGoodsList();

        if (userId == null || StringUtils.isBlank(username) || goods == null || goods.size() == 0) {
            return BaseResult.fail("??????????????????");
        }

        // ?????? ip ??????
        String ip = IPInfoUtil.getIpAddr(request);
        if("0:0:0:0:0:0:0:1".equals(ip)){
            ip="127.0.0.1";
        }

        // Redis key?????????????????????
        String redisKey = ADD_ORDER + ":" + ip;
        String temp = (String) redisCacheManager.get(redisKey);
        if (StringUtils.isNotBlank(temp)) {
            return BaseResult.fail("?????????????????????????????????????????????");
        }

        TbMember tbMember = tbMemberMapper.selectByPrimaryKey(userId);
        if (tbMember == null) {
            return BaseResult.fail("????????????????????????");
        }

        // ????????????
        TbOrder tbOrder = new TbOrder();
        tbOrder.setId(String.valueOf(IDUtil.getRandomId()));
        tbOrder.setUserId(userId);
        if (StringUtils.isBlank(tbMember.getUsername())) {
            tbOrder.setBuyerNick(tbMember.getPhone());
        } else {
            tbOrder.setBuyerNick(tbMember.getUsername());
        }
        tbOrder.setPayment(orderInfo.getOrderTotal());
        tbOrder.setCreated(new Date());
        tbOrder.setUpdated(new Date());
        // 0: ????????? 1: ????????? 2: ????????? 3: ????????? 4: ???????????? 5: ????????????
        tbOrder.setStatus(0);

        // ????????????
        tbOrderMapper.insert(tbOrder);

        // ??????????????????
        List<CartProduct> goodsList = orderInfo.getGoodsList();
        for (CartProduct cartProduct : goodsList) {
            TbOrderItem tbOrderItem = new TbOrderItem();
            Long orderItemId = IDUtil.getRandomId();
            tbOrderItem.setId(orderItemId.toString());
            tbOrderItem.setItemId(cartProduct.getProductId().toString());
            tbOrderItem.setOrderId(tbOrder.getId());
            tbOrderItem.setNum(cartProduct.getProductNum());
            tbOrderItem.setTitle(cartProduct.getProductName());
            tbOrderItem.setPrice(cartProduct.getSalePrice());
            tbOrderItem.setTotalFee(cartProduct.getSalePrice().multiply(BigDecimal.valueOf(cartProduct.getProductNum())));
            tbOrderItem.setPicPath(cartProduct.getProductImg());
            tbOrderItemMapper.insert(tbOrderItem);

            // ????????????????????????????????????
            // Redis key
            String cartProductKey = CART_PRE + ":" + orderInfo.getUserId();
            // Hash Key
            String hashKey = cartProduct.getProductId().toString();
            // ????????????
            redisCacheManager.deleteHash(cartProductKey, hashKey);
        }

        // ??????????????????
        TbOrderShipping tbOrderShipping = new TbOrderShipping();
        tbOrderShipping.setOrderId(tbOrder.getId());
        tbOrderShipping.setReceiverName(orderInfo.getUserName());
        tbOrderShipping.setReceiverAddress(orderInfo.getStreetName());
        tbOrderShipping.setReceiverPhone(orderInfo.getTel());
        tbOrderShipping.setCreated(new Date());
        tbOrderShipping.setUpdated(new Date());
        tbOrderShippingMapper.insert(tbOrderShipping);

        // ????????????????????????
        for (CartProduct good : goods) {
            Long productId = good.getProductId();
            // ????????????
            tbItemMapper.reduceItemNum(productId, good.getProductNum());
            // ?????? Redis ??????????????????
            String redisItemKey = PRODUCT_ITEM + ":" + productId;
            redisCacheManager.del(redisItemKey);
        }

        // ?????? Redis ip ??????
        redisCacheManager.set(redisKey, "ADD_ORDER");
        redisCacheManager.expire(redisKey, 60);
        return BaseResult.success((Object)tbOrder.getId());
    }

    @Override
    public BaseResult getOrderDet(String orderId) {

        Order order = new Order();

        // ??????????????????
        TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(orderId);
        if (tbOrder == null) {
            return BaseResult.fail("??????id????????????????????????");
        }

        // ?????? id
        order.setOrderId(tbOrder.getId());

        // ?????? id
        order.setUserId(tbOrder.getUserId().toString());

        // ????????????
        order.setOrderStatus(tbOrder.getStatus());

        // ??????????????????????????????
        if (order.getOrderStatus() == 0) {
            Date createDate = tbOrder.getCreated();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createDate);
            calendar.add(Calendar.HOUR, 2);
            String countTime = calendar.getTime().getTime() + "";
            order.setCountTime(countTime);
        }

        // ??????????????????
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createDate = simpleDateFormat.format(tbOrder.getCreated());
        order.setCreateDate(createDate);

        // ??????????????????
        if (tbOrder.getPaymentTime() != null) {
            String payDate = simpleDateFormat.format(tbOrder.getPaymentTime());
            order.setPayDate(payDate);
        }

        // ??????????????????
        if (tbOrder.getConsignTime() != null) {
            String consignDate = simpleDateFormat.format(tbOrder.getConsignTime());
            order.setConsignDate(consignDate);
        }

        // ??????????????????
        if (tbOrder.getCloseTime() != null) {
            String closeDate = simpleDateFormat.format(tbOrder.getCloseTime());
            order.setCloseDate(closeDate);
        }

        // ??????????????????
        if (tbOrder.getEndTime() != null && tbOrder.getStatus() == 4) {
            String endDate = simpleDateFormat.format(tbOrder.getEndTime());
            order.setFinishDate(endDate);
        }

        // ??????
        TbOrderShipping tbOrderShipping = tbOrderShippingMapper.selectByPrimaryKey(tbOrder.getId());
        TbAddress tbAddress = new TbAddress();
        tbAddress.setUserName(tbOrderShipping.getReceiverName());
        tbAddress.setStreetName(tbOrderShipping.getReceiverAddress());
        tbAddress.setTel(tbOrderShipping.getReceiverPhone());
        order.setTbAddress(tbAddress);

        // ????????????
        order.setOrderTotal(tbOrder.getPayment());

        // ??????????????????
        List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByOrderId(tbOrder.getId());
        List<CartProduct> cartProductList = new ArrayList<>();
        for (TbOrderItem tbOrderItem : tbOrderItemList) {
            CartProduct cartProduct = new CartProduct();
            cartProduct.setProductId(Long.parseLong(tbOrderItem.getItemId()));
            cartProduct.setProductName(tbOrderItem.getTitle());
            cartProduct.setSalePrice(tbOrderItem.getPrice());
            cartProduct.setProductNum(tbOrderItem.getNum());
            cartProduct.setProductImg(tbOrderItem.getPicPath());
            cartProductList.add(cartProduct);
        }

        // ???????????????????????????????????????????????????????????????????????????
        if (tbOrder.getStatus() == 3 || tbOrder.getStatus() == 4) {
            order.setShippingName(tbOrder.getShippingName());
            order.setShippingCode(tbOrder.getShippingCode());
        }

        order.setGoodsList(cartProductList);
        return BaseResult.success(order);
    }

    @Transactional
    @Override
    public AlipayTradePrecreateResponse payment(OrderPay orderPay) {

        // ??????????????????AlipayTradeService??????????????????
        Configs.init("zfbinfo.properties");
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // (??????) ?????????????????????????????????????????????64????????????????????????????????????????????????????????????
        // ????????????????????????????????????????????????????????????sequence?????????
        String outTradeNo = orderPay.getOrderId().toString();

        // (??????) ??????????????????????????????????????????????????????????????????????????????????????????
        String subject = "YMall";

        // (??????) ?????????????????????????????????????????????1??????
        // ???????????????????????????????????????,????????????????????????,???????????????????????????,???????????????????????????:?????????????????????=??????????????????+????????????????????????
        String totalAmount = orderPay.getOrderTotal().toString();

        // (??????) ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ?????????????????????,?????????????????????????????????,??????????????????,???????????????????????????????????????-??????????????????
        String undiscountableAmount = "";

        // ?????????????????????ID???????????????????????????????????????????????????????????????????????????(?????????sellerId????????????????????????)
        // ??????????????????????????????????????????????????????????????????PID????????????appid?????????PID
        String sellerId = "";

        // ?????????????????????????????????????????????????????????????????????????????????"????????????2??????15.00???"
        String body = "YMall";

        // ??????????????????????????????????????????????????????????????????????????????
        String operatorId = "test_operator_id";

        // (??????) ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        String storeId = "test_store_id";

        // ????????????????????????120??????
        String timeoutExpress = "120m";

        AlipayTradePrecreateRequestBuilder builder =new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject)
                .setTotalAmount(totalAmount)
                .setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount)
                .setSellerId(sellerId)
                .setBody(body)
                .setOperatorId(operatorId)
                .setStoreId(storeId)
                .setTimeoutExpress(timeoutExpress)
                // ???????????????????????????????????????????????????????????????http??????,??????????????????
                .setNotifyUrl(AlipayConfig.notify_url);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        AlipayTradePrecreateResponse response = result.getResponse();

        // ???????????????????????? redis ???
        // redisKey
        String redisKey = ORDER_PAY + ":" + orderPay.getOrderId();
        orderPay.setOrderStatus(Consts.ORDER_STATUS_NOPAY.toString());
        try {
            String orderPayJson = MapperUtil.obj2json(orderPay);
            if (orderPayJson != null) {
                redisCacheManager.set(redisKey, orderPayJson);
                redisCacheManager.expire(redisKey, 60 * 60 * 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Transactional
    @Override
    public int updateOrderStatus(String orderId, Integer orderStatus) {

        // ??????????????????
        String redisKey = ORDER_PAY + ":" + orderId;

        // ????????????
        String orderPayJson = (String) redisCacheManager.get(redisKey);

        if (orderPayJson != null) {
            try {
                OrderPay orderPay = MapperUtil.json2pojo(orderPayJson, OrderPay.class);
                orderPay.setOrderStatus((Consts.ORDER_STATUS_PAY).toString());
                String newPayJson = MapperUtil.obj2json(orderPay);
                if (newPayJson != null) {
                    redisCacheManager.set(redisKey, newPayJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tbOrderMapper.updateOrderStatus(orderId, orderStatus);
    }

    @Override
    public TradeStatus getOrderStatus(String orderId) {

        // ??????????????????AlipayTradeService??????????????????
        Configs.init("zfbinfo.properties");

        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // (??????) ????????????????????????????????????????????????????????????????????????
        String outTradeNo = orderId;
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
                .setOutTradeNo(outTradeNo);
        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        TradeStatus tradeStatus = result.getTradeStatus();
        return tradeStatus;

    }

    @Override
    public BaseResult getOrderList(String userId, int page, int size) {

        // ?????? PageHelper
        PageHelper.startPage(page, size);

        PageOrder pageOrder = new PageOrder();
        List<Order> orders = new ArrayList<>();

        List<TbOrder> tbOrderList = tbOrderMapper.selectByUserId(userId);
        for (TbOrder tbOrder : tbOrderList) {
            Order order = new Order();
            // ??????id
            order.setOrderId(tbOrder.getId());
            // ??????id
            order.setUserId(tbOrder.getUserId().toString());
            // ????????????
            order.setOrderStatus(tbOrder.getStatus());
            // ????????????
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String created = simpleDateFormat.format(tbOrder.getCreated());
            order.setCreateDate(created);
            // ??????????????????
            if (tbOrder.getPaymentTime() != null) {
                String payDate = simpleDateFormat.format(tbOrder.getPaymentTime());
                order.setPayDate(payDate);
            }
            // ??????????????????
            if (tbOrder.getConsignTime() != null) {
                String consignDate = simpleDateFormat.format(tbOrder.getConsignTime());
                order.setConsignDate(consignDate);
            }
            // ??????????????????
            if (tbOrder.getCloseTime() != null) {
                String closeDate = simpleDateFormat.format(tbOrder.getCloseTime());
                order.setCloseDate(closeDate);
            }
            // ??????????????????
            if (tbOrder.getEndTime() != null && tbOrder.getStatus() == 4) {
                String endDate = simpleDateFormat.format(tbOrder.getEndTime());
                order.setFinishDate(endDate);
            }
            // ??????
            TbOrderShipping tbOrderShipping = tbOrderShippingMapper.selectByPrimaryKey(tbOrder.getId());
            TbAddress tbAddress = new TbAddress();
            tbAddress.setUserName(tbOrderShipping.getReceiverName());
            tbAddress.setStreetName(tbOrderShipping.getReceiverAddress());
            tbAddress.setTel(tbOrderShipping.getReceiverPhone());
            order.setTbAddress(tbAddress);
            // ????????????
            order.setOrderTotal(tbOrder.getPayment());
            // ??????????????????
            List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByOrderId(tbOrder.getId());
            List<CartProduct> cartProductList = new ArrayList<>();
            for (TbOrderItem tbOrderItem : tbOrderItemList) {
                CartProduct cartProduct = new CartProduct();
                cartProduct.setProductId(Long.parseLong(tbOrderItem.getItemId()));
                cartProduct.setProductName(tbOrderItem.getTitle());
                cartProduct.setSalePrice(tbOrderItem.getPrice());
                cartProduct.setProductNum(tbOrderItem.getNum());
                cartProduct.setProductImg(tbOrderItem.getPicPath());
                cartProductList.add(cartProduct);
            }
            order.setGoodsList(cartProductList);
            orders.add(order);
        }

        PageInfo<Order> pageInfo = new PageInfo<>(orders);
        pageOrder.setTotal(tbOrderMapper.getMemberOrderCount(userId));
        pageOrder.setData(orders);

        return BaseResult.success(pageOrder);
    }

    @Transactional
    @Override
    public BaseResult confirmReceipt(Order order) {
        // ?????? id
        String userId = order.getUserId();

        // ?????? id
        String orderId = order.getOrderId();

        // ??????????????????
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(orderId)) {
            return BaseResult.fail("????????????");
        }

        // ??????????????????????????????
        TbOrder tbOrder = tbOrderMapper.selectByUserIdAndOrderId(userId, orderId);
        if (tbOrder == null) {
            return BaseResult.fail("????????????");
        }

        // ?????????????????????????????? status ??????
        tbOrderMapper.confirmReceipt(order.getOrderId());
        return BaseResult.success("??????????????????");
    }

    @Override
    public BaseResult deleteService(Order order) {
        // ?????? id
        String userId = order.getUserId();

        // ?????? id
        String orderId = order.getOrderId();

        // ??????????????????
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(orderId)) {
            return BaseResult.fail("????????????");
        }

        // ??????????????????????????????
        TbOrder tbOrder = tbOrderMapper.selectByUserIdAndOrderId(userId, orderId);
        if (tbOrder == null) {
            return BaseResult.fail("????????????");
        }

        // ????????????
        tbOrderMapper.deleteByPrimaryKey(orderId);

        // ??????????????????
        tbOrderItemMapper.deleteByOrderId(orderId);

        // ????????????????????????
        tbOrderShippingMapper.deleteByPrimaryKey(orderId);
        return BaseResult.success("??????????????????");
    }

    @Transactional
    @Override
    public int paySuccess(String orderId) {
        // ???????????????????????????
        return tbOrderMapper.updatePayTimeAndStatus(orderId);
    }

    @Override
    public BaseResult cancelService(Order order) {
        // ???????????????????????????
        tbOrderMapper.cancelOrder(order.getOrderId());

        // ??????????????????
        // ???????????????????????????????????????
        List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByOrderId(order.getOrderId());
        for (TbOrderItem tbOrderItem : tbOrderItemList) {
            String productId = tbOrderItem.getItemId();
            int productNum = tbOrderItem.getNum();
            // ??????????????????
            tbItemMapper.addItemNum(productId, productNum);
            // ?????? Redis ??????
            String redisKey = PRODUCT_ITEM + ":" + productId;
            redisCacheManager.del(redisKey);
        }
        return BaseResult.success("??????????????????");
    }

}
