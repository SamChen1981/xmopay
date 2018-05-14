package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.GatewayAgencyDao;
import com.xmopay.admincp.dto.GatewayAgencyDto;
import com.xmopay.admincp.dto.GatewayBalanceDto;
import com.xmopay.admincp.service.GatewayAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GatewayAgencyServiceImpl implements GatewayAgencyService {

    @Autowired
    private GatewayAgencyDao gatewayAgencyDao;

    @Override
    public SingleResult<List<GatewayAgencyDto>> findGatewayAgencyList(GatewayAgencyDto gatewayAgencyDto) {
        SingleResult<List<GatewayAgencyDto>> result = new SingleResult<>(false, null);
        try {
            List<GatewayAgencyDto> lists = gatewayAgencyDao.findGatewayAgencyList(gatewayAgencyDto);
            if (null != lists && lists.size() > 0) {
                result.setResult(lists);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<PageInfo> getGatewayAgencyPageList(GatewayAgencyDto dto) {
        SingleResult<PageInfo> result = new SingleResult<>(false, null);
        try {
            PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
            List<GatewayAgencyDto> lists = gatewayAgencyDao.getGatewayAgencyList(dto);
            PageInfo<GatewayAgencyDto> pageInfo = new PageInfo<>(lists);
            if (null != lists && lists.size() > 0) {
                result.setResult(pageInfo);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<GatewayAgencyDto> getGatewayAgencyById(int gaid) {
        SingleResult<GatewayAgencyDto> result = new SingleResult<>(false, null);
        try {
            GatewayAgencyDto dto = gatewayAgencyDao.getGatewayAgencyById(gaid);
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
    public SingleResult<Integer> insertGatewayAgency(GatewayAgencyDto dto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try {
            int line = gatewayAgencyDao.insertGatewayAgency(dto);
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
    public SingleResult updateGatewayAgency(GatewayAgencyDto dto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try {
            int line = gatewayAgencyDao.updateGatewayAgency(dto);
            if (line > 0) {
                result.setResult(line);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
