package com.xmopay.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author echo_coco
 * @since
 */
@Data
public class MessageQueueDto implements Serializable {
    private static final long serialVersionUID = -6875017019361889193L;

    private Integer mqId;

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