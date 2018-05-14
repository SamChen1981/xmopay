package com.xmopay.common.model;

import lombok.Data;

/**
 * com.xmopay.vo.request
 *
 * @author echo_coco.
 * @date 7:11 PM, 2018/5/2
 */
@Data
public class QueryOrderRequestModel extends BasicRequestModel {

    private String orderSn;

    private String partnerId;
}
