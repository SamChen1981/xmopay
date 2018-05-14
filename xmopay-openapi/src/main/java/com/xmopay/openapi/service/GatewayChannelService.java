package com.xmopay.openapi.service;

import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dto.GatewayChannelDto;

public interface GatewayChannelService {

    /**
     * 根据渠道实体查询渠道信息
     * @param gatewayChannelDto
     * @return
     */
    SingleResult<GatewayChannelDto> getGatewayChannelInfo(GatewayChannelDto gatewayChannelDto);

    SingleResult<GatewayChannelDto> getGatewayChannelByCode(String channelCode);

}
