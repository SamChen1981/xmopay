package com.xmopay.funds.service.impl;

import com.alibaba.fastjson.JSON;
import com.xmopay.common.constant.StatusConstants;
import com.xmopay.common.utils.DateTimeUtils;
import com.xmopay.common.utils.StringUtils;
import com.xmopay.common.utils.XmoPayUtils;
import com.xmopay.funds.dao.BillingsDao;
import com.xmopay.funds.dao.MessageQueueDao;
import com.xmopay.funds.dao.PartnerAccountDao;
import com.xmopay.funds.dao.PartnerProductDao;
import com.xmopay.funds.dao.TradeOrderDao;
import com.xmopay.funds.dto.BillingsDto;
import com.xmopay.funds.dto.PartnerAccountDto;
import com.xmopay.funds.dto.PartnerProductDto;
import com.xmopay.funds.dto.TradeOrderDto;
import com.xmopay.funds.dto.ext.TradeOrderDtoExt;
import com.xmopay.funds.entity.MessageEntity;
import com.xmopay.funds.exception.FundsException;
import com.xmopay.funds.service.IRechargeServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * com.xmopay.funds.service.impl
 *
 * @author echo_coco.
 * @date 10:41 AM, 2018/4/26
 */
@Service("tradeRechargeServiceImpl")
public class RechargeServiceImpl implements IRechargeServiceI {
    private static final Logger logger = LoggerFactory.getLogger(RechargeServiceImpl.class);

    /**
     * 订单异常码
     */
    private static final String CODE_TRADE_EXCEPTION = "10000";

    @Autowired
    private TradeOrderDao tradeOrderDao;

    @Autowired
    private PartnerAccountDao partnerAccountDao;

    @Autowired
    private BillingsDao billingsDao;

    @Autowired
    private MessageQueueDao messageQueueDao;

    @Autowired
    private PartnerProductDao partnerProductDao;

    @Autowired
    private PlatformTransactionManager txManager;

    @Override
    public void execute(MessageEntity entity) {
        doRecharge(entity);
    }

