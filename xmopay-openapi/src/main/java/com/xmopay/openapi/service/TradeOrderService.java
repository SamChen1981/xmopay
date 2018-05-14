package com.xmopay.openapi.service;

import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dto.TradeOrderDto;

/**
 * Created by mimi on 4/05/2018.
 */
public interface TradeOrderService {

    /**
     * 订单入库
     * @param tradeOrderDto
     * @return
     */
    SingleResult<Integer> insertTradeOrder(TradeOrderDto tradeOrderDto);

    /**
     * 更新订单信息
     *
     * @param tradeOrderDto
     * @return
     */
    SingleResult<Integer> updateTradeOrder(TradeOrderDto tradeOrderDto);

    /**
    * @param
    * @Description: 订单详情
    * @return
    */
    SingleResult<TradeOrderDto> getTradeOrderInfo(TradeOrderDto tradeOrderDto);


}















