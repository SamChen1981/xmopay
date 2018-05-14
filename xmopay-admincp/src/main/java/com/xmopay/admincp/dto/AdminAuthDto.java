package com.xmopay.admincp.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminAuthDto implements Serializable {
    private static final long serialVersionUID = -2994697516892142163L;

    private Integer roleId;

    private Integer menuId;

    private AdminRoleDto adminRole;

    private AdminMenuDto adminMenu;

}
