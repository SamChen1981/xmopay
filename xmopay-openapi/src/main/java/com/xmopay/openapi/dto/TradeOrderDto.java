package com.xmopay.openapi.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mimi on 2018/05/08
 */
@Data
public class TradeOrderDto implements Serializable {

    private static final long serialVersionUID = -1134936901903325580L;

    private Integer toid;

    private Integer tranType;

    private String orderSn;

    private String billingSn;

    private String orderHash;

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

    private String orderSign;
}
