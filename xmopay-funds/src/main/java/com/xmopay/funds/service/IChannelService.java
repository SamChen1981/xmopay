package com.xmopay.funds.service;

import java.util.Map;

/**
 * com.xmopay.funds.service
 *
 * @author echo_coco.
 * @date 2:58 PM, 2018/5/10
 */
public interface IChannelService {

    int updateChannelByIdLock(String channelId);


    Map selectChannelByIdLock(String channelId);

}
