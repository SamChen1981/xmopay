package com.xmopay.openapi.service;

import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dto.PartnerProductDto;

import java.util.List;

/**
 * 产品路由服务，通过路由ID，支付单信息，SP信息,终端信息来查询合适的渠道信息，返回一个渠道ID
 */
public interface PartnerProductService {

    /**
     * 获取商户产品信息
     *
     * @param partnerProductDto
     * @return
     */
    SingleResult<PartnerProductDto> getPartnersProduct(PartnerProductDto partnerProductDto);

    /**
     * @Description: 无分页查询商户产品列表
     * @returns:
     */
    SingleResult<List<PartnerProductDto>> getPartnerProductList(PartnerProductDto partnerProductDto);

}
