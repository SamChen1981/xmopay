package com.xmopay.admincp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TradeOrderDto extends BaseDto {

    private Integer toid;

    private Integer tranType;

    private String orderSn;

    private String orderHash;

    private String billingSn;

    private String partnerId;

    private String partnerName;

    private String payType;

    private String channelCode;

    private String bankCode;

    private String orderTitle;

    private BigDecimal orderAmount;

    private Date orderTime;

    private Date tradeTime;

    private Date finishTime;

    private String tradeIp;

    private String tradeHash;

    private String notifyUrl;

    private String returnUrl;

    private String extendParam;

    private String outTradeNo;

    private Date outTradeTime;

    private String signType;

    private String version;

    private String remark;

    private Integer orderStatus;

    //以下不是数据表字段

    private String minPayMoney;

    private String maxPayMoney;

}
