package com.xmopay.service.impl;

import com.xmopay.dao.MessageQueueDao;
import com.xmopay.dto.MessageQueueDto;
import com.xmopay.service.IMessageQueueService;
import com.xmopay.vo.VoResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * com.xmopay.service.impl
 *
 * @author echo_coco.
 * @date 11:47 AM, 2018/5/3
 */
@Service
public class MessageQueueServiceImpl implements IMessageQueueService {

    @Autowired
    private MessageQueueDao messageQueueDao;

    @Override
    public VoResult<Integer> insertMessageQueue(MessageQueueDto messageQueueDto) {
        VoResult<Integer> result = new VoResult<>("10000", false, "", null);
        try {
            int line = messageQueueDao.insertMessageQueue(messageQueueDto);
            if (line > 0) {
                result.setCode("0000");
                result.setData(line);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setCode("10000");
            result.setErrMessage(e.getMessage());
        }
        return result;
    }
}
