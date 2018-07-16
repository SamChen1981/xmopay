package com.xmopay.funds.service.impl;

import com.xmopay.funds.dao.ChannelDao;
import com.xmopay.funds.exception.FundsException;
import com.xmopay.funds.service.IChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * com.xmopay.funds.service.impl
 *
 * @author echo_coco.
 * @date 2:59 PM, 2018/5/10
 */
@Service
public class ChannelServiceImpl implements IChannelService {

    @Autowired
    private ChannelDao channelDao;

    @Autowired
    private PlatformTransactionManager txManager;

    @Override
    public int updateChannelByIdLock(String channelId) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // 设置事务隔离级别，开启新事务
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus transactionStatus = txManager.getTransaction(def);
        int result = 0;
        try {
            System.out.println("pre...");
            Map channel = selectChannelByIdLock(channelId);
            if (channel == null) {
                throw new FundsException("渠道ID:" + channelId + "不存在.");
            }
            result = channelDao.updateChannelById(channelId);
            System.out.println("update...");
            TimeUnit.SECONDS.sleep(30);
            txManager.commit(transactionStatus);
            System.out.println("commit...");
        } catch (Exception e) {
            txManager.rollback(transactionStatus);
            result = -1;
        }
        return result;
    }

    @Override
    public Map selectChannelByIdLock(String channelId) {
        return channelDao.selectChannelByIdLock(channelId);
    }
}
