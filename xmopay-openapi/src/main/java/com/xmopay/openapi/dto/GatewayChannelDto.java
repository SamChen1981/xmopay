package com.xmopay.openapi.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mimi on 2018/05/08
 */
@Data
public class GatewayChannelDto implements Serializable {
    private static final long serialVersionUID = -7620396369569295222L;
    private Integer channelId;

    private Integer agencyId;

    private Integer channelType;

    private String channelCode;

    private String channelName;

    private String channelKey;

    private Date updateTime;

    private Date dateline;

    private Integer status;

    private String channelSecret;

    private String channelParams;

    private GatewayAgencyDto gatewayAgencyDto;


}