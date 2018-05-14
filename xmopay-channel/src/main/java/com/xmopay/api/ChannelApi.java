package com.xmopay.api;

import com.alibaba.fastjson.JSON;
import com.xmopay.channel.AbstractBaseChannelService;
import com.xmopay.common.model.CallbackRequestModel;
import com.xmopay.common.model.PayRequestModel;
import com.xmopay.common.model.QueryOrderRequestModel;
import com.xmopay.dto.GatewayChannelDto;
import com.xmopay.enums.ChannelPayResultEnum;
import com.xmopay.service.IGatewayChannelService;
import com.xmopay.vo.ChannelResponseResult;
import com.xmopay.vo.VoResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * com.xmopay.api
 *
 * @author echo_coco.
 * @date 6:47 PM, 2018/4/27
 */
@RestController
@RequestMapping(value = "channel")
public class ChannelApi extends BasicApi {
    private static final Logger logger = LoggerFactory.getLogger(ChannelApi.class);

    private final ConfigurableApplicationContext context;

    @Autowired
    private IGatewayChannelService iGatewayChannelService;

    @Autowired
    public ChannelApi(ConfigurableApplicationContext context) {
        this.context = context;
    }

    /**
     * 渠道上送
     * @return
     */
    @RequestMapping(value = "pay", method = RequestMethod.POST, consumes = {"application/json"})
    public String payToChannel(@RequestBody @Validated PayRequestModel payRequestModel) {
        Map<String, String> result = new HashMap<>(2);
        result.put("return_code", ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode());
        result.put("return_message", ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg());
        try {
            GatewayChannelDto gatewayChannelDto = new GatewayChannelDto.Builder().channelCode(payRequestModel.getChannelCode()).build();
            VoResult<GatewayChannelDto> gatewayChannelDtoVoResult = iGatewayChannelService.getGatewayChannelInfo(gatewayChannelDto);
            if (!gatewayChannelDtoVoResult.isSuccess()) {
                return responseString(ChannelPayResultEnum.CHANNEL_PAY_NOT_FOUND.getRespCode(), ChannelPayResultEnum.CHANNEL_PAY_NOT_FOUND.getRespMsg(), null);
            }
            String serviceName = gatewayChannelDtoVoResult.getData().getGatewayAgencyDto().getAgencyCode().split("\\_")[0];
            AbstractBaseChannelService baseChannelService = (AbstractBaseChannelService) context.getBean(serviceName);
            ChannelResponseResult<Map> ret = baseChannelService.doPayment(payRequestModel);
            if (ChannelPayResultEnum.RESPONSE_SUCCESS.getRespCode().equals(ret.getCode())) {
                return responseString(ret.getCode(), ret.getMsg(), JSON.toJSONString(ret.getBody()));
            }
        } catch (BeansException be) {
            be.printStackTrace();
            logger.error("客户端请求渠道代码错误 channel_code={}", payRequestModel.getChannelCode(), be);
            return responseString(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg(), null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("客户端请求渠道上送异常", e);
            return responseString(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg(), null);
        }
        return responseString(ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg(), null);
    }

    /**
     * 支付异步回调
     * @return
     */
    @RequestMapping(value = "callback/asyncPayment", method = RequestMethod.POST)
    public String doPaymentAsyncCallback(@RequestBody CallbackRequestModel callbackRequestModel) {
        Map<String, String> result = new HashMap<>(2);
        result.put("return_code", ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode());
        result.put("return_message", ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg());
        try {
            AbstractBaseChannelService baseChannelService = (AbstractBaseChannelService) context.getBean(callbackRequestModel.getChannelCode());
            ChannelResponseResult<String> ret = baseChannelService.doPaymentAsyncCallback(callbackRequestModel);
            if (ChannelPayResultEnum.RESPONSE_SUCCESS.getRespCode().equals(ret.getCode())) {
                return responseString(ret.getCode(), ret.getMsg(), JSON.toJSONString(ret.getBody()));
            }
        } catch (BeansException be) {
            logger.error("客户端请求渠道代码错误 channel_code={}", callbackRequestModel.getChannelCode(), be);
            return responseString(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg(), null);
        } catch (Exception e) {
            logger.error("客户端请求渠道上送异常", e);
            return responseString(ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg(), null);
        }
        return responseString(ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg(), null);
    }

    /**
     * 查单
     */
    @RequestMapping(value = "queryOrder", method = RequestMethod.POST)
    public ChannelResponseResult<Map> channelToQueryOrder(@RequestBody QueryOrderRequestModel queryOrderRequestModel) {
        ChannelResponseResult<Map> result = new ChannelResponseResult<>(ChannelPayResultEnum.UNKNOWN_ERROR.getRespCode(), ChannelPayResultEnum.UNKNOWN_ERROR.getRespMsg(), null);
        try {
            AbstractBaseChannelService baseChannelService = (AbstractBaseChannelService) context.getBean(queryOrderRequestModel.getChannelCode());
            result = baseChannelService.queryOrder(null);
        } catch (BeansException be) {
            logger.error("客户端请求渠道代码错误 channel_code={}", "wxPayChannelServiceImpl", be);
        } catch (Exception e) {
            logger.error("客户端请求渠道上送异常", e);
        }
        return result;
    }

}
