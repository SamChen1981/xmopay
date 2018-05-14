package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.PartnerProductDao;
import com.xmopay.admincp.dto.PartnerProductDto;
import com.xmopay.admincp.service.PartnerProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerProductServiceImpl implements PartnerProductService {

    @Autowired
    private PartnerProductDao partnerProductDao;

    @Override
    public SingleResult<PageInfo> getPartnerProductPageList(PartnerProductDto partnerProductDto) {
        SingleResult<PageInfo> singleResult = new SingleResult<>(false, null);
        PageHelper.startPage(partnerProductDto.getCurrentPage(), partnerProductDto.getPageSize());
        List<PartnerProductDto> partnerProductList = partnerProductDao.getPartnerProductList(partnerProductDto);
        PageInfo<PartnerProductDto> pageInfo = new PageInfo<>(partnerProductList);
        if (null != partnerProductList && partnerProductList.size()>0) {
            singleResult.setResult(pageInfo);
            singleResult.setSuccess(true);
        }
        return singleResult;
    }

    @Override
    public SingleResult<PartnerProductDto> getPartnersProduct(PartnerProductDto partnerProductDto) {
        SingleResult<PartnerProductDto> singleResult = new SingleResult<>(false, null);
        PartnerProductDto partnerProduct = partnerProductDao.getPartnerProduct(partnerProductDto);
        if(partnerProduct != null){
            singleResult.setResult(partnerProduct);
            singleResult.setSuccess(true);
        }
        return singleResult;
    }

    @Override
    public SingleResult<Integer> insertPartnerProduct(PartnerProductDto partnerProductDto) {
        SingleResult<Integer> singleResult = new SingleResult<>(false, 0);
        try{
            Integer result = partnerProductDao.insertPartnerProduct(partnerProductDto);
            if(result == 1){
                singleResult.setResult(result);
                singleResult.setSuccess(true);
            }
            return singleResult;
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            singleResult.setResult(-1);
            return singleResult;
        }
    }

    @Override
    public SingleResult<Integer> updatePartnerProduct(PartnerProductDto partnerProductDto) {
        SingleResult<Integer> singleResult = new SingleResult<>(false, 0);
        try {
            Integer result = partnerProductDao.updatePartnerProduct(partnerProductDto);
            if (result == 1) {
                singleResult.setResult(result);
                singleResult.setSuccess(true);
            }
        }catch (DuplicateKeyException e){
            e.printStackTrace();
            singleResult.setResult(-1);
        }
        return singleResult;
    }
}
