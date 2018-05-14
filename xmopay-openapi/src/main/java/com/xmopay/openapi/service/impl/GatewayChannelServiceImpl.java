package com.xmopay.openapi.service.impl;

import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dao.GatewayChannelDao;
import com.xmopay.openapi.dto.GatewayChannelDto;
import com.xmopay.openapi.service.GatewayChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("gatewayChannelService")
public class GatewayChannelServiceImpl implements GatewayChannelService {
    private static final Logger logger = LoggerFactory.getLogger(GatewayChannelServiceImpl.class);

    @Autowired
    private GatewayChannelDao gatewayChannelDao;

    @Override
    public SingleResult<GatewayChannelDto> getGatewayChannelByCode(String channelCode) {
        SingleResult<GatewayChannelDto> result = new SingleResult<>(false, null);
        try {
            GatewayChannelDto dto = gatewayChannelDao.getGatewayChannelByCode(channelCode);
            if (dto != null) {
                result.setResult(dto);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<GatewayChannelDto> getGatewayChannelInfo(GatewayChannelDto gatewayChannelDto) {
        SingleResult<GatewayChannelDto> result = new SingleResult<>(false, null);
        try {
            GatewayChannelDto gatewayChannel = gatewayChannelDao.getGatewayChannelInfo(gatewayChannelDto);
            if (null != gatewayChannel) {
                result.setResult(gatewayChannel);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            e.printStackTrace();
        }

        return result;
    }

}

