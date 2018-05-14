package com.xmopay.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author echo_coco.
 * @since
 */
@Data
public class GatewayAgencyDto implements Serializable {
    private static final long serialVersionUID = 5677182361880596542L;

    private Integer gaId;

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