package com.xmopay.channel;

import com.xmopay.channel.payment.IPaymentCallbackService;
import com.xmopay.channel.payment.IPaymentQueryTradeService;
import com.xmopay.channel.payment.IPaymentService;
import com.xmopay.common.model.CallbackRequestModel;
import com.xmopay.common.model.QueryOrderRequestModel;
import com.xmopay.vo.ChannelResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * com.xmopay.channel
 *
 * @author echo_coco.
 * @date 9:14 PM, 2018/4/27
 */
@Component
public abstract class AbstractBaseChannelService implements IPaymentService, IPaymentCallbackService, IPaymentQueryTradeService, BaseChannelService {

    protected String BASE_CALLBACK_API_NAME = "/notify/async/";

    @Autowired
    ConfigurableApplicationContext context;

    @Override
    public ChannelResponseResult<Map> queryOrder(QueryOrderRequestModel queryOrderRequestModel) {
        return null;
    }

    @Override
    public ChannelResponseResult<String> doPaymentAsyncCallback(CallbackRequestModel callbackRequestModel) {
        return null;
    }

    @Override
    public ChannelResponseResult<Map> doPaymentRsyncCallback(CallbackRequestModel callbackRequestModel) {
        return null;
    }

    @Override
    public abstract BaseChannelService getBean(Class clazz);

    @Override
    public abstract BaseChannelService getBean(String className);

    protected String getAsynApiName() {
        return BASE_CALLBACK_API_NAME + buildAsyncUrl();
    }

    protected abstract String buildAsyncUrl();
}
