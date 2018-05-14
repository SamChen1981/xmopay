package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.GatewayBalanceDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GatewayBalanceDao {
    List<GatewayBalanceDto> getGatewayBalanceList(GatewayBalanceDto gatewayBalanceDto);

    Map getSumBalance(GatewayBalanceDto gatewayBalanceDto);
}
