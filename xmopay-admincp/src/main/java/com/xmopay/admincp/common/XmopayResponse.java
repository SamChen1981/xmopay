package com.xmopay.admincp.common;

import java.io.Serializable;

public class XmopayResponse<T> implements Serializable{

    private static final long serialVersionUID = 9143767808749978838L;

    public static final String ERROR = "RESP_ERROR";
    public static final String ISNULL = "RESP_NULL";
    public static final String SUCCESS = "RESP_SUCCESS";
    public static final String FAILURE = "RESP_FAILURE";
    public static final String EXCEPTION = "RESP_EXCEPTION";


    private String resultMsg;

    private String resultCode;

    private T resultData;

    public void init(){
        this.resultMsg = EXCEPTION;
        this.resultCode = "Response Exception";
    }

    public XmopayResponse(){
    }

    public XmopayResponse(String resultCode, String resultMsg, T resultData){
        this.resultMsg = resultMsg;
        this.resultCode = resultCode;
        this.resultData = resultData;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public T getResultData() {
        return resultData;
    }

    public void setResultData(T resultData) {
        this.resultData = resultData;
    }
}