    private void doRecharge(MessageEntity entity) {
        long startTime = System.currentTimeMillis();
        logger.info("--------------------[交易异步加款服务开始执行]--------------------");

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // 设置事务隔离级别，开启新事务
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus transactionStatus = txManager.getTransaction(def);
        int mqId = 0;
        String orderSn = "";
        String partnerId = "";
        try {
            Map message = JSON.parseObject(entity.getMsgBody(), Map.class);

            mqId = entity.getMqId();
            orderSn = message.get("order_sn").toString();
            partnerId = message.get("partner_id").toString();

            // #1 订单信息
            TradeOrderDtoExt pTradeOrderDto = new TradeOrderDtoExt();
            pTradeOrderDto.setOrderSn(orderSn);
            pTradeOrderDto.setPartnerId(partnerId);
            pTradeOrderDto.setOrderStatus(StatusConstants.TRADE_WAIT_BANK_HANDLE);
            TradeOrderDto tradeOrderDto = tradeOrderDao.getTradeOrderByOrderSnToLock(pTradeOrderDto);
            if (tradeOrderDto == null || XmoPayUtils.isEmpty(tradeOrderDto.getPartnerId())) {
                throw new FundsException(CODE_TRADE_EXCEPTION, "[交易异步加款服务] 订单号: " + orderSn + ", 未查询到该笔交易订单信息!");
            }

            long orderTimestamp = DateTimeUtils.getStringToDateTime(tradeOrderDto.getOrderTime(), "yyyy-MM-dd HH:mm:ss").getTime();
            if ((System.currentTimeMillis() - orderTimestamp) > 86400000L) {
                throw new FundsException(CODE_TRADE_EXCEPTION, "[交易异步加款服务] 订单号: " + orderSn + ", 该笔交易订单加款超时, 请取消订单或联系客服!");
            }

            logger.info("[交易异步加款服务] 订单数据, 订单号={}, 订单金额={}, 交易类型={}, 商户名称={}, 支付方式={}, 银行卡号={}, 通知地址={}, 平台订单时间={}, 扩展字段={}, 订单状态={}, 交易IP={}, COOKIE HASH码={}",
                    orderSn,
                    tradeOrderDto.getOrderAmount(),
                    tradeOrderDto.getTranType(),
                    tradeOrderDto.getPartnerName(),
                    tradeOrderDto.getPayType(),
                    tradeOrderDto.getBankCode(),
                    tradeOrderDto.getNotifyUrl(),
                    tradeOrderDto.getOrderTime(),
                    tradeOrderDto.getExtendParam(),
                    tradeOrderDto.getOrderStatus(),
                    tradeOrderDto.getTradeIp(),
                    tradeOrderDto.getTradeHash());

            // #2 获取商户产品信息
            PartnerProductDto partnerProductDto = new PartnerProductDto();
            partnerProductDto.setBankCode(tradeOrderDto.getBankCode());
            partnerProductDto.setProductType(tradeOrderDto.getPayType());
            partnerProductDto.setChannelCode(tradeOrderDto.getChannelCode());
            partnerProductDto = partnerProductDao.getPartnerProduct(partnerProductDto);
            BigDecimal rate = partnerProductDto.getRate();
            if (rate.compareTo(new BigDecimal("0")) == 0 || rate.compareTo(new BigDecimal("0")) < 0) {
                throw new FundsException(CODE_TRADE_EXCEPTION, "[交易异步加款服务] 订单号: " + orderSn + ", 费率设置异常["+rate.toString()+"], 交易加款失败!");
            }

            BigDecimal fee = new BigDecimal(tradeOrderDto.getOrderAmount()).multiply(rate).divide(new BigDecimal("100")).setScale(2, RoundingMode.UP);
            if (fee.compareTo(new BigDecimal(tradeOrderDto.getOrderAmount())) > 0) {
                throw new FundsException(CODE_TRADE_EXCEPTION, "[交易异步加款服务] 订单号: " + orderSn + ", 交易费率设置有误["+rate.toString()+"], 交易加款失败!");
            }

            logger.info("[交易异步加款服务] 商户产品信息, 产品类型={}, 银行代码={}, 费率={}",
                    partnerProductDto.getProductType(), partnerProductDto.getBankCode(), partnerProductDto.getRate());

            // #3 商户账户信息
            PartnerAccountDto partnerAccountDto = partnerAccountDao.getPartnerAccountByPidLock(partnerId);
            logger.info("[交易异步加款服务] 商户账户信息, 商户号={}, 当前余额={}", partnerId, partnerAccountDto.getBalance());

            BigDecimal payment = new BigDecimal(tradeOrderDto.getOrderAmount()).subtract(fee).setScale(2, RoundingMode.DOWN);
            BigDecimal currentBalance = new BigDecimal(partnerAccountDto.getBalance()).add(payment).setScale(2, RoundingMode.DOWN);

            // #4 增加流水
            String billingSN = StringUtils.buildBillsn();
            BillingsDto billingsDto = new BillingsDto();
            billingsDto.setOrderSn(orderSn);
            billingsDto.setBillingSn(billingSN);
            billingsDto.setBillType(tradeOrderDto.getTranType());
            billingsDto.setPartnerId(partnerId);
            billingsDto.setPartnerName(tradeOrderDto.getPartnerName());
            billingsDto.setPayType(tradeOrderDto.getPayType());
            billingsDto.setBankCode(tradeOrderDto.getBankCode());
            billingsDto.setTradeAmount(tradeOrderDto.getOrderAmount());
            billingsDto.setTradeFee(fee.toString());
            billingsDto.setPayment(payment.toPlainString());
            billingsDto.setAccountAmount(currentBalance.toPlainString());
            billingsDto.setRemark("[交易异步加款服务] 商户号: " + partnerId
                    + ", 订单号: " + orderSn
                    + ", 账户变动前余额: " + partnerAccountDto.getBalance()
                    + ", 交易金额: " + tradeOrderDto.getOrderAmount()
                    + ", 手续费: " + fee
                    + ", 账户变动后余额: " + currentBalance);
            billingsDto.setTradeTime(DateTimeUtils.dateFullString());
            billingsDto.setPayStatus(1);
            if (billingsDao.insertBillings(billingsDto) < 0) {
                throw new FundsException("[交易异步加款服务] 订单号: " + orderSn + ", 添加流水失败!");
            }

            logger.info("[交易异步加款服务] 增加流水, 流水号={}, 订单号={}", billingSN, orderSn);

            // #5 更新账户余额
            PartnerAccountDto pPartnerAccountDto = new PartnerAccountDto();
            pPartnerAccountDto.setBalance(payment.toString());
            pPartnerAccountDto.setPartnerId(partnerId);
            pPartnerAccountDto.setLastTrade(DateTimeUtils.dateFullString());
            if (partnerAccountDao.updatePartnerAccount(pPartnerAccountDto) < 0) {
                throw new FundsException("[交易异步加款服务] 商户号: " + partnerId + ", 更新账户余额失败!");
            }

            logger.info("[交易异步加款服务] 更新账户余额, 商户号={}, 加款金额={}", partnerId, payment);

            // #6 更新订单
            pTradeOrderDto = new TradeOrderDtoExt();
            pTradeOrderDto.setOrderSn(orderSn);
            pTradeOrderDto.setBillingSn(billingSN);
            pTradeOrderDto.setFinishTime(DateTimeUtils.dateFullString());
            pTradeOrderDto.setOrderStatus(StatusConstants.TRADE_SUCCESS);
            if (tradeOrderDao.updateTradeOrderStatus(pTradeOrderDto) < 0) {
                throw new FundsException("[交易异步加款服务] 订单号: " + orderSn + ", 更新订单状态失败!");
            }

            logger.info("[交易异步加款服务] 更新订单, 订单号={}, 流水号={}", orderSn, billingSN);

            // #7 删除消息队列
            messageQueueDao.deleteMessageQueueById(mqId);

            txManager.commit(transactionStatus);
            logger.info("[交易异步加款服务] 商户号={}, 订单号={}, 账户变动前余额={}, 交易金额={}, 手续费={}, 账户变动后余额={}, 该笔交易加款成功!",
                    partnerId, orderSn, partnerAccountDto.getBalance(), tradeOrderDto.getOrderAmount(), fee, currentBalance);
        } catch (Exception e) {
            txManager.rollback(transactionStatus);
            if (e instanceof FundsException) {
                FundsException apiException = (FundsException) e;
                if (CODE_TRADE_EXCEPTION.equals(apiException.getErrCode())) {
                    TradeOrderDtoExt tradeOrderDtoExt = new TradeOrderDtoExt();
                    tradeOrderDtoExt.setOrderStatus(StatusConstants.TRADE_EXCEPTION);
                    tradeOrderDtoExt.setOrderSn(orderSn);
                    tradeOrderDtoExt.setFinishTime("");
                    tradeOrderDao.updateTradeOrderStatus(tradeOrderDtoExt);
                }
            }
            logger.error("[交易异步加款服务]执行异常 订单号={}, 异常信息={}", orderSn, e.getMessage(), e);
        }
        logger.info("[交易异步加款服务] 执行结束, 耗时[{}ms]", (System.currentTimeMillis() - startTime));
    }

}
