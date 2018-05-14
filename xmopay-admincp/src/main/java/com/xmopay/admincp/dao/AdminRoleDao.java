package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.AdminRoleDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by mimi on 1/05/2018.
 */
@Mapper
public interface AdminRoleDao {

    AdminRoleDto getUserRole(AdminRoleDto adminRoleDto);

    /**
    * @param
    * @Description: 获取管理员角色表 全部数据信息
    * @return
    */
    List<AdminRoleDto> getAdminRoleList(AdminRoleDto adminRoleDto);

    /**
    * @param
    * @Description: 增加角色
    * @return
    */
    int insertRole(AdminRoleDto adminRoleParam);

    /**
    * @param
    * @Description: 更新角色信息
    * @return
    */
    int updateRole(AdminRoleDto adminRoleParam);

    /**
    * @param
    * @Description: 据编号rid 删除角色
    * @return
    */
    int deleteRoleByRid(Integer rid);
}
