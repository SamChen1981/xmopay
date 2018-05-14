package com.xmopay.enums;

/**
 * Created by mimi on 2018/05/08
 */
public enum ChannelPayResultEnum {

    /** 未知定义 */
    UNKNOWN_ERROR("UNKNOWN_ERROR", "未知错误"),
    UNKNOWN_EXCEPTION_ERROR("UNKNOWN_EXCEPTION_ERROR", "未知异常错误"),

    /** 渠道定义 */
    CHANNEL_PAY_RESPONSE_FAILURE("CHANNEL_PAY_RESPONSE_FAILURE", "网关渠道请求响应失败"),
    CHANNEL_PAY_RESPONSE_SUCCESS("CHANNEL_PAY_RESPONSE_SUCCESS", "网关渠道请求响应成功"),
    CHANNEL_PAY_RESPONSE_EXCEPTION("CHANNEL_PAY_RESPONSE_EXCEPTION", "网关渠道请求响应异常"),
    CHANNEL_PAY_SIGNATURE_VALID_ERROR("CHANNEL_PAY_SIGN_EXCEPTION", "网关渠道签名校验错误"),
    CHANNEL_PAY_NOT_FOUND("CHANNEL_PAY_NOT_FOUND", "未找到可用渠道"),

    /** 订单定义 */
    TRADE_ORDER_OTHER_ERROR("TRADE_ORDER_OTHER_ERROR", "订单其他错误"),
    TRADE_ORDER_NOT_EXIST("TRADE_ORDER_NOT_EXIST", "订单查询不存在"),
    TRADE_ORDER_STATUS_ERROR("TRADE_ORDER_STATUS_ERROR", "请求订单状态错误"),
    TRADE_ORDER_AMOUNT_ERROR("TRADE_ORDER_AMOUNT_ERROR", "请求订单金额错误"),

    /** 参数定义 */
    ILLEGAL_SIGN_VALUE("ILLEGAL_SIGN_VALUE", "签名值不匹配"),
    ILLEGAL_MERCHANT_NO_ERROR("ILLEGAL_MERCHANT_NO_ERROR", "渠道商户号错误"),
    ILLEGAL_IP_ACCESS_FORBIDDEN("ILLEGAL_IP_ACCESS_FORBIDDEN", "请求访问受限，IP未在白名单内"),

    /** 成功定义 */
    RESPONSE_FAILURE("RESPONSE_FAILURE", "请求响应失败"),
    RESPONSE_SUCCESS("RESPONSE_SUCCESS", "请求响应成功");

    private String respCode;
    private String respMsg;

    ChannelPayResultEnum(String respCode, String respMsg) {
        this.respCode = respCode;
        this.respMsg = respMsg;
    }

    public String getRespCode() {
        return respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

}
