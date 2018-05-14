package com.xmopay.admincp.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdminRoleDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = -3225424699377348984L;

    private Integer rid;

    private String roleName;

    private String roleDesc;

    private Date dateLine;
}
