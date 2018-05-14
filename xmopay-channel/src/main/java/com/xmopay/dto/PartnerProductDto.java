package com.xmopay.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author echo_coco
 * @since
 */
@Data
public class PartnerProductDto implements Serializable {
    private static final long serialVersionUID = 4213731850393942754L;

    private Integer ppId;

    private String partnerId;

    private String partnerName;

    private String productType;

    private String channelCode;

    private String channelName;

    private Date updateTime;

    private Date createTime;

    private Integer status;

    private BigDecimal rate;

    private String bankCode;

    private String bankName;

}