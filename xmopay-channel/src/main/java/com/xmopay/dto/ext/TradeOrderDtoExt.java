package com.xmopay.dto.ext;

import com.xmopay.dto.TradeOrderDto;
import lombok.Data;

import java.io.Serializable;

/**
 * com.xmopay.dto
 *
 * @author echo_coco.
 * @date 2:58 PM, 2018/5/2
 */
@Data
public class TradeOrderDtoExt extends TradeOrderDto implements Serializable {
    private static final long serialVersionUID = -2288590716667006517L;

    private Integer oriOrderStatus;

    public TradeOrderDtoExt() {
    }

    public TradeOrderDtoExt(String orderSn, String partnerId, Integer orderStatus, Integer oriOrderStatus) {
        this.setOrderSn(orderSn);
        this.setPartnerId(partnerId);
        this.setOrderStatus(orderStatus);
        this.oriOrderStatus = oriOrderStatus;
    }

    public static class Builder {
        private String orderSn;
        private String partnerId;
        private Integer orderStatus;
        private Integer oriOrderStatus;

        public Builder orderSn(String orderSn) {
            this.orderSn = orderSn;
            return this;
        }

        public Builder partnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public Builder orderStatus(Integer orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder oriOrderStatus(Integer oriOrderStatus) {
            this.oriOrderStatus = oriOrderStatus;
            return this;
        }

        public TradeOrderDtoExt build() {
            return new TradeOrderDtoExt(orderSn, partnerId, orderStatus, oriOrderStatus);
        }
    }
}
