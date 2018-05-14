package com.xmopay.common.config;

import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * com.xmopay.config
 *
 * @author monica.
 * @date 5:17 PM, 2018/4/25
 */
@Configuration
public class DruidServletConfiguration {

    @Bean
    public ServletRegistrationBean druidStatViewServletBean() {
        //后台的路径
        ServletRegistrationBean statViewServletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String, String> params = new HashMap<>();
        //账号密码，是否允许重置数据
        params.put("loginUsername", "sa");
        params.put("loginPassword", "1d85138a");
        params.put("resetEnable", "true");
        params.put("allow", "127.0.0.1");
        params.put("deny", "");
        statViewServletRegistrationBean.setInitParameters(params);
        return statViewServletRegistrationBean;
    }
}
