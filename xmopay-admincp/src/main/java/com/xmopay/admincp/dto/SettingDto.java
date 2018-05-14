package com.xmopay.admincp.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SettingDto implements Serializable {

    private static final long serialVersionUID = -2445675831256473374L;
    private String key;

    private String value;
}
