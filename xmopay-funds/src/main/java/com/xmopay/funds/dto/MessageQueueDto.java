package com.xmopay.funds.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * com.xmopay.funds.dto
 *
 * @author echo_coco.
 * @date 4:11 PM, 2018/4/27
 */
@Data
public class MessageQueueDto implements Serializable {
    private static final long serialVersionUID = 1296927611380976429L;

    private Integer mqId;

    private String partnerId;

    private String orderSn;

    private String messageTopic;

    private String messageBody;

    private String messageHost;

    private Integer threadStatus;

    private Integer consumerStatus;

}
