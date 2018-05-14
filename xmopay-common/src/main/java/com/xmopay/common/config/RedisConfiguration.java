package com.xmopay.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * com.xmopay.common.config
 *
 * @author echo_coco.
 * @date 11:17 AM, 2018/4/26
 */
@Configuration
@ConditionalOnExpression("${redis.enable:false}")
public class RedisConfiguration {
    @Value("${redis.host}")
    private String host;  // Redis服务器地址

    @Value("${redis.port}")
    private int port;  // Redis服务器连接端口

    @Value("${redis.password}")
    private String password;  // Redis服务器连接密码（默认为空）

    @Value("${redis.timeout}")
    private int timeout;  // 连接超时时间（毫秒）

    @Value("${redis.database}")
    private int database;  // 连接超时时间（毫秒）

    @Value("${redis.pool.max-active}")
    private int maxTotal;  // 连接池最大连接数（使用负值表示没有限制）

    @Value("${redis.pool.max-wait}")
    private int maxWaitMillis;  // 连接池最大阻塞等待时间（使用负值表示没有限制）

    @Value("${redis.pool.max-idle}")
    private int maxIdle;  // 连接池中的最大空闲连接

    @Value("${redis.pool.min-idle}")
    private int minIdle;  // 连接池中的最小空闲连接

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig());
        return jedisConnectionFactory;
    }

    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(this.maxWaitMillis);  // 连接池最大阻塞等待时间（使用负值表示没有限制）
        jedisPoolConfig.setMaxTotal(this.maxTotal);  //  连接池最大连接数（使用负值表示没有限制）
        jedisPoolConfig.setMaxIdle(this.maxIdle);  // 连接池中的最大空闲连接
        jedisPoolConfig.setMinIdle(this.minIdle);  // 连接池中的最小空闲连接
        return jedisPoolConfig;
    }

    @Bean
    public StringRedisTemplate redisTemplate() {
        return new StringRedisTemplate(jedisConnectionFactory());
    }
}
