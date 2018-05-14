package com.xmopay.channel.payment;

import com.xmopay.channel.BaseChannelService;
import com.xmopay.common.model.QueryOrderRequestModel;
import com.xmopay.vo.ChannelResponseResult;

import java.util.Map;

/**
 * com.xmopay.channel.payment
 * 支付查单接口
 * @author echo_coco.
 * @date 6:45 PM, 2018/4/27
 */
public interface IPaymentQueryTradeService extends BaseChannelService {

     ChannelResponseResult<Map> queryOrder(QueryOrderRequestModel queryOrderRequestModel);
}
