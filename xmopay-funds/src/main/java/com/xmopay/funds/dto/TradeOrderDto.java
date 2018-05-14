package com.xmopay.funds.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * com.xmopay.funds.dto
 *
 * @author echo_coco.
 * @date 11:36 AM, 2018/4/27
 */
@Data
public class TradeOrderDto implements Serializable {
    private static final long serialVersionUID = 6794100427681629145L;

    private Integer toId;

    private Integer tranType;

    private String orderSn;

    private String billingSn;

    private String partnerId;

    private Integer orderStatus;

    private String orderAmount;

    private String partnerName;

    private String payType;

    private String bankCode;

    private String notifyUrl;

    private String orderTime;

    private String extendParam;

    private String tradeIp;

    private String tradeHash;

    private String finishTime;

    private String signType;

    private String channelCode;

    private String tradeTime;

    private String outTradeNo;

}

