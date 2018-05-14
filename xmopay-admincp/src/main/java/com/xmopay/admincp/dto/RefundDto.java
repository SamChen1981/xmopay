package com.xmopay.admincp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class RefundDto extends BaseDto {

    private Integer toid;

    private Integer refundType;

    private String orderSn;

    private String billingSn;

    private String partnerId;

    private String partnerName;

    private BigDecimal orderAmount;

    private Date updateTime;

    private Date dateline;

    private String outTradeNo;

    private String remark;

    private Integer refundStatus;

    /*以下非数据表字段*/

    private String minPayMoney;

    private String maxPayMoney;
}
