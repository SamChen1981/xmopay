package com.xmopay.admincp.dto;

import lombok.Data;

@Data
public class BaseDto {

    private java.lang.Integer currentPage;
    private java.lang.Integer pageSize;

    private String startTime;
    private String endTime;
}
