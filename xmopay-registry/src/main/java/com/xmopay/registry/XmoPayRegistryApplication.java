package com.xmopay.registry;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * com.xmopay.registry
 *
 * @author echo_coco.
 * @date 9:55 AM, 2018/5/14
 */
@EnableEurekaServer
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class XmoPayRegistryApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XmoPayRegistryApplication.class).web(WebApplicationType.SERVLET).run(args);
    }

}
