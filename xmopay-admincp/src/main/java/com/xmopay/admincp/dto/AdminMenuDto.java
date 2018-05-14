package com.xmopay.admincp.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminMenuDto implements Serializable {
    private static final long serialVersionUID = 657365228360522398L;

    private Integer menuId;

    private String menuCode;

    private String menuName;

    private String menuDesc;

    private String menuUrl;

    private Integer menuParent;

    private Integer menuFlag;

    private Integer displayOrder;

    private Integer status;

    private AdminMenuDto parent;

}
