package com.xmopay.admincp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * com.xmopay
 *
 * @author monica.
 * @date 8:58 PM, 2018/4/25
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.xmopay")
@ServletComponentScan
public class XmoPayAdmincpApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmoPayAdmincpApplication.class, args);
    }
}
