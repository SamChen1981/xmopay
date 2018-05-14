package com.xmopay.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xmopay.channel.payment.IPaymentCallbackService;
import com.xmopay.channel.payment.IPaymentService;
import com.xmopay.common.Constants;
import com.xmopay.common.constant.StatusConstants;
import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.common.model.CallbackRequestModel;
import com.xmopay.common.model.PayRequestModel;
import com.xmopay.common.model.QueryOrderRequestModel;
import com.xmopay.dto.GatewayChannelDto;
import com.xmopay.dto.MessageQueueDto;
import com.xmopay.dto.TradeOrderDto;
import com.xmopay.dto.ext.TradeOrderDtoExt;
import com.xmopay.enums.ChannelPayResultEnum;
import com.xmopay.service.IGatewayChannelService;
import com.xmopay.service.IMessageQueueService;
import com.xmopay.service.IRedisService;
import com.xmopay.service.ITradeOrderService;
import com.xmopay.utils.wxpay.WXPay;
import com.xmopay.utils.wxpay.WXPayConfigImpl;
import com.xmopay.utils.wxpay.WXPayConstants;
import com.xmopay.utils.wxpay.WXPayUtil;
import com.xmopay.vo.ChannelResponseResult;
import com.xmopay.vo.VoResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * com.xmopay.channel
 * 微信
 *
 * @author echo_coco.
 * @date 6:46 PM, 2018/4/27
 */
@Service("wxPayChannelServiceImpl")
public class WxPayChannelServiceImpl extends AbstractBaseChannelService implements IPaymentService, IPaymentCallbackService {
    private static final Logger logger = LoggerFactory.getLogger(WxPayChannelServiceImpl.class);

    @Autowired
    private IGatewayChannelService iGatewayChannelService;

    @Autowired
    private ITradeOrderService iTradeOrderService;

    @Autowired
    private IMessageQueueService iMessageQueueService;

    @Autowired
    private IRedisService iRedisService;

    @Override
    public ChannelResponseResult<Map> doPayment(PayRequestModel payRequestModel) {
        logger.info("[WeChat Pay Service] ******************开始执行******************");
        ChannelResponseResult<Map> result = new ChannelResponseResult<>(ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg(), null);

        try {
            // #0 获取订单信息
            VoResult<TradeOrderDto> tradeOrderDtoVoResult = iTradeOrderService.getOrderInfo(payRequestModel.getOrderSn());
            if (!tradeOrderDtoVoResult.isSuccess()) {
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespMsg());
                return result;
            }

            TradeOrderDto tradeOrderDto = tradeOrderDtoVoResult.getData();
            if (tradeOrderDto.getOrderStatus() != StatusConstants.TRADE_WAIT_TO_BANK) {
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_STATUS_ERROR.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_STATUS_ERROR.getRespMsg());
                return result;
            }

