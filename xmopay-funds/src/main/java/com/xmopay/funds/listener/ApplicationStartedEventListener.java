package com.xmopay.funds.listener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * com.xmopay.funds.listener
 *
 * @author echo_coco.
 * @date 10:33 AM, 2018/4/26
 */
@Component
public class ApplicationStartedEventListener implements ApplicationListener<ContextRefreshedEvent> {

    private static boolean initialized = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final ApplicationContext app = event.getApplicationContext();
        System.out.println("app.getParent(): " + app.getParent());
        System.out.println("app.getId(): " + app.getId());
        System.out.println("app.getDisplayName(): " + app.getDisplayName());
        System.out.println("app.getApplicationName(): " + app.getApplicationName());
    }
}
