package com.xmopay.service;

import com.xmopay.dto.GatewayChannelDto;
import com.xmopay.vo.VoResult;

/**
 * com.xmopay.service
 *
 * @author echo_coco.
 * @date 11:36 AM, 2018/5/2
 */
public interface IGatewayChannelService {

    VoResult<GatewayChannelDto> getGatewayChannelInfo(GatewayChannelDto gatewayChannelDto);
}
