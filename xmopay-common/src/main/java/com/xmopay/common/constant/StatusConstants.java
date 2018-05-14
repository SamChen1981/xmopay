package com.xmopay.common.constant;

/**
 * Created by mimi on 6/05/2018.
 */
public class StatusConstants {
    //================================================================================================================//
    //================================================== 订单表相关  ==================================================//
    //================================================================================================================//
    /**
     * 订单交易 - 状态
     **/
    public static int TRADE_WAIT_TO_BANK = 0;         // 未提交到银行
    public static int TRADE_SUCCESS = 1;              // 交易成功
    public static int TRADE_WAIT_BANK_HANDLE = 2;     // 已提交待处理
    public static int TRADE_FAILURE = -1;             // 交易失败
    public static int TRADE_EXCEPTION = -9;           // 异常订单
    public static int TRADE_CLOSE = -2;               // 订单自动关闭
    public static int TRADE_REFUND = -3;              // 订单已退款

}

