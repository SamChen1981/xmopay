package com.xmopay.funds.exception;

/**
 * com.xmopay.funds.exception
 *
 * @author echo_coco.
 * @date 10:39 AM, 2018/4/27
 */
public class FundsException extends RuntimeException {

    private static final long serialVersionUID = -238091758285157331L;

    private String            errCode;
    private String            errMsg;

    public FundsException() {
        super();
    }

    public FundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FundsException(String message) {
        super(message);
    }

    public FundsException(Throwable cause) {
        super(cause);
    }

    public FundsException(String errCode, String errMsg) {
        super(errCode + ":" + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

}
