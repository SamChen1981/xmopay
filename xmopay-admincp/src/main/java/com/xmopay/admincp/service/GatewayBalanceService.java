package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.GatewayBalanceDto;

import java.util.Map;

public interface GatewayBalanceService {

    SingleResult<PageInfo> getGatewayBalancePageList(GatewayBalanceDto gatewayBalanceDto);

    SingleResult<Map> getTotal(GatewayBalanceDto gatewayBalanceDto);

    SingleResult<Integer> insertGatewayBalance(GatewayBalanceDto gatewayBalance);

    SingleResult<Integer> deleteGatewayBalance(GatewayBalanceDto gatewayBalanceDto);
}
