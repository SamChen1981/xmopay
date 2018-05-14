package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.MessageQueueDao;
import com.xmopay.admincp.dto.MessageQueueDto;
import com.xmopay.admincp.service.MessageQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageQueueServiceImpl implements MessageQueueService {

    @Autowired
    private MessageQueueDao messageQueueDao;


    @Override
    public SingleResult<PageInfo> getMessageQueueList(MessageQueueDto messageQueueDto) {
        SingleResult<PageInfo> result = new SingleResult<>(false, null);
        PageHelper.startPage(messageQueueDto.getCurrentPage(), messageQueueDto.getPageSize());
        List<MessageQueueDto> lists = messageQueueDao.getMessageQueueList(messageQueueDto);
        PageInfo<MessageQueueDto> pageInfo = new PageInfo<>(lists);
        if (null != lists && lists.size() > 0) {
            result.setResult(pageInfo);
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    public SingleResult<Integer> deleteMessageQueueByMqid(MessageQueueDto messageQueueDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try {
            int n = messageQueueDao.deleteMessageQueue(messageQueueDto);
            if (n > 0) {
                result.setResult(n);
                result.setSuccess(true);
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public SingleResult<Integer> updateQueueByMqid(MessageQueueDto messageQueueDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try {
            int n = messageQueueDao.updateQueue(messageQueueDto);
            if (n > 0) {
                result.setResult(1);
                result.setSuccess(true);
            }
        } catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }
}
