package com.xmopay.service;

import java.util.concurrent.TimeUnit;

/**
 * com.xmopay.service
 *
 * @author echo_coco.
 * @date 12:02 PM, 2018/5/3
 */
public interface IRedisService {

    void setValue(String key, String value, long expire, TimeUnit unit);

    boolean del(String key);

    String getValue(String key);
}
