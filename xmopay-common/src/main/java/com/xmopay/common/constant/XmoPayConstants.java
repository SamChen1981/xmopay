package com.xmopay.common.constant;

public class XmoPayConstants {


    public static final String API_VERSION = "V1.0";

    /**
     * UTF-8字符集
     **/
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * HTTP请求相关
     **/
    public static final String SIGN_METHOD_MD5 = "MD5";
    public static final String SIGN_METHOD_AES = "AES";
    public static final String SIGN_METHOD_RSA = "RSA";
    public static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

    public static String CONFIGSET = "system";

    // 后台用户session
    public static String SYSADMIN_SESSION = "sessionAdminUserInfo";


    // 支付网关
    public static int PAY_IN = 0;

    public static final String WXPAY = "WXPAY";

    /**
     * 支持交易接口
     */
    public static String API_NAME = "payment";
    public static String INTERFACE_NAME_LIST = "XMO_ONLINE_PAY";
    // 在线支付
    public static String XMO_ONLINE_PAY = "XMO_ONLINE_PAY";


    //================================================================================================================//
    //================================================== 产品业务类型 ==================================================//
    //================================================================================================================//
    /**
     * 交易: 1000 在线支付
     */
    public static int TYPE_ONLINE_PAY = 1000;


    //================================================================================================================//
    //================================================= 消息队列TOPIC =================================================//
    //================================================================================================================//
    /**
     * 1、交易 - 加款消息
     **/
    public static String TOPIC_TRADE_PAY_WAIT_SETTLE = "TOPIC_TRADE_PAY_WAIT_SETTLE";

    /**
     * 2、交易 - 通知消息
     **/
    public static String TOPIC_TRADE_PAY_WAIT_NOTIFY = "TOPIC_TRADE_PAY_WAIT_NOTIFY";

}
