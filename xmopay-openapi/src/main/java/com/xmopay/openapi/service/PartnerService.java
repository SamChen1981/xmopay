package com.xmopay.openapi.service;

import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dto.PartnerDto;

public interface PartnerService {

    /**
     * 获取商户信息
     *
     * @param partnerDto
     * @return
     */
    SingleResult<PartnerDto> getPartnerInfo(PartnerDto partnerDto);

    /**
     * 获取商户账户信息
     *
     * @param partnerDto
     * @return
     */
    SingleResult<PartnerDto> getPartnerAccount(PartnerDto partnerDto);


    /**
     * 获取商户账户列表多币种
     *
     * @param partnerDto
     * @return
     */
    SingleResult<String> getPartnerAccountList(PartnerDto partnerDto);

}
