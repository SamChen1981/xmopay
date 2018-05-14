package com.xmopay.openapi.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mimi on 2018/05/08
 */
@Data
public class PartnerDto implements Serializable {
    private static final long serialVersionUID = 6589439292663183795L;

    private Integer ptid;

    private String partnerId;

    private String partnerName;

    private String partnerMd5Key;

    private String partnerAesKey;

    private Date createTime;

    private Date updateTime;

    private Integer apiStatus;

    private Integer status;

    private String apiWhiteIp;

    private String partnerInfo;

    private String xmoPayPrivateKey;

    private String xmoPayPublicKey;

    private String partnerPublicKey;

}