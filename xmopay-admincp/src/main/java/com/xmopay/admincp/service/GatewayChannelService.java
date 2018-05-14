package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.GatewayChannelDto;

import java.util.List;

public interface GatewayChannelService {

    SingleResult<List<GatewayChannelDto>> getAutoGatewayChannelList(GatewayChannelDto gatewayChannelDto);

    SingleResult<Integer> updateAutoGatewayChannelByChannelId(GatewayChannelDto gatewayChannelDto1);

    SingleResult<PageInfo> getChannelPageList(GatewayChannelDto gatewayChannelDto);

    SingleResult<GatewayChannelDto> getGatewayChannelById(int channelId);

    SingleResult insertGatewayChannel(GatewayChannelDto dto);

    SingleResult updateGatewayChannel(GatewayChannelDto dto);

    SingleResult<Integer> deleteGatewayChannel(Integer channelId);

    SingleResult<List<GatewayChannelDto>> findGatewayChannelList(GatewayChannelDto gatewayChannel);

}
