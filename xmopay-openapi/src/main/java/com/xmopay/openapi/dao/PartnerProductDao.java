package com.xmopay.openapi.dao;

import com.xmopay.openapi.dto.PartnerProductDto;

import java.util.List;

/**
 * Created by mimi on 2018/05/08
 */
public interface PartnerProductDao {

    /**
     * 产品信息
     * @param partnerProductDto
     * @return
     */
    PartnerProductDto getPartnerProduct(PartnerProductDto partnerProductDto);

    /**
     * @Description: 查询商户产品list
     */
    List<PartnerProductDto> getPartnerProductList(PartnerProductDto partnerProductDto);
}
