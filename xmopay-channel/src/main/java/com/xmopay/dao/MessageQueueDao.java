package com.xmopay.dao;

import com.xmopay.dto.MessageQueueDto;

/**
 * com.xmopay.dao
 *
 * @author echo_coco.
 * @date 3:56 PM, 2018/4/28
 */
public interface MessageQueueDao {

    int insertMessageQueue(MessageQueueDto messageQueueDto);
}
