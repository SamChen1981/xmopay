package com.xmopay.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * com.xmopay.vo
 *
 * @author echo_coco.
 * @date 11:36 AM, 2018/5/2
 */
@Data
public class VoResult<T> implements Serializable {
    private static final long serialVersionUID = -1864907355538091991L;

    private String code;

    private String errMessage;

    private boolean success;

    private T data;

    public VoResult() {
    }

    public VoResult(String code, boolean success, String errMessage, T data) {
        this.code = code;
        this.success = success;
        this.errMessage = errMessage;
        this.data = data;
    }
}
