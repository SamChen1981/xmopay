package com.xmopay.common.model;

/**
 * com.xmopay.vo.request
 *
 * @author echo_coco.
 * @date 7:17 PM, 2018/5/2
 */
public abstract class BasicRequestModel {

    private String channelCode;

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelCode() {
        return channelCode;
    }
}
