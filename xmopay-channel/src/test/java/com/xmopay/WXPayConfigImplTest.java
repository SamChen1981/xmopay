package com.xmopay;

import com.xmopay.utils.wxpay.WXPayConfig;
import com.xmopay.utils.wxpay.WXPayConfigImpl;
import org.junit.Test;

/**
 * com.xmopay
 *
 * @author echo_coco.
 * @date 5:50 PM, 2018/5/2
 */
public class WXPayConfigImplTest {

    @Test
    public void testInit() throws Exception {
        WXPayConfigImpl wxPayConfig = WXPayConfigImpl.getInstance();
    }
}
