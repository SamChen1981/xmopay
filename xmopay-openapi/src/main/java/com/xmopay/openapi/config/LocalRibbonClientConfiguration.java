package com.xmopay.openapi.config;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * com.xmopay.openapi.config
 *
 * @author echo_coco.
 * @date 4:24 PM, 2018/5/9
 */
@Configuration
public class LocalRibbonClientConfiguration {
    @Bean
    public ServerList<Server> ribbonServerList() {
        // return new ConfigurationBasedServerList();
//        StaticServerList<Server> staticServerList = new StaticServerList<>((new Server("localhost", 8001)),
//                new Server("localhost", 8000));

        StaticServerList<Server> staticServerList = new StaticServerList<>((new Server("localhost", 9992)));
        return staticServerList;
    }
}
