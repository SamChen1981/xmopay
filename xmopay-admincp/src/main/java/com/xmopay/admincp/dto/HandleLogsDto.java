package com.xmopay.admincp.dto;

import lombok.Data;

import java.util.Date;

@Data
public class HandleLogsDto extends BaseDto {

    private Integer hlid;

    private String puserid;

    private String partnerId;

    private String handleType;

    private String handleCode;

    private String orderSn;

    private String handleEvents;

    private Integer handleStatus;

    private String handleIp;

    private Date dateline;

    private String handleParams;

    private String ipInfo;

}
