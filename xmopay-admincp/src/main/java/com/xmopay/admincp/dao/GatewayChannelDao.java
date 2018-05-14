package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.GatewayChannelDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GatewayChannelDao {

    List<GatewayChannelDto> getGatewayChannelList(GatewayChannelDto gatewayChannelDto);

    GatewayChannelDto getGatewayChannelById(int channelId);

    Integer insertGatewayChannel(GatewayChannelDto gatewayChannelDto);

    Integer updateGatewayChannel(GatewayChannelDto gatewayChannelDto);

    Integer deleteGatewayChannel(Integer channelId);

    List<GatewayChannelDto> findGatewayChannelList(GatewayChannelDto gatewayChannelDto);
}
