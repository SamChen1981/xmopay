package com.xmopay.admincp.dto;

import lombok.Data;

import java.util.Date;

@Data
public class GatewayChannelDto extends BaseDto {
    private Integer channelId;

    private Integer agencyId;

    private Integer channelType;

    private String channelCode;

    private String channelName;

    private String channelKey;

    private String channelSecret;

    private String channelBalance;

    private String channelParams;

    private Date updateTime;

    private Date dateline;

    private Integer status;

    private GatewayAgencyDto gatewayAgencyDto;

}
