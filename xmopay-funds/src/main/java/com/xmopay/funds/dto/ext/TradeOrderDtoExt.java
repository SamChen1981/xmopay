package com.xmopay.funds.dto.ext;

import com.xmopay.funds.dto.TradeOrderDto;

/**
 * com.xmopay.funds.dto
 *
 * @author echo_coco.
 * @date 1:51 PM, 2018/4/27
 */
public class TradeOrderDtoExt extends TradeOrderDto {

    private Integer extOrderStatus;

    public void setExtOrderStatus(Integer extOrderStatus) {
        this.extOrderStatus = extOrderStatus;
    }

    public Integer getExtOrderStatus() {
        return extOrderStatus;
    }
}
