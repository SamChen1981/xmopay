package com.xmopay.funds.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * com.xmopay.funds.dto
 *
 * @author echo_coco.
 * @date 3:41 PM, 2018/4/27
 */
@Data
public class BillingsDto implements Serializable {

    private static final long serialVersionUID = 5610350130029575471L;

    private Integer pid;

    private String orderSn;

    private String billingSn;

    private Integer billType;

    private String partnerId;

    private String partnerName;

    private String payType;

    private String bankCode;

    private String tradeAmount;

    private String tradeFee;

    private String payment;

    private String accountAmount;

    private String remark;

    private String tradeTime;

    private Integer payStatus;


}

