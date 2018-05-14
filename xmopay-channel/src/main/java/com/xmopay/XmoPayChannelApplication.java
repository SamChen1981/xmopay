package com.xmopay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * com.xmopay
 *
 * @author echo_coco.
 * @date 5:51 PM, 2018/4/27
 */

@SpringBootApplication(scanBasePackages = {"com.xmopay"})
@MapperScan(basePackages = {"com.xmopay.dao"})
@EnableEurekaClient
public class XmoPayChannelApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmoPayChannelApplication.class, args);
    }
}
