package com.xmopay.admincp.common;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisCacheUtils {

    private static StringRedisTemplate redisTemplate;

    /**
     * 缓存value操作
     * @param key
     * @param v
     * @param time 毫秒
     * @return
     */
    public static boolean cacheValue(String key, String v, long time) {
        try {
            ValueOperations<String, String> valueOps =  redisTemplate.opsForValue();
            valueOps.set(key, v);
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
            }
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    /**
     * 缓存value操作
     * @param k
     * @param v
     * @return
     */
    public static boolean cacheValue(String k, String v) {
        return cacheValue(k, v, -1);
    }

    /**
     * 判断缓存是否存在
     * @param k
     * @return
     */
    public static boolean containsValueKey(String k) {
        return containsKey(k);
    }

    /**
     * 判断缓存是否存在
     * @param k
     * @return
     */
    public static boolean containsSetKey(String k) {
        return containsKey(k);
    }

    /**
     * 判断缓存是否存在
     * @param k
     * @return
     */
    public static boolean containsListKey(String k) {
        return containsKey(k);
    }

    public static boolean containsKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Throwable t) {
          t.printStackTrace();
        }
        return false;
    }

    /**
     * 获取缓存
     * @param k
     * @return
     */
    public static String getValue(String k) {
        try {
            ValueOperations<String, String> valueOps =  redisTemplate.opsForValue();
            return valueOps.get(k);
        } catch (Throwable t) {
           t.printStackTrace();
        }
        return null;
    }

    /**
     * 移除缓存
     * @param k
     * @return
     */
    public static boolean removeValue(String k) throws Exception {
        return remove(k);
    }

    public static boolean removeSet(String k) throws Exception {
        return remove(k);
    }

    public static boolean removeList(String k) throws Exception {
        return remove(k);
    }

    /**
     * 移除缓存
     * @param key
     * @return
     */
    public static boolean remove(String key) throws Exception {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("redis删除失败", t);
        }
    }
    /**
     * 缓存set操作
     * @param key
     * @param v
     * @param time
     * @return
     */
    public static boolean cacheSet(String key, String v, long time) {
        try {
            SetOperations<String, String> valueOps =  redisTemplate.opsForSet();
            valueOps.add(key, v);
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
            }
            return true;
        } catch (Throwable t) {
           t.printStackTrace();
        }
        return false;
    }

    /**
     * 缓存set
     * @param k
     * @param v
     * @return
     */
    public static boolean cacheSet(String k, String v) {
        return cacheSet(k, v, -1);
    }

    /**
     * 缓存set
     * @param key
     * @param v
     * @param time
     * @return
     */
    public static boolean cacheSet(String key, Set<String> v, long time) {
        try {
            SetOperations<String, String> setOps =  redisTemplate.opsForSet();
            setOps.add(key, v.toArray(new String[v.size()]));
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
            }
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    /**
     * 缓存set
     * @param k
     * @param v
     * @return
     */
    public static boolean cacheSet(String k, Set<String> v) {
        return cacheSet(k, v, -1);
    }

    /**
     * 获取缓存set数据
     * @param k
     * @return
     */
    public static Set<String> getSet(String k) {
        try {
            SetOperations<String, String> setOps = redisTemplate.opsForSet();
            return setOps.members( k);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * list缓存
     * @param key
     * @param v
     * @param time
     * @return
     */
    public  static boolean cacheList(String key, String v, long time) {
        try {
            ListOperations<String, String> listOps =  redisTemplate.opsForList();
            listOps.rightPush(key, v);
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
            }
            return true;
        } catch (Throwable t) {
           t.printStackTrace();
        }
        return false;
    }

    /**
     * 缓存list
     * @param k
     * @param v
     * @return
     */
    public  static boolean cacheList(String k, String v) {
        return cacheList(k, v, -1);
    }

    /**
     * 缓存list
     * @param key
     * @param v
     * @param time 毫秒
     * @return
     */
    public  static boolean cacheList(String key, List<String> v, long time) {
        try {
            ListOperations<String, String> listOps =  redisTemplate.opsForList();
            long l = listOps.rightPushAll(key, v);
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
            }
            return true;
        } catch (Throwable t) {
          t.printStackTrace();
        }
        return false;
    }

    /**
     * 缓存list
     * @param k
     * @param v
     * @return
     */
    public static boolean cacheList(String k, List<String> v) {
        return cacheList(k, v, -1);
    }

    /**
     * 获取list缓存
     * @param k
     * @param start
     * @param end
     * @return
     */
    public static List<String> getList(String k, long start, long end) {
        try {
            ListOperations<String, String> listOps =  redisTemplate.opsForList();
            return listOps.range(k, start, end);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 获取总条数, 可用于分页
     * @param k
     * @return
     */
    public static long getListSize(String k) {
        try {
            ListOperations<String, String> listOps =  redisTemplate.opsForList();
            return listOps.size( k);
        } catch (Throwable t) {
           t.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取总条数, 可用于分页
     * @param listOps
     * @param k
     * @return
     */
    public static long getListSize(ListOperations<String, String> listOps, String k) {
        try {
            return listOps.size( k);
        } catch (Throwable t) {
           t.printStackTrace();
        }
        return 0;
    }

    /**
     * 移除list缓存
     * @param k
     * @return
     */
    public static boolean removeOneOfList(String k) {
        try {
            ListOperations<String, String> listOps =  redisTemplate.opsForList();
            listOps.rightPop( k);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }
}
