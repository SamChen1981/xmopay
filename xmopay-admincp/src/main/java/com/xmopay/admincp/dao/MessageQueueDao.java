package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.MessageQueueDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageQueueDao {
    Integer insertMessageQueue(MessageQueueDto messageQueue);

    List<MessageQueueDto> getMessageQueueList(MessageQueueDto messageQueueDto);

    Integer deleteMessageQueue(MessageQueueDto messageQueueDto);

    Integer updateQueue(MessageQueueDto messageQueueDto);
}
