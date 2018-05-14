package com.xmopay.openapi.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse<T>  {

    /**
     * 响应吗
     */
    private String respCode;

    /**
     * 响应消息
     */
    private String respMessage;

    /**
     * 响应结果
     */
    @JsonProperty(value = "resp_result")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T respResult;

    @JsonProperty(value = "trade_pay_resp_result")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T tradePayRespResult;

    public ApiResponse() {
    }

    public ApiResponse(String respCode, String respMessage, T respResult){
        this.respCode = respCode;
        this.respResult = respResult;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

    public T getRespResult() {
        return respResult;
    }

    public void setRespResult(T respResult) {
        this.respResult = respResult;
    }

    public T getTradePayRespResult() {
        return tradePayRespResult;
    }

    public void setTradePayRespResult(T tradePayRespResult) {
        this.tradePayRespResult = tradePayRespResult;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "respCode='" + respCode + '\'' +
                ", respMessage='" + respMessage + '\'' +
                ", respResult=" + respResult +
                ", tradePayRespResult=" + tradePayRespResult +
                '}';
    }
}
