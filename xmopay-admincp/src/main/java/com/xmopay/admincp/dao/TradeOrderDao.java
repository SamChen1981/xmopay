package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.TradeOrderDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TradeOrderDao {
    List<TradeOrderDto> getTradeOrderList(TradeOrderDto tradeOrderDto);

    Map<String,Object> getTradeOrderTotal(TradeOrderDto tradeOrderDto);

    TradeOrderDto getTradeOrderInfo(TradeOrderDto tradeOrderDto);

    Integer updateTradeOrder(TradeOrderDto finalTradeOrder);
}
