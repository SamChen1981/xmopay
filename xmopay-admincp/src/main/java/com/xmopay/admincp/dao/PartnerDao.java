package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.PartnerDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartnerDao {

    /**
     * 商户记录列表
     * @param partnerDto
     * @return
     */
    List<PartnerDto> getListAllPartners(PartnerDto partnerDto);


    Integer insertPartner(PartnerDto partnerDto);

    Integer insertPartnerAccount(PartnerDto partnerDto);

    List<PartnerDto> getPartnersList(PartnerDto partnerDto);

    PartnerDto getPartnerInfo(PartnerDto partnerDto);

    Integer updatePartner(PartnerDto partnerDto);
}
