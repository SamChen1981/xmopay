package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.MessageQueueDto;

public interface MessageQueueService {
    SingleResult<PageInfo> getMessageQueueList(MessageQueueDto messageQueueDto);

    SingleResult<Integer> deleteMessageQueueByMqid(MessageQueueDto messageQueueDto);

    SingleResult<Integer> updateQueueByMqid(MessageQueueDto messageQueueDto);
}
