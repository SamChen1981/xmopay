package com.xmopay.admincp.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AdminUserVo {

    private Integer muId;

    private Integer roleId;

    private String userName;

    private String password;

    private String bindIp;

    private String lastIp;

    private Date lastLogin;

    private String salts;

    private Integer status;

    private String googleCode;
}
