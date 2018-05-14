package com.xmopay.admincp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PartnerProductDto extends BaseDto {

    private Integer ppid;

    private String partnerId;

    private String partnerName;

    private String productType;

    private String channelCode;

    private String channelName;

    private BigDecimal rate;

    private String bankCode;

    private String bankName;

    private Date updateTime;

    private Date createTime;

    private Integer status;

}
