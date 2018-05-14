package com.xmopay.openapi.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by mimi on 2018/05/08
 */
@Data
public class GatewayAgencyDto implements Serializable {
    private static final long serialVersionUID = 5677182361880596542L;

    private Integer gaid;

    private String agencyCode;

    private String agencyName;

    private Integer agencyStatus;

    private Date updateTime;

    private Date dateline;

    private String agencyParams;

    private Integer pageIndex;

    private Integer pageSize;

    private Integer firstPage;

    private List<GatewayChannelDto> gatewayChannelDtos;
}