            // #1 查询网关信息
            GatewayChannelDto gatewayChannelDto = new GatewayChannelDto.Builder()
                    .channelCode(tradeOrderDto.getChannelCode())
                    .build();
            VoResult<GatewayChannelDto> gatewayChannelDtoVoResult = iGatewayChannelService.getGatewayChannelInfo(gatewayChannelDto);
            if (!gatewayChannelDtoVoResult.isSuccess()) {
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_NOT_FOUND.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_NOT_FOUND.getRespMsg());
                return result;
            }

            gatewayChannelDto = gatewayChannelDtoVoResult.getData();

            // #2 更新订单状态
            TradeOrderDtoExt tradeOrderDtoExt = new TradeOrderDtoExt.Builder()
                    .orderSn(payRequestModel.getOrderSn())
                    .orderStatus(StatusConstants.TRADE_WAIT_BANK_HANDLE)
                    .partnerId(payRequestModel.getPartnerId())
                    .oriOrderStatus(StatusConstants.TRADE_WAIT_TO_BANK).build();
            VoResult<Integer> updateTradeOrderVoResult = iTradeOrderService.updateTradeOrder(tradeOrderDtoExt);
            if (!updateTradeOrderVoResult.isSuccess()) {
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespMsg());
                return result;
            }

            final String appId = gatewayChannelDto.getChannelKey();
            final String key = gatewayChannelDto.getChannelSecret();
            JSONObject channelParamsJson = JSON.parseObject(gatewayChannelDto.getChannelParams());
            final String mchId = channelParamsJson.containsKey("mch_id") ? channelParamsJson.getString("mch_id") : "";
            final String asyncDomain = channelParamsJson.containsKey("async_domain") ? channelParamsJson.getString("async_domain") : "";

            // #3 上送
            WXPayConfigImpl config = WXPayConfigImpl.getInstance();
            config.init(appId, mchId, key, false);
            WXPay wxPay = new WXPay(config);

            HashMap<String, String> data = new HashMap<>(8);
            data.put("body", "xmopay-wx支付测试");
            data.put("out_trade_no", payRequestModel.getOrderSn());
            data.put("device_info", "");
            data.put("fee_type", "CNY");
            data.put("total_fee", tradeOrderDto.getOrderAmount().multiply(new BigDecimal("100")).intValue() + "");
            data.put("spbill_create_ip", tradeOrderDto.getTradeIp());
            data.put("notify_url", asyncDomain + getAsynApiName());
            data.put("trade_type", "NATIVE");
            data.put("product_id", "P" + payRequestModel.getOrderSn());
            Map<String, String> r = wxPay.unifiedOrder(wxPay.fillRequestData(data));
            logger.info("[WeChat Pay Service] 响应结果={}", r);
            String resultCode = r.containsKey("result_code") ? r.get("result_code") : "";
            String returnCode = r.containsKey("return_code") ? r.get("return_code") : "";
            if ("SUCCESS".equals(resultCode) && "SUCCESS".equals(returnCode)) {
                Map<String, String> body = new HashMap<>(1);
                body.put("qr_url", r.get("code_url"));
                body.put("order_sn", payRequestModel.getOrderSn());

                // #4 返回结果信息
                result.setCode(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespCode());
                result.setMsg(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespMsg());
                result.setBody(body);
            } else {
                logger.info("[WeChat Pay Service] 上送失败. msg={}", r.get("return_msg"));
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespMsg());
                result.setBody(null);
            }
            logger.info("[WeChat Pay Service] ******************执行结束******************");
        } catch (Exception e) {
            logger.error("[WeChat Pay Service] 程序异常.", e);
            result.setCode(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode());
            result.setMsg(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg());
            result.setBody(null);
        }
        return result;
    }

    @Override
    public ChannelResponseResult<String> doPaymentAsyncCallback(CallbackRequestModel callbackRequestModel) {
        logger.info("[微信支付回调服务] ******************执行开始******************");
        ChannelResponseResult<String> result = new ChannelResponseResult<>(ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg(), null);
        String outTradeNo = "";
        try {
            logger.info("[微信支付回调服务] 通知参数={}", callbackRequestModel.getBody());
            Map<String, String> callbackParam = JSON.parseObject(callbackRequestModel.getBody(), Map.class);

            outTradeNo = callbackParam.getOrDefault("out_trade_no", "0");

            // #0 监测订单缓存 @TODO 缓存一定要开启
            /*final String cacheTradeNo = iRedisService.getValue(Constants.TRADE_CACHE_PREFIX + outTradeNo);
            if ("success".equals(iRedisService.getValue(Constants.TRADE_CACHE_PREFIX + callbackParam.getOrDefault("out_trade_no", "0")))) {
                logger.info("[微信支付回调服务] ip={}, trade_no={}, cache_trade_no={}, 本地缓存已存在改订单.", callbackRequestModel.getRequestIp(), outTradeNo, cacheTradeNo);
                result.setCode(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespCode());
                result.setMsg(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespMsg());
                result.setBody(Constants.WXReturnCode.RETURN_CODE_SUCCESS);
                return result;
            }*/

            String returnCode = callbackParam.getOrDefault("return_code", "");
            if (!"SUCCESS".equals(returnCode)) {
                logger.info("[微信支付回调服务] ip={}, trade_no={}, return_code={}, 微信返回状态码错误.", callbackRequestModel.getRequestIp(), outTradeNo, returnCode);
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespMsg());
                result.setBody(Constants.WXReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            // #1 获取订单信息
            final String orderSn = callbackParam.getOrDefault("out_trade_no", "");
            VoResult<TradeOrderDto> tradeOrderDtoVoResult = iTradeOrderService.getOrderInfo(orderSn);
            if (!tradeOrderDtoVoResult.isSuccess()) {
                logger.info("[微信支付回调服务] ip={}, trade_no={}, 订单号错误, 获取本地订单失败.", callbackRequestModel.getRequestIp(), outTradeNo);
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespMsg());
                result.setBody(Constants.WXReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            final TradeOrderDto tradeOrderDto = tradeOrderDtoVoResult.getData();

            if (tradeOrderDto.getOrderAmount().multiply(new BigDecimal("100")).compareTo(new BigDecimal(callbackParam.getOrDefault("total_fee", "0"))) != 0) {
                logger.info("[微信支付回调服务] ip={}, trade_no={}, order_amount={}, total_fee={}, 订单金额错误.", callbackRequestModel.getRequestIp(), outTradeNo, tradeOrderDto.getOrderAmount(), callbackParam.getOrDefault("total_fee", "0"));
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_AMOUNT_ERROR.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_AMOUNT_ERROR.getRespMsg());
                result.setBody(Constants.WXReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            // #2 查询网关信息
            GatewayChannelDto gatewayChannelDto = new GatewayChannelDto.Builder()
                    .channelCode(tradeOrderDto.getChannelCode())
                    .build();
            VoResult<GatewayChannelDto> gatewayChannelDtoVoResult = iGatewayChannelService.getGatewayChannelInfo(gatewayChannelDto);
            if (!gatewayChannelDtoVoResult.isSuccess()) {
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_NOT_FOUND.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_NOT_FOUND.getRespMsg());
                result.setBody(Constants.WXReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            // #3 校验签名
            boolean signatureValid = WXPayUtil.isSignatureValid(callbackParam, gatewayChannelDtoVoResult.getData().getChannelSecret(), WXPayConstants.SignType.HMACSHA256);
            if (!signatureValid) {
                logger.info("[微信支付回调服务] ip={}, trade_no={}, 校验签名错误.", callbackRequestModel.getRequestIp(), outTradeNo);
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_SIGNATURE_VALID_ERROR.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_SIGNATURE_VALID_ERROR.getRespMsg());
                result.setBody(Constants.WXReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            if (StatusConstants.TRADE_SUCCESS == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_CLOSE == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_FAILURE == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_EXCEPTION == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_REFUND == tradeOrderDto.getOrderStatus()) {
                logger.info("[微信支付回调服务] ip={}, trade_no={}, trade_status={}, 本订单状态有误.", callbackRequestModel.getRequestIp(), outTradeNo, tradeOrderDto.getOrderStatus());
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespMsg());
                result.setBody(Constants.WXReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            if ("SUCCESS".equals(callbackParam.getOrDefault("result_code", ""))
                    && (StatusConstants.TRADE_WAIT_BANK_HANDLE == tradeOrderDto.getOrderStatus() || StatusConstants.TRADE_WAIT_TO_BANK == tradeOrderDto.getOrderStatus())) {
                MessageQueueDto messageQueueDto = new MessageQueueDto();
                messageQueueDto.setConsumerStatus(0);
                messageQueueDto.setThreadStatus(0);
                messageQueueDto.setDateline(new Date(System.currentTimeMillis()));
                messageQueueDto.setMessageBody("{\"order_sn\":\""+tradeOrderDto.getOrderSn()+"\", \"partner_id\":\""+tradeOrderDto.getPartnerId()+"\"}");
                messageQueueDto.setMessageHost("");
                messageQueueDto.setMessageTopic(XmoPayConstants.TOPIC_TRADE_PAY_WAIT_SETTLE);
                messageQueueDto.setOrderSn(tradeOrderDto.getOrderSn());
                messageQueueDto.setPartnerId(tradeOrderDto.getPartnerId());
                VoResult<Integer> insertMessageQueueVoResult = iMessageQueueService.insertMessageQueue(messageQueueDto);
                if (insertMessageQueueVoResult.isSuccess() && insertMessageQueueVoResult.getData() > 0) {
                    // 设置订单缓存 @TODO 一定要设置缓存
//                    iRedisService.setValue(Constants.TRADE_CACHE_PREFIX + tradeOrderDto.getOrderSn(), "success", 24, TimeUnit.HOURS);

                    logger.info("[微信支付回调服务] ip={}, trade_no={}, 添加交易加款消息队列成功.", callbackRequestModel.getRequestIp(), callbackParam.get("out_trade_no"));
                    result.setCode(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespCode());
                    result.setMsg(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespMsg());
                    result.setBody(responseWx(Constants.WXReturnCode.RETURN_CODE_SUCCESS));
                } else {
                    logger.info("[微信支付回调服务] ip={}, trade_no={}, 添加交易加款消息队列失败.", callbackRequestModel.getRequestIp(), callbackParam.get("out_trade_no"));
                    result.setCode(ChannelPayResultEnum.RESPONSE_FAILURE.getRespCode());
                    result.setMsg(ChannelPayResultEnum.RESPONSE_FAILURE.getRespMsg());
                    result.setBody(responseWx(Constants.WXReturnCode.RETURN_CODE_FAIL));
                }
                return result;
            }
        } catch (Exception e) {
            logger.error("[微信支付回调服务] out_trade_no={}, 执行异常 ex={}", outTradeNo, e.getMessage(), e);
            result.setCode(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode());
            result.setMsg(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg());
            result.setBody(Constants.WXReturnCode.RETURN_CODE_FAIL);
        }
        // #5 返回结果
        logger.info("[微信支付回调服务] ******************执行结束******************");
        return result;
    }

    @Override
    public ChannelResponseResult<Map> queryOrder(QueryOrderRequestModel queryOrderRequestModel) {
        logger.info("[WeChat Query Service] ******************开始执行******************");
        ChannelResponseResult<Map> result = new ChannelResponseResult<>(ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg(), null);
        try {
            final String orderSn = queryOrderRequestModel.getOrderSn();

            // #1 查询本地订单
            VoResult<TradeOrderDto> tradeOrderDtoVoResult = iTradeOrderService.getOrderInfo(orderSn);
            if (!tradeOrderDtoVoResult.isSuccess()) {
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespMsg());
                return result;
            }

            final TradeOrderDto tradeOrderDto = tradeOrderDtoVoResult.getData();

            // #2 查询网关信息
            GatewayChannelDto gatewayChannelDto = new GatewayChannelDto.Builder()
                    .channelCode(tradeOrderDto.getChannelCode())
                    .build();
            VoResult<GatewayChannelDto> gatewayChannelDtoVoResult = iGatewayChannelService.getGatewayChannelInfo(gatewayChannelDto);
            if (!gatewayChannelDtoVoResult.isSuccess()) {
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_NOT_FOUND.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_NOT_FOUND.getRespMsg());
                return result;
            }

            final String appId = gatewayChannelDto.getChannelKey();
            final String key = gatewayChannelDto.getChannelSecret();
            JSONObject channelParamsJson = JSON.parseObject(gatewayChannelDto.getChannelParams());
            String mchId = channelParamsJson.containsKey("mch_id") ? channelParamsJson.getString("mch_id") : "";

            WXPayConfigImpl wxPayConfig = WXPayConfigImpl.getInstance();
            wxPayConfig.init(appId, mchId, key, false);
            WXPay wxPay = new WXPay(wxPayConfig);

            Map<String, String> reqData = new HashMap<>(8);
            reqData.put("out_trade_no", orderSn);
            Map<String, String> r = wxPay.orderQuery(wxPay.fillRequestData(reqData));
            logger.info("[WeChat Query Service] 响应结果={}", r);
            if (!WXPayUtil.isSignatureValid(r, key, WXPayConstants.SignType.HMACSHA256)) {
                logger.info("[WeChat Query Service] 检验签名错误.");
                result.setCode(ChannelPayResultEnum.ILLEGAL_SIGN_VALUE.getRespCode());
                result.setMsg(ChannelPayResultEnum.ILLEGAL_SIGN_VALUE.getRespMsg());
            }

            String returnCode = r.getOrDefault("return_code", "");
            if (!"SUCCESS".equals(returnCode)) {
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespMsg());
            }

            String resultCode = r.getOrDefault("result_code", "");
            String tradeCode = r.getOrDefault("trade_state", "");
            String tradeStateDesc = r.getOrDefault("trade_state_desc", "");
            logger.info("[WeChat Query Service] 订单号={}, 交易状态说明={}", queryOrderRequestModel.getOrderSn(), tradeStateDesc);
            if ("SUCCESS".equals(resultCode) ) {
                Map<String, String> data = new HashMap<>(4);
                if ("SUCCESS".equals(tradeCode)) {
                    if (StatusConstants.TRADE_WAIT_BANK_HANDLE == tradeOrderDto.getOrderStatus()) {
                        MessageQueueDto messageQueueDto = new MessageQueueDto();
                        messageQueueDto.setConsumerStatus(0);
                        messageQueueDto.setThreadStatus(0);
                        messageQueueDto.setDateline(new Date(System.currentTimeMillis()));
                        messageQueueDto.setMessageBody("{\"order_sn\":\"" + tradeOrderDto.getOrderSn() + "\", \"partner_id\":\"" + tradeOrderDto.getPartnerId() + "\"}");
                        messageQueueDto.setMessageHost("");
                        messageQueueDto.setMessageTopic(XmoPayConstants.TOPIC_TRADE_PAY_WAIT_SETTLE);
                        messageQueueDto.setOrderSn(tradeOrderDto.getOrderSn());
                        messageQueueDto.setPartnerId(tradeOrderDto.getPartnerId());
                        VoResult<Integer> insertMessageQueueVoResult = iMessageQueueService.insertMessageQueue(messageQueueDto);
                        if (insertMessageQueueVoResult.isSuccess() && insertMessageQueueVoResult.getData() > 0) {
                            logger.info("[WeChat Query Service] 订单支付成功, 添加加款消息成功. trade_no={}, trade_code={}, order_status={}", orderSn, tradeCode, tradeOrderDto.getOrderStatus());
                            result.setCode(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespCode());
                            result.setMsg(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespMsg());
                            return result;
                        }
                    } else {
                        logger.info("[WeChat Query Service] 订单支付成功, 本地订单状态已变更, 不做处理. trade_no={}, trade_code={}, order_status={}", orderSn, tradeCode, tradeOrderDto.getOrderStatus());
                    }
                    data.put("order_status", "success");
                } else if ("REFUND".equals(tradeCode) || "CLOSED".equals(tradeCode) || "REVOKED".equals(tradeCode) || "PAYERROR".equals(tradeCode)) {
                    logger.info("[WeChat Query Service] 订单支付失败. trade_no={}, trade_code={}", orderSn, tradeCode);
                    data.put("order_status", "fail");
                } else {
                    logger.info("[WeChat Query Service] 订单支付中. trade_no={}, trade_code={}", orderSn, tradeCode);
                    data.put("order_status", "paying");
                }
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_SUCCESS.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_SUCCESS.getRespMsg());
            } else {
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespMsg());
            }
        } catch (Exception e) {
            result.setCode(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode());
            result.setMsg(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg());
        }
        return result;
    }

    @Override
    public BaseChannelService getBean(Class clazz) {
        return (WxPayChannelServiceImpl) context.getBean(clazz);
    }

    @Override
    public BaseChannelService getBean(String className) {
        return (WxPayChannelServiceImpl) context.getBean(className);
    }

    @Override
    protected String buildAsyncUrl() {
        return "wxPay";
    }

    private String responseWx(String returnCode) {
        return "<xml><return_code><![CDATA["+returnCode+"]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }
}
