package com.xmopay.openapi.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * com.xmopay.openapi.controller
 *
 * @author echo_coco.
 * @date 9:35 PM, 2018/5/7
 */
@RestController
@RequestMapping(value = "return")
public class ReturnController {

    @Autowired
    private EurekaClient eurekaClient;

    @RequestMapping(value = "info")
    public String getInfo() {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("xmopay-channel", false);

        return instanceInfo.getAppName() + ", "
                + instanceInfo.getHostName() + ", "
                + instanceInfo.getInstanceId();
    }
}
