package com.xmopay.admincp.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * com.xmopay.dto
 *
 * @author echo_coco.
 * @date 9:59 PM, 2018/4/25
 */
@Data
public class AdminUserDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 8567283746902238182L;
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

    private AdminRoleDto adminRoleDto;
}
