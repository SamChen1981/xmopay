package com.xmopay.service;

import com.xmopay.dto.MessageQueueDto;
import com.xmopay.vo.VoResult;

/**
 * com.xmopay.service
 *
 * @author echo_coco.
 * @date 11:46 AM, 2018/5/3
 */
public interface IMessageQueueService {

    VoResult<Integer> insertMessageQueue(MessageQueueDto messageQueueDto);
}
