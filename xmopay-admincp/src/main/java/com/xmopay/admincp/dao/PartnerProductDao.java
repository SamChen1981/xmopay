package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.PartnerProductDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartnerProductDao {
    List<PartnerProductDto> getPartnerProductList(PartnerProductDto partnerProductDto);

    PartnerProductDto getPartnerProduct(PartnerProductDto partnerProductDto);

    Integer insertPartnerProduct(PartnerProductDto partnerProductDto);

    Integer updatePartnerProduct(PartnerProductDto partnerProductDto);
}
