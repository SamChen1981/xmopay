package com.xmopay.openapi.dao;

import com.xmopay.openapi.dto.PartnerDto;

import java.util.List;

/**
 * Created by mimi on 2018/05/08
 */
public interface PartnerDao {

    /**
     * 商户信息
     *
     * @param partnerDto
     * @return
     */
    PartnerDto getPartnerInfo(PartnerDto partnerDto);

    /**
     * 商户账户信息
     *
     * @param partnerDto
     * @return
     */
    PartnerDto getPartnerAccount(PartnerDto partnerDto);

    /**
     * 商户账户信息
     *
     * @param partnerDto
     * @return
     */
    List<PartnerDto> getPartnerAccountList(PartnerDto partnerDto);
}