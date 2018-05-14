package com.xmopay.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author echo_coco
 * @since
 */
@Data
public class GatewayChannelDto implements Serializable {
    private static final long serialVersionUID = -7620396369569295222L;
    private Integer channelId;

    private Integer agencyId;

    private Integer channelType;

    private String channelCode;

    private String channelName;

    private String channelKey;

    private Date updateTime;

    private Date dateline;

    private Integer status;

    private String channelSecret;

    private String channelParams;

    private GatewayAgencyDto gatewayAgencyDto;

    private GatewayChannelDto() {
    }

    private GatewayChannelDto(String channelCode) {
        this.channelCode = channelCode;
    }

    public static class Builder {
        private String channelCode;

        public Builder channelCode(String channelCode) {
            this.channelCode = channelCode;
            return this;
        }

        public GatewayChannelDto build() {
            return new GatewayChannelDto(channelCode);
        }
    }

}