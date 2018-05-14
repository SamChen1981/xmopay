package com.xmopay.admincp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BillingsDto extends BaseDto {
    private Long bid;

    private String orderSn;

    private String billingSn;

    private Integer billType;

    private String partnerId;

    private String partnerName;

    private BigDecimal tradeAmount;

    private BigDecimal tradeFee;

    private BigDecimal payment;

    private BigDecimal accountAmount;

    private String remark;

    private Date tradeTime;

    private Integer payStatus;

    // 支付类型
    private String payType;

    // 支付代码
    private  String bankCode;

    //以下为非数据表字段

    private String minPayMoney;

    private String maxPayMoney;

    private String sortString;

}
