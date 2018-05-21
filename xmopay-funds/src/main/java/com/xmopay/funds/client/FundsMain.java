package com.xmopay.funds.client;

import com.alibaba.druid.pool.DruidDataSource;
import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.funds.entity.MessageEntity;
import com.xmopay.funds.message.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * com.xmopay.funds
 *
 * @author echo_coco.
 * @date 1:48 PM, 2018/4/26
 */
@Component("fundsMain")
public class FundsMain implements IFundsMain {

    private static final Logger logger = LoggerFactory.getLogger(FundsMain.class);

    @Value("${funds.execute.stop:true}")
    private Boolean stop;

    @Value("${funds.execute.enable:true}")
    private Boolean enable;

    @Value("${funds.execute.messageType:0}")
    private Integer messageType;

    @Resource
    private DruidDataSource druidDataSource;

    @Resource(name = "dbMessageConsumerImpl")
    private MessageConsumer messageConsumer;

    @Override
    @Async
    public void execute() {
        System.out.println("=====" + Thread.currentThread().getName() + "=========");
        if (!enable) {
            logger.info("*********************未开启资金结算读取任务*********************");
            return;
        }

        Connection connection = null;
        try {
            connection = druidDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("读取资金结算消息数据, 获取数据库连接异常!", e);
        }

        final String sql = getMessageQueueSql();

        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            MessageEntity messageEntity = null;
            List<Integer> mqIds = null;

            while (!stop) {
                mqIds = new ArrayList<>(16);

                statement = connection.prepareStatement(sql);
                rs = statement.executeQuery();
                while (rs.next()) {
                    int mqId = rs.getInt("MQID");
                    int notifyCount = rs.getInt("NOTIFY_COUNT");
                    String orderSn = rs.getString("ORDER_SN");
                    String partnerId = rs.getString("PARTNER_ID");
                    String msgBody = rs.getString("MESSAGE_BODY");
                    String msgTopic = rs.getString("MESSAGE_TOPIC");
                    String dateline = rs.getString("DATELINE");
                    String messageHost = rs.getString("MESSAGE_HOST");

                    mqIds.add(mqId);
                    messageEntity = new MessageEntity(mqId, notifyCount, orderSn, partnerId, msgBody, msgTopic, dateline, messageHost);
                    messageConsumer.consumer(messageEntity);
                }
                rs.close();
                statement.close();

                statement = connection.prepareStatement("update wp_message_queue set THREAD_STATUS = ?, CONSUMER_STATUS = ? where MQID = ?");
                for (int i = 0, len = mqIds.size(); i < len; i++) {
                    statement.setInt(1, 1);
                    statement.setInt(2, 1);
                    statement.setInt(3, mqIds.get(i));
                    statement.addBatch();
                }
                statement.executeBatch();
                statement.close();

                TimeUnit.MILLISECONDS.sleep(500L);
            }
        } catch (Exception e) {
            logger.error("执行资金结算消息异常!", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getMessageQueueSql() {
        return "SELECT * FROM wp_message_queue WHERE THREAD_STATUS = 0 AND CONSUMER_STATUS = 0 order by DATELINE ASC limit 25";
    }

}
