package com.xmopay.funds.service.impl;

import com.xmopay.funds.entity.MessageEntity;
import com.xmopay.funds.service.IRefundServiceI;
import org.springframework.stereotype.Service;

/**
 * com.xmopay.funds.service.impl
 *
 * @author echo_coco.
 * @date 10:03 AM, 2018/4/27
 */
@Service("tradeRefundServiceImpl")
public class RefundServiceImpl implements IRefundServiceI {

    @Override
    public void execute(MessageEntity entity) {
        doRefund(entity);
    }

    private void doRefund(MessageEntity entity) {
        System.out.println("执行交易退款...");
    }
}
