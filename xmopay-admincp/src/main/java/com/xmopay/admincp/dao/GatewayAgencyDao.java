package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.GatewayAgencyDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GatewayAgencyDao {

    List<GatewayAgencyDto> findGatewayAgencyList(GatewayAgencyDto gatewayAgencyDto);

    List<GatewayAgencyDto> getGatewayAgencyList(GatewayAgencyDto dto);

    GatewayAgencyDto getGatewayAgencyById(int gaid);

    Integer insertGatewayAgency(GatewayAgencyDto dto);

    Integer updateGatewayAgency(GatewayAgencyDto dto);
}
