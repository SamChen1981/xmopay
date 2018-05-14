package com.xmopay.openapi.core;

import com.alibaba.fastjson.JSON;
import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.common.enums.ApiResultEnum;
import com.xmopay.common.model.PayRequestModel;
import com.xmopay.common.utils.StringUtils;
import com.xmopay.common.utils.XmoPayUtils;
import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dto.PartnerProductDto;
import com.xmopay.openapi.dto.TradeOrderDto;
import com.xmopay.openapi.service.TradeOrderService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Component
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private static final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @Resource(name = "tradeOrderService")
    private TradeOrderService tradeOrderService;

    @Autowired
    private ChannelRemote channelRemote;

    /**
     * 在线充值/支付
     *
     * @param params
     * @return
     */
    public SingleResult<Map> doOnlinePay(Map params) {
        SingleResult<Map> result = new SingleResult<>();
        result.setSuccess(false);
        result.setErrorCode(ApiResultEnum.GATEWAY_CHANNEL_RESPONSE_FAILURE.getRespCode());
        result.setErrorMessage(ApiResultEnum.GATEWAY_CHANNEL_RESPONSE_FAILURE.getRespMsg());

        try {
            String tradeIp = params.get("trade_ip").toString();
            String payType = params.get("pay_type").toString();
            String bankCode = params.get("bank_code").toString();
            String outTradeNo = params.get("out_trade_no").toString();
            String partnerProductJson = params.get("partner_product").toString();

            // 加载商户产品DTO
            PartnerProductDto partnerProduct = JSON.parseObject(partnerProductJson, PartnerProductDto.class);

            // 查询订单信息 - 需要做缓存处理
            TradeOrderDto tradeOrderDto = new TradeOrderDto();
            tradeOrderDto.setOutTradeNo(outTradeNo);
            tradeOrderDto.setOrderAmount(new BigDecimal(params.getOrDefault("amount", "").toString()));

            SingleResult<TradeOrderDto> tradeOrderResult = tradeOrderService.getTradeOrderInfo(tradeOrderDto);
            if (tradeOrderResult.isSuccess()) {
                result.setSuccess(false);
                result.setErrorCode(ApiResultEnum.ILLEGAL_OUT_TRADE_NO_IS_EXISTS.getRespCode());
                result.setErrorMessage(ApiResultEnum.ILLEGAL_OUT_TRADE_NO_IS_EXISTS.getRespMsg());
                logger.info("[网关支付中心] 来自客户端IP={}, outTradeNo={}, 商户订单号已存在，拒绝重复提交", tradeIp, outTradeNo);
                return result;
            }

            // 保存订单信息
            String orderSn = StringUtils.getOrderSNByRule("default");
            tradeOrderDto.setOrderSn(orderSn);
            tradeOrderDto.setPayType(payType);
            tradeOrderDto.setBankCode(bankCode);
            tradeOrderDto.setOrderTime(new Date());
            tradeOrderDto.setTradeTime(new Date());
            tradeOrderDto.setFinishTime(new Date());
            tradeOrderDto.setChannelCode(partnerProduct.getChannelCode());
            tradeOrderDto.setVersion(params.get("version").toString());
            tradeOrderDto.setTradeIp(params.get("trade_ip").toString());
            tradeOrderDto.setSignType(params.get("sign_type").toString());
            tradeOrderDto.setTradeHash(params.get("trade_hash").toString());
            tradeOrderDto.setPartnerId(params.get("partner_id").toString());
            tradeOrderDto.setPartnerName(params.get("partner_name").toString());
            tradeOrderDto.setOrderTitle(params.get("summary") == null ? "" : params.get("summary").toString());
            tradeOrderDto.setNotifyUrl(params.get("notify_url") == null ? "" : params.get("notify_url").toString());
            tradeOrderDto.setReturnUrl(params.get("return_url") == null ? "" : params.get("return_url").toString());
            tradeOrderDto.setExtendParam(params.get("memo") == null ? "" : params.get("memo").toString());
            tradeOrderDto.setOutTradeTime(DateTime.parse(params.get("out_trade_time").toString(), format).toDate());
            tradeOrderDto.setTranType(Integer.valueOf(XmoPayConstants.TYPE_ONLINE_PAY));
            tradeOrderDto.setOrderSign(XmoPayUtils.createOrderSign(params.get("partner_id").toString(), orderSn, outTradeNo, params.getOrDefault("amount", "").toString()));

            // 订单入库
            SingleResult<Integer> insertTradeOrderResult = tradeOrderService.insertTradeOrder(tradeOrderDto);
            if (!insertTradeOrderResult.isSuccess()) {
                result.setSuccess(false);
                result.setErrorCode(ApiResultEnum.TRADE_ORDER_RESPONSE_ERROR.getRespCode());
                result.setErrorMessage(ApiResultEnum.TRADE_ORDER_RESPONSE_ERROR.getRespMsg());
                logger.info("[网关支付中心] 来自客户端IP={}, orderSn={}, 订单入库失败", tradeIp, orderSn);
                return result;
            }

            // 调用渠道接口
            SingleResult<Map> channelPayResult = doChannelPay(tradeOrderDto);
            if (channelPayResult == null) {
                result.setSuccess(false);
                result.setErrorCode(ApiResultEnum.GATEWAY_CHANNEL_RESPONSE_FAILURE.getRespCode());
                result.setErrorMessage(ApiResultEnum.GATEWAY_CHANNEL_RESPONSE_FAILURE.getRespMsg());
                logger.info("[网关支付中心] 来自客户端IP={}, orderSn={}, 调用渠道接口异常", tradeIp, orderSn);
                return result;
            }

            if (channelPayResult.isSuccess()) {
                result.setSuccess(true);
                result.setResult(channelPayResult.getResult());
                logger.info("[网关支付中心] 来自客户端IP={}, orderSn={}, 调用渠道接口成功", tradeIp, orderSn);
                return result;
            }

            result.setSuccess(false);
            result.setErrorCode(channelPayResult.getErrorCode());
            result.setErrorMessage(channelPayResult.getErrorMessage());
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorCode(ApiResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode());
            result.setErrorMessage(ApiResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg());
            return result;
        }
    }

    /**
     * 调用各类支付请求接口
     *
     * @param tradeOrderDto
     * @return
     */
    public SingleResult<Map> doChannelPay(TradeOrderDto tradeOrderDto) {
        SingleResult<Map> result = new SingleResult<>(false, ApiResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode(), ApiResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg(), null);
        try {
            StringBuilder params = new StringBuilder(8);
            params.append("{");
            params.append("\"channelCode\":\"" + tradeOrderDto.getChannelCode() + "\",");
            params.append("\"orderSn\":\"" + tradeOrderDto.getOrderSn() + "\",");
            params.append("\"partnerId\":\"" + tradeOrderDto.getPartnerId() + "\"");
            params.append("}");
            String ret = channelRemote.doPayment(JSON.parseObject(params.toString(), PayRequestModel.class));
            if (ret == null) {
                return result;
            }
            Map retMap = JSON.parseObject(ret, Map.class);
            if (!"RESPONSE_SUCCESS".equals(retMap.getOrDefault("return_code", ""))) {
                logger.info("[网关支付中心] 调用渠道失败, return_code={}, return_message={}", retMap.get("return_code"), retMap.get("return_message"));
                return result;
            }

            result.setErrorCode(ApiResultEnum.GATEWAY_CHANNEL_RESPONSE_SUCCESS.getRespCode());
            result.setErrorMessage(ApiResultEnum.GATEWAY_CHANNEL_RESPONSE_SUCCESS.getRespMsg());
            result.setResult(JSON.parseObject(retMap.getOrDefault("body", "{}").toString(), Map.class));
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setErrorCode(ApiResultEnum.GATEWAY_CHANNEL_RESPONSE_EXCEPTION.getRespCode());
            result.setErrorMessage(ApiResultEnum.GATEWAY_CHANNEL_RESPONSE_EXCEPTION.getRespMsg());
            return result;
        }
    }

}
