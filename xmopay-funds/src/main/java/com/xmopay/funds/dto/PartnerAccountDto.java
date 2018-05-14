package com.xmopay.funds.dto;

import java.io.Serializable;

/**
 * com.xmopay.funds.dto
 *
 * @author echo_coco.
 * @date 3:26 PM, 2018/4/27
 */
public class PartnerAccountDto implements Serializable {
    private static final long serialVersionUID = -4059482630574127558L;
    
    private String partnerId;

    private String balance;

    private String freezeBalance;

    private String lastTrade;

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBalance() {
        return balance;
    }

    public void setFreezeBalance(String freezeBalance) {
        this.freezeBalance = freezeBalance;
    }

    public String getFreezeBalance() {
        return freezeBalance;
    }

    public void setLastTrade(String lastTrade) {
        this.lastTrade = lastTrade;
    }

    public String getLastTrade() {
        return lastTrade;
    }
}
