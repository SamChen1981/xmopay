package com.xmopay.admincp.service;

import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.AdminAuthDto;

/**
 * Created by mimi on 2/05/2018.
 */
public interface AdminAuthService {

    /**
    * @param
    * @Description: 删除后台角色权限表 对应数据
    * @return
    */
    SingleResult<Integer> deleteAuth(AdminAuthDto adminAuthDto);

    /**
    * @param
    * @Description: 增加权限
    * @return
    */
    SingleResult<Integer> addAuth(AdminAuthDto adminAuthDto);
}
