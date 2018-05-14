package com.xmopay.openapi.dao;

import com.xmopay.openapi.dto.GatewayChannelDto;

/**
 * Created by mimi on 2018/05/08
 */
public interface GatewayChannelDao {

    /**
     * 网关详情
     * @param gatewayChannelDto
     * @return
     */
    GatewayChannelDto getGatewayChannelInfo(GatewayChannelDto gatewayChannelDto);

    GatewayChannelDto getGatewayChannelByCode(String channelCode);

}