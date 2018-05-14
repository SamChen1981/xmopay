package com.xmopay.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author echo_coco.
 * @since
 */
@Data
public class PartnerAccountDto implements Serializable {
    private static final long serialVersionUID = 6501819918071371512L;

    private String partnerId;

    private BigDecimal balance;

    private BigDecimal freezeBalance;

    private String hashCode;

    private Date lastTrade;

    private String lastSign;
}