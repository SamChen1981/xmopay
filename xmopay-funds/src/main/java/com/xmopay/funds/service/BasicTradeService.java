package com.xmopay.funds.service;

import com.xmopay.funds.entity.MessageEntity;

/**
 * com.xmopay.funds.service
 *
 * @author echo_coco.
 * @date 9:43 AM, 2018/4/27
 */
public interface BasicTradeService {

    void execute(MessageEntity entity);
}
