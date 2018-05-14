package com.xmopay.funds.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * com.xmopay.funds.entity
 *
 * @author echo_coco.
 * @date 2:46 PM, 2018/4/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    private Integer mqId;

    private Integer notifyCount;

    private String orderSn;

    private String partnerId;

    private String msgBody;

    private String msgTopic;

    private String dateline;

    private String messageHost;
}
