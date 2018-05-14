package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.AdminUserDto;
import com.xmopay.admincp.dto.TradeOrderDto;

import java.util.Map;

public interface TradeOrderService {
    SingleResult<PageInfo> getTradeOrderList(TradeOrderDto tradeOrderDto);

    SingleResult<Map<String,Object>> getTradeOrderTotal(TradeOrderDto tradeOrderDto);

    SingleResult<TradeOrderDto> getTradeOrderInfo(TradeOrderDto tradeOrderDto);

    SingleResult<String> editTrade(TradeOrderDto tradeInfo, Integer modifyOrderStatus, AdminUserDto adminUserDto);
}
