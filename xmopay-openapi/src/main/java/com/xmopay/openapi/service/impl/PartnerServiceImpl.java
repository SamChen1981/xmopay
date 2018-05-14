package com.xmopay.openapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dao.PartnerDao;
import com.xmopay.openapi.dto.PartnerDto;
import com.xmopay.openapi.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("partnerService")
public class PartnerServiceImpl implements PartnerService {

    @Autowired
    private PartnerDao partnerDao;

    @Override
    public SingleResult<PartnerDto> getPartnerInfo(PartnerDto partnerDto) {
        SingleResult<PartnerDto> result = new SingleResult<>(false, null);
        try {
            PartnerDto dto = partnerDao.getPartnerInfo(partnerDto);
            if (null != dto) {
                result.setResult(dto);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<PartnerDto> getPartnerAccount(PartnerDto partnerDto) {
        SingleResult<PartnerDto> result = new SingleResult<>(false, null);
        try {
            PartnerDto partnerResult = partnerDao.getPartnerAccount(partnerDto);
            if (null != partnerResult) {
                result.setResult(partnerResult);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<String> getPartnerAccountList(PartnerDto partnerDto) {
        SingleResult<String> result = new SingleResult<>(false, null);
        try {
            List<PartnerDto> partnerDtoList = partnerDao.getPartnerAccountList(partnerDto);
            if (null != partnerDtoList && partnerDtoList.size() > 0) {
                result.setResult(JSONArray.toJSONString(partnerDtoList));
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
