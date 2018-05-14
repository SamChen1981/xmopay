package com.xmopay.openapi.service;

import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dto.MessageQueueDto;

/**
 * Created by monica on 15/04/2018.
 */
public interface MessageQueueService {

    SingleResult<Integer> insertMessageQueue(MessageQueueDto messageQueueDto);

}
