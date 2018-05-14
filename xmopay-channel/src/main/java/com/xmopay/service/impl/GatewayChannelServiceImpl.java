package com.xmopay.service.impl;

import com.xmopay.dao.GatewayChannelDao;
import com.xmopay.dto.GatewayChannelDto;
import com.xmopay.service.IGatewayChannelService;
import com.xmopay.vo.VoResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * com.xmopay.service.impl
 *
 * @author echo_coco.
 * @date 11:41 AM, 2018/5/2
 */
@Service
public class GatewayChannelServiceImpl implements IGatewayChannelService {

    @Autowired
    private GatewayChannelDao gatewayChannelDao;

    @Override
    public VoResult<GatewayChannelDto> getGatewayChannelInfo(GatewayChannelDto gatewayChannelDto) {
        VoResult<GatewayChannelDto> result = new VoResult<>("10000", false, "", null);
        try {
            GatewayChannelDto gatewayChannel = gatewayChannelDao.getGatewayChannelInfo(gatewayChannelDto);
            if (gatewayChannel != null && gatewayChannel.getChannelId() > 0) {
                result.setCode("0000");
                result.setData(gatewayChannel);
                result.setErrMessage("");
                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setErrMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }
}
