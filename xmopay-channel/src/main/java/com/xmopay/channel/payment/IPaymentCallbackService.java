package com.xmopay.channel.payment;

import com.xmopay.channel.BaseChannelService;
import com.xmopay.common.model.CallbackRequestModel;
import com.xmopay.vo.ChannelResponseResult;

import java.util.Map;

/**
 * com.xmopay.channel.payment
 * 支付回调接口
 * @author echo_coco.
 * @date 6:44 PM, 2018/4/27
 */
public interface IPaymentCallbackService extends BaseChannelService {

    ChannelResponseResult<String> doPaymentAsyncCallback(CallbackRequestModel callbackRequestModel);

    ChannelResponseResult<Map> doPaymentRsyncCallback(CallbackRequestModel callbackRequestModel);
}
