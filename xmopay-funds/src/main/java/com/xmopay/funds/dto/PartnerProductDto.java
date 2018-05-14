package com.xmopay.funds.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mimi on 2018/05/08
 */
@Data
public class PartnerProductDto implements Serializable {
    private static final long serialVersionUID = 4213731850393942754L;

    private Integer ppid;

    private String partnerId;

    private String partnerName;

    private String productType;

    private String channelCode;

    private String channelName;

    private String bankName;

    private String bankCode;

    private BigDecimal rate;

    private Date updateTime;

    private Date createTime;

    private Integer status;

}