package com.xmopay.openapi.service.impl;

import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dao.MessageQueueDao;
import com.xmopay.openapi.dto.MessageQueueDto;
import com.xmopay.openapi.service.MessageQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by monica on 2/05/2018.
 */
@Service("messageQueueService")
public class MessageQueueServiceImpl implements MessageQueueService {

    @Autowired
    private MessageQueueDao messageQueueDao;


    @Override
    public SingleResult<Integer> insertMessageQueue(MessageQueueDto messageQueueDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try {
            int line = messageQueueDao.insertMessageQueue(messageQueueDto);
            if (line > 0) {
                result.setResult(line);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }

}
