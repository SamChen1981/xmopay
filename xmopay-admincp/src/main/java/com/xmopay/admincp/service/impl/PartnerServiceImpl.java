package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.PartnerDao;
import com.xmopay.admincp.dto.PartnerDto;
import com.xmopay.admincp.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PartnerServiceImpl implements PartnerService {

    @Autowired
    private PartnerDao partnerDao;

    @Override
    public SingleResult<PageInfo> getPartnerPageList(PartnerDto partnerDto) {
        SingleResult<PageInfo> result = new SingleResult<>(false, null);
        PageHelper.startPage(partnerDto.getCurrentPage(), partnerDto.getPageSize());
        List<PartnerDto> lists = partnerDao.getListAllPartners(partnerDto);
        PageInfo<PartnerDto> pageInfo = new PageInfo<>(lists);
        if (null != lists && lists.size() > 0) {
            result.setResult(pageInfo);
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED, timeout = 1000, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public SingleResult<Integer> insertPartner(PartnerDto partnerDto) {
        SingleResult<Integer> result = new SingleResult<>(false, 0);
        try{
            Integer pline = partnerDao.insertPartner(partnerDto);

            Integer aline = partnerDao.insertPartnerAccount(partnerDto);

            if (pline == 1 && aline == 1) {
                result.setSuccess(true);
                result.setResult(1);
            }else{
                result.setSuccess(false);
                result.setResult(0);
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            throw new RuntimeException("Transactional rollback runtimeException!");
        }
        return result;
    }

    @Override
    public SingleResult<List<PartnerDto>> getPartnersList(PartnerDto partnerDto) {
        SingleResult<List<PartnerDto>> result = new SingleResult<>(false, null);
        try {
            List<PartnerDto> partnersList = partnerDao.getPartnersList(partnerDto);
            if (null != partnersList && partnersList.size() > 0) {
                result.setResult(partnersList);
                result.setSuccess(true);
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<PartnerDto> getPartnerInfo(PartnerDto partnerDto) {
        SingleResult<PartnerDto> result = new SingleResult<>(false, null);
        try {
            PartnerDto partner = partnerDao.getPartnerInfo(partnerDto);
            if (null != partner && partner.getPtid() > 0) {
                result.setResult(partner);
                result.setSuccess(true);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<Integer> updatePartner(PartnerDto partnerDto) {
        SingleResult<Integer> result = new SingleResult<>(false, 0);
        Integer n = partnerDao.updatePartner(partnerDto);
        if (n ==1 ){
            result.setResult(n);
            result.setSuccess(true);
        }
        return result;
    }
}

















