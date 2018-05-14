package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.GatewayBalanceDao;
import com.xmopay.admincp.dto.GatewayBalanceDto;
import com.xmopay.admincp.service.GatewayBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GatewayBalanceServiceImpl implements GatewayBalanceService {

    @Autowired
    private GatewayBalanceDao gatewayBalanceDao;

    @Override
    public SingleResult<PageInfo> getGatewayBalancePageList(GatewayBalanceDto gatewayBalanceDto) {
        SingleResult<PageInfo> singleResult = new SingleResult<>(false,null);
        try {
            PageHelper.startPage(gatewayBalanceDto.getCurrentPage(), gatewayBalanceDto.getPageSize());
            List<GatewayBalanceDto> gatewayBalanceList = gatewayBalanceDao.getGatewayBalanceList(gatewayBalanceDto);
            PageInfo<GatewayBalanceDto> pageInfo = new PageInfo<>(gatewayBalanceList);
            if (gatewayBalanceList != null && gatewayBalanceList.size() > 0) {
                singleResult.setResult(pageInfo);
                singleResult.setSuccess(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return singleResult;
    }

    @Override
    public SingleResult<Map> getTotal(GatewayBalanceDto gatewayBalanceDto) {
        SingleResult<Map> singleResult = new SingleResult<>(false,null);
        try {
            Map sumBalance = gatewayBalanceDao.getSumBalance(gatewayBalanceDto);
            if (sumBalance != null) {
                singleResult.setResult(sumBalance);
                singleResult.setSuccess(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return singleResult;
    }

    @Override
    public SingleResult<Integer> insertGatewayBalance(GatewayBalanceDto gatewayBalance) {
        return null;
    }

    @Override
    public SingleResult<Integer> deleteGatewayBalance(GatewayBalanceDto gatewayBalanceDto) {
        return null;
    }
}
