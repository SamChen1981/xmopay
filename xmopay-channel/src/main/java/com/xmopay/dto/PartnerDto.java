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
public class PartnerDto implements Serializable {
    private static final long serialVersionUID = 6589439292663183795L;

    private Integer ptid;

    private String partnerType;

    private String partnerId;

    private String partnerName;

    private String partnerMd5Key;

    private String partnerAesKey;

    private String xmopayPrivateKey;

    private String xmopayPublicKey;

    private String partnerPublicKey;

    private Date createTime;

    private Date updateTime;

    private Integer apiStatus;

    private Integer status;

    private String partnerInfo;



}
