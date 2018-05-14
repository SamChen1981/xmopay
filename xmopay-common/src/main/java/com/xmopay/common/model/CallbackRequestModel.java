package com.xmopay.common.model;

import lombok.Data;

/**
 * com.xmopay.vo.request
 *
 * @author echo_coco.
 * @date 10:00 PM, 2018/5/2
 */
@Data
public class CallbackRequestModel extends BasicRequestModel {

    private String requestIp;
    private String body;
}
