package com.xmopay.funds.dao;

import com.xmopay.funds.dto.PartnerProductDto;

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
}
