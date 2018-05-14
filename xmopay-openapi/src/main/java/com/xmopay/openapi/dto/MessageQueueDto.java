package com.xmopay.openapi.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageQueueDto implements Serializable {
    private static final long serialVersionUID = -6875017019361889193L;

    private Integer mqid;

    private String partnerId;

    private String orderSn;

    private String messageTopic;

    private String messageHost;

    private Integer notifyCount;

    private Integer threadStatus;

    private Integer consumerStatus;

    private Date dateline;

    private String messageBody;

    //以下不属于数据表字段

    private String startTime;

    private String endTime;

    private Integer pageSize;

    private Integer pageIndex;
}