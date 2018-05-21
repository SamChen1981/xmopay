package com.xmopay.funds;

import com.xmopay.funds.client.IFundsMain;
import com.xmopay.funds.listener.ApplicationStartedEventListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * com.xmopay.funds
 *
 * @author echo_coco.
 * @date 10:32 AM, 2018/4/26
 */
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.xmopay"})
@MapperScan(basePackages = {"com.xmopay.funds.dao"})
public class XmoPayFundsApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(XmoPayFundsApplication.class);
        app.addListeners(new ApplicationStartedEventListener());
        ConfigurableApplicationContext context = app.run(args);

        IFundsMain fundsMain = (IFundsMain) context.getBean("fundsMain");
        fundsMain.execute();
    }
}
