package com.xmopay.service.impl;

import com.xmopay.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * com.xmopay.service.impl
 *
 * @author echo_coco.
 * @date 12:02 PM, 2018/5/3
 */
@Service
public class RedisServiceImpl implements IRedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置String类型值
     * @param key
     * @param value
     * @param expire
     * @param unit
     */
    @Override
    public void setValue(String key, String value, long expire, TimeUnit unit) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key, value, expire, unit);
    }

    /**
     * 删除key-value
     * @param key
     * @return
     */
    @Override
    public boolean del(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * key获取String value
      * @param key
     * @return
     */
    @Override
    public String getValue(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }
}
