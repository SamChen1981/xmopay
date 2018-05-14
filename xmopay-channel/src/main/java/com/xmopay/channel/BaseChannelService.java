package com.xmopay.channel;

/**
 * com.xmopay.channel
 *
 * @author echo_coco.
 * @date 6:14 PM, 2018/4/27
 */
public interface BaseChannelService {

    BaseChannelService getBean(Class clazz);

    BaseChannelService getBean(String className);
}
