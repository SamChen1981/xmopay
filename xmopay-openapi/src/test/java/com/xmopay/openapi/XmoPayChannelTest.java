package com.xmopay.openapi;

import com.xmopay.openapi.utils.HttpPost;
import org.junit.Test;

/**
 * com.xmopay.openapi
 *
 * @author echo_coco.
 * @date 11:03 PM, 2018/5/5
 */
public class XmoPayChannelTest {
    @Test
    public void testChannel() {
        try {
            String channelCode = "aliPayChannelServiceImpl_WXPAY_PAYIN_31007354".split("\\_")[0];
            System.out.println(channelCode);
            StringBuilder params = new StringBuilder(16);
            params.append("{");
            params.append("\"channel_code\":\"" + channelCode + "\",");
            params.append("\"partner_id\":\"" + "624109" + "\",");
            params.append("\"order_sn\":\"1805051754195589365\"");
            params.append("}");
            System.out.println("请求参数: " + params);
            String ret = HttpPost.doPost("http://localhost:9092/channel/pay", params.toString());
            System.out.println("渠道响应结果: " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
