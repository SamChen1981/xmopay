package com.xmopay.admincp.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GatewayAgencyDto extends BaseDto {

    private Integer gaid;

    private String agencyCode;

    private String agencyName;

    private Integer agencyStatus;

    private Date updateTime;

    private Date dateline;

    private String agencyParams;

    private List<GatewayChannelDto> gatewayChannelDtos;

}
