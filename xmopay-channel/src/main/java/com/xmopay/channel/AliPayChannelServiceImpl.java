package com.xmopay.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.xmopay.common.Constants;
import com.xmopay.common.model.CallbackRequestModel;
import com.xmopay.common.model.PayRequestModel;
import com.xmopay.common.model.QueryOrderRequestModel;
import com.xmopay.common.constant.StatusConstants;
import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.common.utils.XmoPayUtils;
import com.xmopay.dto.GatewayChannelDto;
import com.xmopay.dto.MessageQueueDto;
import com.xmopay.dto.TradeOrderDto;
import com.xmopay.dto.ext.TradeOrderDtoExt;
import com.xmopay.enums.ChannelPayResultEnum;
import com.xmopay.service.IGatewayChannelService;
import com.xmopay.service.IMessageQueueService;
import com.xmopay.service.IRedisService;
import com.xmopay.service.ITradeOrderService;
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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * com.xmopay.channel
 * Alipay
 *
 * @author echo_coco.
 * @date 6:42 PM, 2018/4/27
 */
@Service("aliPayChannelServiceImpl")
public class AliPayChannelServiceImpl extends AbstractBaseChannelService {
    private static final Logger logger = LoggerFactory.getLogger(AliPayChannelServiceImpl.class);

    private static final String ALIPAY_SUCCESS_MSG = "Success";
    private static final String ALIPAY_SUCCESS_CODE = "10000";

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
        logger.info("[Ali Pay Service] ******************开始执行******************");
        ChannelResponseResult<Map> result = new ChannelResponseResult<>(ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg(), null);

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
                .orderSn(tradeOrderDto.getOrderSn())
                .orderStatus(StatusConstants.TRADE_WAIT_BANK_HANDLE)
                .partnerId(tradeOrderDto.getPartnerId())
                .oriOrderStatus(StatusConstants.TRADE_WAIT_TO_BANK).build();
        VoResult<Integer> updateTradeOrderVoResult = iTradeOrderService.updateTradeOrder(tradeOrderDtoExt);
        if (!updateTradeOrderVoResult.isSuccess()) {
            result.setCode(ChannelPayResultEnum.TRADE_ORDER_OTHER_ERROR.getRespCode());
            result.setMsg(ChannelPayResultEnum.TRADE_ORDER_OTHER_ERROR.getRespMsg());
            return result;
        }

