package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.AdminUserDto;

/**
 * com.xmopay.admincp.service
 *
 * @author echo_coco.
 * @date 10:24 PM, 2018/4/25
 */
public interface AdminUserService {

    SingleResult<AdminUserDto> findAdminUserByName(String userName);

    SingleResult<AdminUserDto>  getAdminUserInfo(String userName);

    /**
     * @param
     * @Description: 更新管理员信息
     * @return
     */
    SingleResult<Integer> updateAdminUser(AdminUserDto adminUserDto);


    /**
     * @param
     * @Description: 根据  管理员编号muId 获取管理员信息
     * @return
     */
    SingleResult<AdminUserDto>  getAdminUserInfoByMuId(int muId);


    /**
     * @param
     * @Description: 增加管理员信息
     * @return
     */
    SingleResult<Integer> addAdminUser(AdminUserDto adminUserDt);

    SingleResult<Integer> updateGoogleCode(AdminUserDto adminUserDto);

    /**
     * @param
     * @Description: 获取管理员列表
     * @return
     */
    SingleResult<PageInfo> getAdminUserList(AdminUserDto adminUserDto);

}
