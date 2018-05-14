package com.xmopay.openapi.dao;

import com.xmopay.openapi.dto.TradeOrderDto;

import java.sql.SQLException;

/**
 * Created by mimi on 2018/05/08
 */
public interface TradeOrderDao {

    /**
    * @param
    * @Description: 根据tradeOrderDto信息查询订单详情
    */
    TradeOrderDto getTradeOrderInfo(TradeOrderDto tradeOrderDto);

    /**
     * 订单入库
     * @param tradeOrderDto
     * @return
     */
    int insertTradeOrder(TradeOrderDto tradeOrderDto) throws SQLException;


    /**
    * @param tradeOrderDto
    * @Description: 更新订单
    * @return
    */
    int updateTradeOrder(TradeOrderDto tradeOrderDto);
}














