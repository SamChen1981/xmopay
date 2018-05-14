package com.xmopay.funds.dao;

import java.util.Map;

/**
 * com.xmopay.funds.dao
 *
 * @author echo_coco.
 * @date 2:59 PM, 2018/5/10
 */
public interface ChannelDao {

    Map selectChannelByIdLock(String channelId);

    int updateChannelById(String channelId);
}
