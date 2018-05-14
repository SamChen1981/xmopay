package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.AdminUserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * com.xmopay.dao
 */
@Mapper
public interface AdminUserDao {

    AdminUserDto findAdminUserByName(String userName);

    AdminUserDto getAdminUserInfo(String userName);

    /**
     * @param
     * @Description:  根据  管理员编号muId 获取管理员信息
     * @return
     */
    AdminUserDto getAdminUserInfoByMuId(int muId);


    /**
     * @param
     * @Description: 更新管理员信息
     * @return
     */
    Integer updateAdminUser(AdminUserDto adminUserDto);


    /**
     * @param
     * @Description: 增加管理员信息
     * @return
     */
    int addAdminUser(AdminUserDto adminUserDto);


    /**
     * @param
     * @Description: 更新googleCode
     * @return
     */
    int updateGoogleCode(AdminUserDto adminUserDto);

    /**
     * @param
     * @Description: 获取管理员列表
     * @return
     */
    List<AdminUserDto> getAdminUserList(AdminUserDto adminUserDto);

}
