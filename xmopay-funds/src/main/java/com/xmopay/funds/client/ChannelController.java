package com.xmopay.funds.client;

import com.xmopay.funds.service.IChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * com.xmopay.funds.client
 *
 * @author echo_coco.
 * @date 3:08 PM, 2018/5/10
 */
@RestController
@RequestMapping(value = "channel")
public class ChannelController {

    @Autowired
    private IChannelService iChannelService;

    @RequestMapping(value = "update")
    public String updateChannel() {
        int result = iChannelService.updateChannelByIdLock("65");
        return (result > 0 ? "success" : "fail");
    }
}
