package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.PartnerDto;

import java.util.List;


public interface PartnerService {

    SingleResult<Integer> insertPartner(PartnerDto partnerDto);

    SingleResult<PageInfo> getPartnerPageList(PartnerDto partnerDto);

    SingleResult<List<PartnerDto>> getPartnersList(PartnerDto partnerDto);

    SingleResult<PartnerDto> getPartnerInfo(PartnerDto partnerDto);

    SingleResult<Integer> updatePartner(PartnerDto partnerDto);
}
