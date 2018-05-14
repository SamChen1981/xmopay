package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.AdminRoleDto;

import java.util.List;

/**
 * Created by monica on 2/05/2018.
 */
public interface AdminRoleService {

    SingleResult<AdminRoleDto> getUserRole(Integer roleId);

    /**
    * @param
    * @Description: 根据角色名查找角色
    * @return
    */
    SingleResult<AdminRoleDto> getUserRoleByRoleName(String roleName);

    /**
    * @param
    * @Description: 获取管理员角色表 全部数据信息 (无参数、无分页)
    * @return
    */
    SingleResult<List<AdminRoleDto>> getAdminRoleList();

    /**
    * @param
    * @Description: 获取管理员角色表 全部数据信息(分页)
    * @return
    */
    SingleResult<PageInfo> getAdminRolePagesList(AdminRoleDto adminRoleDto);

    /**
    * @param
    * @Description: 增加角色
    * @return
    */
    SingleResult<Integer> addRole(AdminRoleDto adminRoleParam);

    /**
    * @param
    * @Description: 更新角色信息
    * @return
    */
    SingleResult<Integer> updateRole(AdminRoleDto adminRoleParam);

    /**
    * @param
    * @Description: 根据id 删除角色
    * @return
    */
    SingleResult<Integer> deleteRoleByIds(String rids);
}
