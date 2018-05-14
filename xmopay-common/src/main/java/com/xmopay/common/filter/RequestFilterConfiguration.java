package com.xmopay.common.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.List;

/**
 * com.xmopay.common.filter
 *
 * @author echo_coco.
 * @date 12:56 AM, 2018/4/26
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class RequestFilterConfiguration {
    @Bean
    public FilterRegistrationBean pcdRequestIdFilterRegistrationBean() {
        RequestFilter filter = new RequestFilter();
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        List<String> urlPatterns = Arrays.asList("/*");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;

    }
}