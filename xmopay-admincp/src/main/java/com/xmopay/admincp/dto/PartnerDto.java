package com.xmopay.admincp.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PartnerDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 3439967482315317800L;
    private Integer ptid;

    private String partnerId;

    private String partnerName;

    private String partnerInfo;

    private String xmopayPrivateKey;

    private String xmopayPublicKey;

    private String partnerPublicKey;

    private String partnerMd5Key;

    private String partnerAesKey;

    private Date createTime;

    private Date updateTime;

    private String apiWhiteIp;

    private Integer apiStatus;

    private Integer status;

    /** 账务表 **/
    private BigDecimal balance;

    private BigDecimal freezeBalance;

    private String hashCode;

    private Date lastTrade;

    private String lastSign;

}
