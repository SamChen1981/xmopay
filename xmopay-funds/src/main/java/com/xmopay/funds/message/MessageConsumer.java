package com.xmopay.funds.message;

import com.xmopay.funds.entity.MessageEntity;

/**
 * com.xmopay.funds.message
 *
 * @author echo_coco.
 * @date 10:39 PM, 2018/4/26
 */
public interface MessageConsumer {

    void consumer(MessageEntity messageEntity);
}
