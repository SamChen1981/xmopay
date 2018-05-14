package com.xmopay.openapi.core;

import com.xmopay.common.model.CallbackRequestModel;
import com.xmopay.common.model.PayRequestModel;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * com.xmopay.openapi.core
 *
 * @author echo_coco.
 * @date 9:57 PM, 2018/5/7
 */
@FeignClient(name = "xmopay-channel")
@RibbonClient(name="xmopay-channel")
public interface ChannelRemote {

    @RequestMapping(value = "channel/pay", method = RequestMethod.POST, consumes = "application/json")
    String doPayment(@RequestBody PayRequestModel payRequestModel);

    @RequestMapping(value = "channel/callback/asyncPayment", method = RequestMethod.POST)
    String doPaymentAsyncCallback(@RequestBody CallbackRequestModel callbackRequestModel);

}
