package com.xmopay.funds.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * com.xmopay.funds.config
 *
 * @author echo_coco.
 * @date 9:59 AM, 2018/4/27
 */
@Component
@ConfigurationProperties(prefix = "message")
public class YmlConfig {

    private Map<String, String> topics = new HashMap<>(8);

    public void setTopics(Map<String, String> topics) {
        this.topics = topics;
    }

    public Map<String, String> getTopics() {
        return topics;
    }
}
