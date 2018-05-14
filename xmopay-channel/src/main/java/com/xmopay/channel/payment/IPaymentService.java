package com.xmopay.channel.payment;

import com.xmopay.channel.BaseChannelService;
import com.xmopay.common.model.PayRequestModel;
import com.xmopay.vo.ChannelResponseResult;

import java.util.Map;

/**
 * com.xmopay.channel.payment
 * 支付接口
 * @author echo_coco.
 * @date 6:17 PM, 2018/4/27
 */
public interface IPaymentService extends BaseChannelService {

    ChannelResponseResult<Map> doPayment(PayRequestModel tradeOrderRequest);
}
