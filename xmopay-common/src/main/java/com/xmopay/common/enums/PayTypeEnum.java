package com.xmopay.common.enums;

/**
 * com.xmopay.common.enums
 *
 * @author echo_coco.
 * @date 11:06 PM, 2018/5/3
 */
public enum  PayTypeEnum {

    WXPAY("WXPAY"),

    QQPAY("QQPAY"),

    ALIPAY("ALIPAY");

    private String payType;

    PayTypeEnum(String type) {
        this.payType = type;
    }

    public String payType() {
        return payType;
    }
}
