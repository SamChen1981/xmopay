package com.xmopay.openapi;

import com.xmopay.openapi.config.LocalRibbonClientConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * com.xmopay.openapi
 *
 * @author echo_coco.
 * @date 2:03 PM, 2018/5/4
 */
@EnableFeignClients(basePackages = "com.xmopay.openapi.core")
@RibbonClient(name = "xmopay-channel", configuration = LocalRibbonClientConfiguration.class)
@SpringBootApplication
@ComponentScan(basePackages = {"com.xmopay"})
@MapperScan(basePackages = {"com.xmopay.openapi.dao"})
public class XmoPayOpenApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmoPayOpenApiApplication.class, args);
    }
}

