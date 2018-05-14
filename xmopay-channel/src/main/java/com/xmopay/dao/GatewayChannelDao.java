package com.xmopay.dao;

import com.xmopay.dto.GatewayChannelDto;

/**
 * com.xmopay.dao
 *
 * @author echo_coco.
 * @date 3:54 PM, 2018/4/28
 */
public interface GatewayChannelDao {

    GatewayChannelDto getGatewayChannelInfo(GatewayChannelDto gatewayChannelDto);
}
