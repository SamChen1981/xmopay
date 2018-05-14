package com.xmopay.dao;

import com.xmopay.dto.TradeOrderDto;

/**
 * com.xmopay.dao
 *
 * @author echo_coco.
 * @date 4:00 PM, 2018/4/28
 */
public interface TradeOrderDao {

    TradeOrderDto getTradeOrderInfo(String orderSn);

    int updateTradeOrder(TradeOrderDto tradeOrderDto);

}
