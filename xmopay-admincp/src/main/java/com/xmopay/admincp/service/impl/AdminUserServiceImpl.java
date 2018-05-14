package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.AdminRoleDao;
import com.xmopay.admincp.dao.AdminUserDao;
import com.xmopay.admincp.dto.AdminUserDto;
import com.xmopay.admincp.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * com.xmopay.admincp.service.impl
 *
 * @author echo_coco.
 * @date 10:25 PM, 2018/4/25
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserDao adminUserDao;

    @Autowired
    private AdminRoleDao adminRoleDao;

    @Override
    public SingleResult<AdminUserDto> findAdminUserByName(String userName) {
        SingleResult<AdminUserDto> result = new SingleResult<>(false, null);
        AdminUserDto adminUserDto = adminUserDao.findAdminUserByName(userName);
        if (adminUserDto != null) {
            result.setResult(adminUserDto);
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    public SingleResult<AdminUserDto> getAdminUserInfo(String userName) {
        SingleResult<AdminUserDto> result = new SingleResult<>(false, null);
        AdminUserDto adminUserDto = adminUserDao.getAdminUserInfo(userName);
        if (adminUserDto != null) {
            result.setResult(adminUserDto);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * @param
     * @return
     * @Description: 根据  管理员编号muId 获取管理员信息
     */
    @Override
    public SingleResult<AdminUserDto> getAdminUserInfoByMuId(int muId) {
        SingleResult<AdminUserDto> result = new SingleResult<>(false, null);
        AdminUserDto adminUserDto = adminUserDao.getAdminUserInfoByMuId(muId);
        if (adminUserDto != null) {
            result.setResult(adminUserDto);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * @param
     * @return
     * @Description: 更新管理员信息
     */
    @Override
    public SingleResult<Integer> updateAdminUser(AdminUserDto adminUserDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        int line = adminUserDao.updateAdminUser(adminUserDto);
        if (adminUserDto != null) {
            result.setResult(line);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * @param
     * @return
     * @Description: 增加管理员信息
     */
    @Override
    public SingleResult<Integer> addAdminUser(AdminUserDto adminUserDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        int n = adminUserDao.addAdminUser(adminUserDto);
        if (n > 0) {
            result.setSuccess(true);
            result.setResult(n);
        }
        return result;
    }

    /**
     * @param
     * @return
     */
    @Override
    public SingleResult<Integer> updateGoogleCode(AdminUserDto adminUserDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        int num = adminUserDao.updateGoogleCode(adminUserDto);
        if (num > 0) {
            result.setResult(num);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * @param
     * @return
     * @Description: 获取管理员列表
     */
    @Override
    public SingleResult<PageInfo> getAdminUserList(AdminUserDto adminUserDto) {
        SingleResult<PageInfo> result = new SingleResult<>(false, null);
        PageHelper.startPage(adminUserDto.getCurrentPage(), adminUserDto.getPageSize());
        List<AdminUserDto> adminUserList = adminUserDao.getAdminUserList(adminUserDto);
        PageInfo<AdminUserDto> pageInfo = new PageInfo<>(adminUserList);
        if (null != adminUserList) {
            result.setResult(pageInfo);
            result.setSuccess(true);
        }

        return result;
    }
}
