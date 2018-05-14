package com.xmopay.admincp.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.MessageQueueDao;
import com.xmopay.admincp.dao.PartnerDao;
import com.xmopay.admincp.dao.TradeOrderDao;
import com.xmopay.admincp.dto.AdminUserDto;
import com.xmopay.admincp.dto.MessageQueueDto;
import com.xmopay.admincp.dto.PartnerDto;
import com.xmopay.admincp.dto.TradeOrderDto;
import com.xmopay.admincp.service.TradeOrderService;
import com.xmopay.common.constant.StatusConstants;
import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.common.utils.XmoPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TradeOrderServiceImpl implements TradeOrderService {

    @Autowired
    private TradeOrderDao tradeOrderDao;

    @Autowired
    private PartnerDao partnerDao;

    @Autowired
    private MessageQueueDao messageQueueDao;

    @Override
    public SingleResult<PageInfo> getTradeOrderList(TradeOrderDto tradeOrderDto) {
        SingleResult<PageInfo> result = new SingleResult<>(false, null);
        PageHelper.startPage(tradeOrderDto.getCurrentPage(), tradeOrderDto.getPageSize());
        List<TradeOrderDto> tradeOrderList = tradeOrderDao.getTradeOrderList(tradeOrderDto);
        PageInfo<TradeOrderDto> pageInfo = new PageInfo<>(tradeOrderList);
        if (null != tradeOrderList && tradeOrderList.size() > 0) {
            result.setResult(pageInfo);
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    public SingleResult<Map<String, Object>> getTradeOrderTotal(TradeOrderDto tradeOrderDto) {
        SingleResult<Map<String, Object>> singleResult = new SingleResult<>(false,null);
        try{
            Map<String, Object> totalMap = tradeOrderDao.getTradeOrderTotal(tradeOrderDto);
            if(totalMap != null ){
                singleResult.setSuccess(true);
                singleResult.setResult(totalMap);
                return singleResult;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return singleResult;
    }

    @Override
    public SingleResult<TradeOrderDto> getTradeOrderInfo(TradeOrderDto tradeOrderDto) {
        SingleResult<TradeOrderDto> result = new SingleResult<>(false, null);
        TradeOrderDto tradeOrderInfo = tradeOrderDao.getTradeOrderInfo(tradeOrderDto);
        if (!XmoPayUtils.isNull(tradeOrderInfo)) {
            result.setResult(tradeOrderInfo);
            result.setSuccess(true);
        }
        return result;
    }

    @Transactional
    @Override
    public SingleResult<String> editTrade(TradeOrderDto tradeInfo, Integer modifyOrderStatus, AdminUserDto adminUserDto) {
        SingleResult<String> result = new SingleResult<>(false, "");
        try {

            String musername = adminUserDto.getUserName();
            int muid = adminUserDto.getMuId();
            //查询订单
            String partner_id = tradeInfo.getPartnerId();
            String order_sn   = tradeInfo.getOrderSn();
            Date orderTime    = tradeInfo.getOrderTime();

            PartnerDto partnerDto = new PartnerDto();
            partnerDto.setPartnerId(partner_id);
            PartnerDto partnerInfo = partnerDao.getPartnerInfo(partnerDto);
            if (XmoPayUtils.isNull(partnerInfo)) {
                result.setResult("商户不存在!");
                return result;
            }

            int order_status = tradeInfo.getOrderStatus();
            //'订单状态: WAIT_TO_BANK-未提交到银行  WAIT_BANK_NOTIFY-已提交待处理  TRADE_SUCCESS-交易成功  TRADE_FAILURE-交易失败  TRADE_EXCEPTION - 异常订单 TRADE_CLOSE - 订单自动关闭', -->
            //（允许：已提交待处理-->成功[需要超级权限]；已提交待处理-->失败[需要超级权限]；未提交到银行-->订单自动关闭）

            TradeOrderDto finalTradeOrder = new TradeOrderDto();
            String logMsg = "[" + muid + "]操作人:" + musername + ", 更新订单号:" + order_sn + "的状态为" + order_status + ", 修改为状态" + modifyOrderStatus;

            //1. TRADE_WAIT_BANK_HANDLE-已提交待处理 ----> TRADE_SUCCESS-交易成功
            if (order_status == StatusConstants.TRADE_WAIT_BANK_HANDLE && modifyOrderStatus == StatusConstants.TRADE_SUCCESS) {

                //修改订单时间
                finalTradeOrder.setOrderSn(order_sn);
                finalTradeOrder.setPartnerId(partner_id);
                finalTradeOrder.setTradeTime(new Date());
                int n = tradeOrderDao.updateTradeOrder(finalTradeOrder);
                if (n > 0) {

                    // 增加消息队列 - 交易加款
                    Map messageBodyMap = new HashMap();
                    messageBodyMap.put("order_sn", order_sn);
                    messageBodyMap.put("partner_id", partner_id);

                    long seconds = (System.currentTimeMillis() - orderTime.getTime())/ 1000;

                    if( seconds/3600.0 > 6){ //订单时间超过6小时，添加人工处理标识
                        messageBodyMap.put("manual_handling", true);
                    }

                    MessageQueueDto messageQueue = new MessageQueueDto();
                    messageQueue.setOrderSn(order_sn);
                    messageQueue.setPartnerId(partner_id);
                    messageQueue.setMessageTopic(XmoPayConstants.TOPIC_TRADE_PAY_WAIT_SETTLE);//交易加款
                    messageQueue.setMessageBody(JSON.toJSONString(messageBodyMap));
                    messageQueue.setDateline(new Date());
                    int m = messageQueueDao.insertMessageQueue(messageQueue);
                    if (m > 0) {
                        log.info(logMsg + " [产生交易加款消息，放入通知系统]");
                        result.setResult("状态操作成功，已产生消息队列并上送，请等待系统处理!");
                        result.setSuccess(true);
                        return result;
                    }
                    log.info(logMsg + " [产生交易加款消息失败]");
                    result.setResult("订单操作失败!");
                    return result;
                }
                log.info(logMsg + " [订单时间修改失败]");
                result.setResult("订单操作失败!");
                return result;

            }
            //2. 待处理 --> 失败 : 只修改状态
            if (order_status == StatusConstants.TRADE_WAIT_BANK_HANDLE && modifyOrderStatus == StatusConstants.TRADE_FAILURE) {
                finalTradeOrder.setOrderSn(order_sn);
                finalTradeOrder.setPartnerId(partner_id);
//                finalTradeOrder.setOrderStatus(StatusConstants.TRADE_FAILURE);//2
                finalTradeOrder.setFinishTime(new Date());
                int n = tradeOrderDao.updateTradeOrder(finalTradeOrder);
                if (n > 0) {
                    log.info(logMsg + " [操作成功]");
                    result.setResult("订单操作成功!");
                    result.setSuccess(true);
                    return result;
                }
                log.info(logMsg + " [操作失败]");
                result.setResult("订单操作失败!");
                return result;
            }

            //3. 风控订单 --> 成功 ： 修改订单状态为待处理(orderStatus=2), 产生交易加款消息
            if (order_status == StatusConstants.TRADE_EXCEPTION && modifyOrderStatus == StatusConstants.TRADE_SUCCESS) {
                finalTradeOrder.setOrderSn(order_sn);
                finalTradeOrder.setPartnerId(partner_id);
                finalTradeOrder.setOrderStatus(StatusConstants.TRADE_WAIT_BANK_HANDLE);//2
                finalTradeOrder.setOrderTime(new Date());
                finalTradeOrder.setFinishTime(new Date());
                int n = tradeOrderDao.updateTradeOrder(finalTradeOrder);
                if (n > 0) {
                    // 增加消息队列 - 交易加款
                    Map messageBodyMap = new HashMap();
                    messageBodyMap.put("order_sn", order_sn);
                    messageBodyMap.put("partner_id", partner_id);

                    MessageQueueDto messageQueue = new MessageQueueDto();
                    messageQueue.setOrderSn(order_sn);
                    messageQueue.setPartnerId(partner_id);
                    messageQueue.setMessageTopic(XmoPayConstants.TOPIC_TRADE_PAY_WAIT_SETTLE);//交易加款
                    messageQueue.setMessageBody(JSON.toJSONString(messageBodyMap));
                    messageQueue.setDateline(new Date());

                    int m = messageQueueDao.insertMessageQueue(messageQueue);
                    if (m > 0) {
                        log.info(logMsg + " [已产生交易加款消息，放入通知系统]");
                        result.setResult("状态操作成功，已产生消息队列并上送，请等待系统处理!");
                        result.setSuccess(true);
                        return result;
                    }
                    log.info(logMsg + " [状态操作成功，交易加款消息产生失败]");
                    result.setResult("状态操作成功，交易加款消息产生失败!");
                    result.setSuccess(true);
                    return result;
                }
                log.info(logMsg + " [订单状态修改失败]");
                result.setResult("订单状态修改失败!");
                return result;
            }
            //4. 未提交到银行 --> 交易自动关闭 ： 修改订单状态
            if (order_status == StatusConstants.TRADE_WAIT_TO_BANK && modifyOrderStatus == StatusConstants.TRADE_CLOSE) {
                finalTradeOrder.setOrderSn(order_sn);
                finalTradeOrder.setPartnerId(partner_id);
                finalTradeOrder.setOrderStatus(StatusConstants.TRADE_CLOSE);
                finalTradeOrder.setFinishTime(new Date());
                int n = tradeOrderDao.updateTradeOrder(finalTradeOrder);
                if (n > 0) {
                    log.info(logMsg + " [状态操作成功]");
                    result.setResult("状态操作成功!");
                    result.setSuccess(true);
                    return result;
                }
                log.info(logMsg + " [订单状态修改失败]");
                result.setResult("订单状态修改失败!");
                return result;
            }
            log.info(logMsg + " [订单状态出错，请重新操作]");
            result.setResult("订单状态出错，请重新操作!");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult("系统异常，请联系客服!");
            throw new RuntimeException("Transactional rollback runtimeException!");
        }
    }
}
