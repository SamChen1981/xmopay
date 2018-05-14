package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.PartnerProductDto;

public interface PartnerProductService {
    SingleResult<PageInfo> getPartnerProductPageList(PartnerProductDto partnerProductDto);

    SingleResult<PartnerProductDto> getPartnersProduct(PartnerProductDto partnerProductDto);

    SingleResult<Integer> insertPartnerProduct(PartnerProductDto partnerProductDto);

    SingleResult<Integer> updatePartnerProduct(PartnerProductDto partnerProductDto);
}
