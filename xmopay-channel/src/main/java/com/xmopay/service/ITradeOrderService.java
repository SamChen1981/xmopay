package com.xmopay.service;

import com.xmopay.dto.TradeOrderDto;
import com.xmopay.vo.VoResult;

/**
 * com.xmopay.service
 *
 * @author echo_coco.
 * @date 1:41 PM, 2018/5/2
 */
public interface ITradeOrderService {

    VoResult<TradeOrderDto> getOrderInfo(String orderSn);

    VoResult<Integer> updateTradeOrder(TradeOrderDto tradeOrderDto);
}
