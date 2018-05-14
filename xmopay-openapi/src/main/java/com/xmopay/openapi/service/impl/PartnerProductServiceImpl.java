package com.xmopay.openapi.service.impl;

import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dao.PartnerProductDao;
import com.xmopay.openapi.dto.PartnerProductDto;
import com.xmopay.openapi.service.PartnerProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by monica on 21/03/2018.
 */
@Service("partnerProductService")
public class PartnerProductServiceImpl implements PartnerProductService {

    @Autowired
    private PartnerProductDao partnerProductDao;

    @Override
    public SingleResult<PartnerProductDto> getPartnersProduct(PartnerProductDto partnerProductDto) {
        SingleResult<PartnerProductDto> result = new SingleResult<>(false, null);
        try {
            PartnerProductDto partnerProduct = partnerProductDao.getPartnerProduct(partnerProductDto);
            if (null != partnerProduct) {
                result.setResult(partnerProduct);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<List<PartnerProductDto>> getPartnerProductList(PartnerProductDto partnerProductDto) {
        SingleResult<List<PartnerProductDto>>  singleResult = new SingleResult<>(false, null);
        List<PartnerProductDto> partnerProductList = partnerProductDao.getPartnerProductList(partnerProductDto);
        if (null != partnerProductList) {
            singleResult.setResult(partnerProductList);
            singleResult.setSuccess(true);
        }
        return singleResult;
    }

}
