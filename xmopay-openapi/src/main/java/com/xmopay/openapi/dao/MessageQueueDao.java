package com.xmopay.openapi.dao;

import com.xmopay.openapi.dto.MessageQueueDto;

/**
 * Created by mimi on 2018/05/08
 */
public interface MessageQueueDao {

    int insertMessageQueue(MessageQueueDto messageQueueDto);
}
