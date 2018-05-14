package com.xmopay.admincp.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MessageQueueDto extends BaseDto {

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

}
