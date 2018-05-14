package com.xmopay.admincp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PartnerAccountDto {
    private String partnerId;

    private BigDecimal balance;

    private BigDecimal freezeBalance;

    private String hashCode;

    private Date lastTrade;

    private String lastSign;
}
