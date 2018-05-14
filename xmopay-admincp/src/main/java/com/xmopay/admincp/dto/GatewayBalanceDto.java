package com.xmopay.admincp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GatewayBalanceDto extends BaseDto {

    private Integer gaid;

    private Integer gatewayType;

    private String gatewayCode;

    private String gatewayName;

    private BigDecimal balance;

    private Date dateline;

    private String merViralAcct;

    private String batchId;

    private Date lastTradeTime;
}
