package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.GatewayChannelDao;
import com.xmopay.admincp.dto.GatewayChannelDto;
import com.xmopay.admincp.service.GatewayChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GatewayChannelServiceImpl implements GatewayChannelService {

    @Autowired
    private GatewayChannelDao gatewayChannelDao;

    @Override
    public SingleResult<List<GatewayChannelDto>> getAutoGatewayChannelList(GatewayChannelDto gatewayChannelDto) {
        return null;
    }

    @Override
    public SingleResult<Integer> updateAutoGatewayChannelByChannelId(GatewayChannelDto gatewayChannelDto1) {
        return null;
    }

    @Override
    public SingleResult<PageInfo> getChannelPageList(GatewayChannelDto gatewayChannelDto) {
        SingleResult<PageInfo> result = new SingleResult<>(false, null);
        PageHelper.startPage(gatewayChannelDto.getCurrentPage(), gatewayChannelDto.getPageSize());
        List<GatewayChannelDto> lists = gatewayChannelDao.getGatewayChannelList(gatewayChannelDto);
        PageInfo<GatewayChannelDto> pageInfo = new PageInfo<>(lists);
        if (null != lists && lists.size() > 0) {
            result.setResult(pageInfo);
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    public SingleResult<GatewayChannelDto> getGatewayChannelById(int channelId) {
        SingleResult<GatewayChannelDto> result = new SingleResult<>(false, null);
        try {
            GatewayChannelDto dto = gatewayChannelDao.getGatewayChannelById(channelId);
            if (dto != null) {
                result.setResult(dto);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<Integer> insertGatewayChannel(GatewayChannelDto gatewayChannelDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try {
            int line = gatewayChannelDao.insertGatewayChannel(gatewayChannelDto);
            if (line > 0) {
                result.setResult(line);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult updateGatewayChannel(GatewayChannelDto gatewayChannelDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try {
            int line = gatewayChannelDao.updateGatewayChannel(gatewayChannelDto);
            if (line > 0) {
                result.setResult(line);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<Integer> deleteGatewayChannel(Integer channelId) {
        SingleResult<Integer> result = new SingleResult<>(false, 0);
        int state = gatewayChannelDao.deleteGatewayChannel(channelId);
        if (state > 0) {
            result.setResult(state);
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    public SingleResult<List<GatewayChannelDto>> findGatewayChannelList(GatewayChannelDto gatewayChannelDto) {
        SingleResult<List<GatewayChannelDto>> result = new SingleResult<>(false, null);
        try {
            List<GatewayChannelDto> lists = gatewayChannelDao.findGatewayChannelList(gatewayChannelDto);
            if (null != lists && lists.size() > 0) {
                result.setResult(lists);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
