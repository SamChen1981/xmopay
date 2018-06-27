package com.xmopay.funds.message;

import com.xmopay.funds.config.YmlConfig;
import com.xmopay.funds.entity.MessageEntity;
import com.xmopay.funds.service.IBasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * com.xmopay.funds.message
 *
 * @author echo_coco.
 * @date 10:39 PM, 2018/4/26
 */
@Component("dbMessageConsumerImpl")
public class DBMessageConsumerImpl implements MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DBMessageConsumerImpl.class);

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private YmlConfig ymlConfig;

    @Override
    public void consumer(MessageEntity messageEntity) {
        try {
            final String serviceName = ymlConfig.getTopics().get(messageEntity.getMsgTopic());
            if (serviceName == null) {
                logger.info("获取资金结算类失败 topic={}", messageEntity.getMsgTopic());
                return;
            }

            IBasicService service = (IBasicService) context.getBean(serviceName);
            service.execute(messageEntity);
        } catch (Exception e) {
            logger.error("资金结算类获取异常 topic={}", messageEntity.getMsgTopic(), e);
        }
    }

}
