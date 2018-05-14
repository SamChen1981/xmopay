package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.GatewayAgencyDto;

import java.util.List;

public interface GatewayAgencyService {
    SingleResult<List<GatewayAgencyDto>> findGatewayAgencyList(GatewayAgencyDto gatewayAgencyDto);

    SingleResult<PageInfo> getGatewayAgencyPageList(GatewayAgencyDto dto);

    SingleResult<GatewayAgencyDto> getGatewayAgencyById(int i);

    SingleResult<Integer> insertGatewayAgency(GatewayAgencyDto dto);

    SingleResult updateGatewayAgency(GatewayAgencyDto dto);
}
