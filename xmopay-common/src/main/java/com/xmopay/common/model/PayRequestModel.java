package com.xmopay.common.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * com.xmopay.vo.request
 *
 * @author echo_coco.
 * @date 1:49 PM, 2018/4/28
 */
@Data
@ToString
public class PayRequestModel extends BasicRequestModel implements Serializable {
    private static final long serialVersionUID = -8879300040399295827L;

    private String orderSn;

    private String partnerId;

}