        final String appId = gatewayChannelDto.getChannelKey();
        final String appPrivateKey = gatewayChannelDto.getChannelSecret();
        JSONObject channelParamsJson = JSON.parseObject(gatewayChannelDto.getChannelParams());
        final String alipayPublicKey = channelParamsJson.containsKey("alipay_public_key") ? channelParamsJson.getString("alipay_public_key") : "";
        JSONObject agencyParamsJson = JSON.parseObject(gatewayChannelDto.getGatewayAgencyDto().getAgencyParams());
        final String serverUrl = agencyParamsJson.containsKey("server_url") ? agencyParamsJson.getString("server_url") : "";
        final String asyncDomain = channelParamsJson.containsKey("async_domain") ? channelParamsJson.getString("async_domain") : "";
        // #3 上送
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl,
                appId,
                appPrivateKey,
                Constants.JSON,
                Constants.CHARSET_UTF8,
                alipayPublicKey,
                Constants.RSA2SHA256);

        // 创建API对应的request类
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        // 设置业务参数
        String subject = XmoPayUtils.isEmpty(tradeOrderDto.getOrderTitle()) ? "subject:::" + tradeOrderDto.getOrderSn() : tradeOrderDto.getOrderTitle();
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + tradeOrderDto.getOrderSn() + "\"," +
                "\"total_amount\":\"" + tradeOrderDto.getOrderAmount() + "\"," +
                "\"subject\":\"" + subject + "\"," +
                "\"store_id\":\"" + "P" + payRequestModel.getOrderSn() + "\"," +
                "\"timeout_express\":\"90m\"}");
        request.setNotifyUrl(asyncDomain + getAsynApiName());
        try {
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            logger.info("[Ali Pay Service] 响应结果={}", response.getBody());
            if (response.isSuccess() && ALIPAY_SUCCESS_MSG.equals(response.getMsg()) && ALIPAY_SUCCESS_CODE.equals(response.getCode())) {
                Map<String, String> body = new HashMap<>(1);
                body.put("qr_url", response.getQrCode());
                body.put("order_sn", payRequestModel.getOrderSn());

                // #4 返回结果信息
                result.setCode(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespCode());
                result.setMsg(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespMsg());
                result.setBody(body);
            } else {
                logger.info("[Ali Pay Service] 上送失败. msg={}", response.getMsg());
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespMsg());
                result.setBody(null);
            }
            logger.info("[Ali Pay Service] ******************执行结束******************");
        } catch (AlipayApiException e) {
            logger.error("[Ali Pay Service] 上送异常.", e);
            result.setCode(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespCode());
            result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespMsg());
            result.setBody(null);
        } catch (Exception e) {
            logger.error("[Ali Pay Service] 程序异常.", e);
            result.setCode(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode());
            result.setMsg(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg());
            result.setBody(null);
        }
        return result;
    }

    @Override
    public ChannelResponseResult<String> doPaymentAsyncCallback(CallbackRequestModel callbackRequestModel) {
        logger.info("[支付宝支付回调服务] ******************执行开始******************");
        ChannelResponseResult<String> result = new ChannelResponseResult<>(ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg(), null);
        String outTradeNo = "";
        try {
            logger.info("[支付宝支付回调服务] 通知参数={}", callbackRequestModel.getBody());
            Map<String, String> callbackParam = JSON.parseObject(callbackRequestModel.getBody(), Map.class);
            outTradeNo = callbackParam.getOrDefault("out_trade_no", "0").toString();
            /*final String cacheTradeNo = iRedisService.getValue(Constants.TRADE_CACHE_PREFIX + outTradeNo);

            // #0 监测订单缓存 @TODO 缓存必须配上
            if ("success".equals(iRedisService.getValue(Constants.TRADE_CACHE_PREFIX + callbackParam.getOrDefault("out_trade_no", "0")))) {
                logger.info("[支付宝支付回调服务] ip={}, trade_no={}, cache_trade_no={}, 本地缓存已存在改订单.", callbackRequestModel.getRequestIp(), outTradeNo, cacheTradeNo);
                result.setCode(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespCode());
                result.setMsg(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespMsg());
                result.setBody(Constants.AliPayReturnCode.RETURN_CODE_SUCCESS);
                return result;
            }*/

            String tradeStatus = callbackParam.getOrDefault("trade_status", "").toString();
            if (!"TRADE_SUCCESS".equals(tradeStatus)) {
                logger.info("[支付宝支付回调服务] ip={}, trade_no={}, return_code={}, 支付宝返回状态码错误.", callbackRequestModel.getRequestIp(), outTradeNo, tradeStatus);
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_FAILURE.getRespMsg());
                result.setBody(Constants.AliPayReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            // #2 获取订单信息
            final String orderSn = callbackParam.getOrDefault("out_trade_no", "").toString();
            VoResult<TradeOrderDto> tradeOrderDtoVoResult = iTradeOrderService.getOrderInfo(orderSn);
            if (!tradeOrderDtoVoResult.isSuccess()) {
                logger.info("[支付宝支付回调服务] ip={}, trade_no={}, 订单号错误, 获取本地订单失败.", callbackRequestModel.getRequestIp(), outTradeNo);
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespMsg());
                result.setBody(Constants.AliPayReturnCode.RETURN_CODE_SUCCESS);
                return result;
            }

            TradeOrderDto tradeOrderDto = tradeOrderDtoVoResult.getData();

            if (tradeOrderDto.getOrderAmount().compareTo(new BigDecimal(callbackParam.getOrDefault("total_amount", "0"))) != 0) {
                logger.info("[支付宝支付回调服务] ip={}, trade_no={}, order_amount={}, total_fee={}, 订单金额错误.", callbackRequestModel.getRequestIp(), outTradeNo, tradeOrderDto.getOrderAmount(), callbackParam.getOrDefault("total_fee", "0"));
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_AMOUNT_ERROR.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_AMOUNT_ERROR.getRespMsg());
                result.setBody(Constants.AliPayReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            if (StatusConstants.TRADE_SUCCESS == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_CLOSE == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_FAILURE == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_EXCEPTION == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_REFUND == tradeOrderDto.getOrderStatus()) {
                logger.info("[支付宝支付回调服务] ip={}, trade_no={}, trade_status={}, 本订单状态有误.", callbackRequestModel.getRequestIp(), outTradeNo, tradeOrderDto.getOrderStatus());
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespMsg());
                result.setBody(Constants.AliPayReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            // #3 获取渠道信息
            GatewayChannelDto gatewayChannelDto = new GatewayChannelDto.Builder()
                    .channelCode(tradeOrderDto.getChannelCode())
                    .build();
            VoResult<GatewayChannelDto> gatewayChannelDtoVoResult = iGatewayChannelService.getGatewayChannelInfo(gatewayChannelDto);
            if (!gatewayChannelDtoVoResult.isSuccess()) {
                logger.info("[支付宝支付回调服务] ip={}, trade_no={}, channel_code={}, 获取渠道信息失败", callbackRequestModel.getRequestIp(), outTradeNo, tradeOrderDto.getChannelCode());
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_NOT_FOUND.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_NOT_FOUND.getRespMsg());
                result.setBody(Constants.AliPayReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            gatewayChannelDto = gatewayChannelDtoVoResult.getData();
            JSONObject channelParamsJson = JSON.parseObject(gatewayChannelDto.getChannelParams());
            final String alipayPublicKey = channelParamsJson.containsKey("alipay_public_key") ? channelParamsJson.getString("alipay_public_key") : "";
            // #4 校验签名
            callbackParam.remove("sign_type");
            boolean signatureValid = AlipaySignature.rsaCheckV2(callbackParam, alipayPublicKey, Constants.CHARSET_UTF8, Constants.RSA2SHA256);
            if (!signatureValid) {
                logger.info("[支付宝支付回调服务] ip={}, trade_no={}, 校验签名错误.", callbackRequestModel.getRequestIp(), outTradeNo);
                result.setCode(ChannelPayResultEnum.CHANNEL_PAY_SIGNATURE_VALID_ERROR.getRespCode());
                result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_SIGNATURE_VALID_ERROR.getRespMsg());
                result.setBody(Constants.AliPayReturnCode.RETURN_CODE_FAIL);
                return result;
            }

            if ("TRADE_SUCCESS".equals(callbackParam.getOrDefault("trade_status", ""))
                    && (StatusConstants.TRADE_WAIT_BANK_HANDLE == tradeOrderDto.getOrderStatus() || StatusConstants.TRADE_WAIT_TO_BANK == tradeOrderDto.getOrderStatus())) {
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
                    // 设置订单缓存 @TODO 缓存必须配上
                    // iRedisService.setValue(Constants.TRADE_CACHE_PREFIX + tradeOrderDto.getOrderSn(), "success", 24, TimeUnit.HOURS);

                    logger.info("[支付宝支付回调服务] ip={}, trade_no={}, 添加交易加款消息队列成功.", callbackRequestModel.getRequestIp(), callbackParam.get("out_trade_no"));
                    result.setCode(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespCode());
                    result.setMsg(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespMsg());
                    result.setBody(Constants.AliPayReturnCode.RETURN_CODE_SUCCESS);
                } else {
                    logger.info("[支付宝支付回调服务] ip={}, trade_no={}, 添加交易加款消息队列失败.", callbackRequestModel.getRequestIp(), callbackParam.get("out_trade_no"));
                    result.setCode(ChannelPayResultEnum.RESPONSE_FAILURE.getRespCode());
                    result.setMsg(ChannelPayResultEnum.RESPONSE_FAILURE.getRespMsg());
                    result.setBody(Constants.AliPayReturnCode.RETURN_CODE_FAIL);
                }
            }
        } catch (Exception e) {
            logger.error("[支付宝支付回调服务] out_trade_no={}, 执行异常 ex={}", outTradeNo, e.getMessage(), e);
            result.setCode(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode());
            result.setMsg(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg());
            result.setBody(Constants.AliPayReturnCode.RETURN_CODE_FAIL);
        }
        logger.info("[支付宝支付回调服务] ******************执行结束******************");
        // #5 返回结果
        return result;
    }

    @Override
    public ChannelResponseResult<Map> queryOrder(QueryOrderRequestModel queryOrderRequestModel) {
        logger.info("[支付宝订单查询服务] ******************执行开始******************");
        ChannelResponseResult<Map> result = new ChannelResponseResult<>(ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg(), null);
        try {
            // #0 获取订单信息
            final String orderSn = queryOrderRequestModel.getOrderSn();
            VoResult<TradeOrderDto> tradeOrderDtoVoResult = iTradeOrderService.getOrderInfo(orderSn);
            if (!tradeOrderDtoVoResult.isSuccess()) {
                logger.info("[支付宝订单查询服务] trade_no={}, 订单号错误, 获取本地订单失败.", orderSn);
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespMsg());
                return result;
            }

            TradeOrderDto tradeOrderDto = tradeOrderDtoVoResult.getData();

            if (StatusConstants.TRADE_SUCCESS == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_CLOSE == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_FAILURE == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_EXCEPTION == tradeOrderDto.getOrderStatus()
                    || StatusConstants.TRADE_REFUND == tradeOrderDto.getOrderStatus()) {
                logger.info("[支付宝订单查询服务] trade_no={}, trade_status={}, 本订单状态有误.", orderSn, tradeOrderDto.getOrderStatus());
                result.setCode(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespCode());
                result.setMsg(ChannelPayResultEnum.TRADE_ORDER_NOT_EXIST.getRespMsg());
                return result;
            }

            // # 查询网关信息
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
            final String appPrivateKey = gatewayChannelDto.getChannelSecret();
            JSONObject channelParamsJson = JSON.parseObject(gatewayChannelDto.getChannelParams());
            final String alipayPublicKey = channelParamsJson.containsKey("alipay_public_key") ? channelParamsJson.getString("alipay_public_key") : "";

            // #1 查询支付宝订单
            AlipayClient alipayClient = new DefaultAlipayClient("serverUrl",
                    appId,
                    appPrivateKey,
                    Constants.JSON,
                    Constants.CHARSET_UTF8,
                    alipayPublicKey,
                    Constants.RSA2SHA256);
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizContent("{" +
                    "\"out_trade_no\":\"" + queryOrderRequestModel.getOrderSn() + "\"" +
                    "}");
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                logger.info("[支付宝订单查询服务] 调用成功. trade_no={}, trade_status={}", response.getOutTradeNo(), response.getTradeStatus());
                if ("TRADE_SUCCESS".equals(response.getTradeStatus())) {

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
                            logger.info("[支付宝订单查询服务] 订单支付成功, 添加加款消息成功. trade_no={}, trade_status={}, order_status={}", response.getOutTradeNo(), response.getTradeStatus(), tradeOrderDto.getOrderStatus());
                            result.setCode(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespCode());
                            result.setMsg(ChannelPayResultEnum.RESPONSE_SUCCESS.getRespMsg());

                            Map<String, String> data = new HashMap<>(4);
                            data.put("query_status", "success");
                            data.put("error_message", "查询成功");
                            data.put("order_sn", orderSn);
                            result.setBody(data);
                            return result;
                        } else {
                            Map<String, String> data = new HashMap<>(4);
                            data.put("query_status", "success");
                            data.put("error_message", "[消息队列入队失败]" + response.getMsg());
                            data.put("order_sn", orderSn);
                            result.setBody(data);
                            return result;
                        }
                    } else {
                        logger.info("[支付宝订单查询服务] 订单支付成功, 本地订单状态已变更, 不做处理. trade_no={}, trade_status={}, order_status={}", response.getOutTradeNo(), response.getTradeStatus(), tradeOrderDto.getOrderStatus());
                        Map<String, String> data = new HashMap<>(4);
                        data.put("query_status", "success");
                        data.put("error_message", "[本地业务不做处理]" + response.getMsg());
                        data.put("order_sn", orderSn);
                        result.setBody(data);
                        return result;
                    }
                }
            } else {
                logger.info("[支付宝订单查询服务] 调用失败. trade_no={}, msg={}, sub_msg={}", response.getOutTradeNo(), response.getMsg(), response.getSubMsg());
                Map<String, String> data = new HashMap<>(4);
                data.put("query_status", "fail");
                data.put("error_message", response.getMsg());
                data.put("order_sn", orderSn);
                result.setBody(data);
                return result;
            }
            result.setCode(ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode());
            result.setMsg(ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg());
            return result;
        } catch (AlipayApiException ae) {
            logger.error("[支付宝订单查询服务] ALIPAY异常 ex={}", ae.getMessage(), ae);
            result.setCode(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_EXCEPTION.getRespCode());
            result.setMsg(ChannelPayResultEnum.CHANNEL_PAY_RESPONSE_EXCEPTION.getRespMsg());
        } catch (Exception e) {
            logger.error("[支付宝订单查询服务] 执行异常 ex={}", e.getMessage(), e);
            result.setCode(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode());
            result.setMsg(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg());
        }
        logger.info("[支付宝订单查询服务] ******************执行完成******************");
        return result;
    }

    @Override
    public BaseChannelService getBean(Class clazz) {
        return (AliPayChannelServiceImpl) context.getBean(clazz);
    }

    @Override
    public BaseChannelService getBean(String className) {
        return (AliPayChannelServiceImpl) context.getBean(className);
    }

    @Override
    protected String buildAsyncUrl() {
        return "aliPay";
    }
}
