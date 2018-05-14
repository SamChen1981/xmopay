package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.AdminAuthDao;
import com.xmopay.admincp.dao.AdminRoleDao;
import com.xmopay.admincp.dto.AdminAuthDto;
import com.xmopay.admincp.dto.AdminRoleDto;
import com.xmopay.admincp.service.AdminRoleService;
import com.xmopay.common.utils.XmoPayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by monica on 21/04/2018.
 */
@Service
public class AdminRoleServiceImpl implements AdminRoleService {

    @Autowired
    private AdminRoleDao adminRoleDao;

    @Autowired
    private AdminAuthDao adminAuthDao;

    @Override
    public SingleResult<AdminRoleDto> getUserRole(Integer roleId) {
        SingleResult<AdminRoleDto> result = new SingleResult<>(false, null);
        AdminRoleDto roleParam = new AdminRoleDto();
        roleParam.setRid(roleId);
        AdminRoleDto adminRoleDto = adminRoleDao.getUserRole(roleParam);
        if (adminRoleDto != null) {
            result.setResult(adminRoleDto);
            result.setSuccess(true);
        }
        return result;
    }

    /**
    * @param
    * @Description: 根据角色名查找角色
    * @return
    */
    @Override
    public SingleResult<AdminRoleDto> getUserRoleByRoleName(String roleName) {
        SingleResult<AdminRoleDto> result = new SingleResult<>(false, null);
        AdminRoleDto roleParam = new AdminRoleDto();
        roleParam.setRoleName(roleName);
        AdminRoleDto adminRoleDto = adminRoleDao.getUserRole(roleParam);
        if (adminRoleDto != null) {
            result.setResult(adminRoleDto);
            result.setSuccess(true);
        }
        return result;
    }

    /**
    * @param
    * @Description: 获取管理员角色表 全部数据信息 (无参数、无分页)
    */
    @Override
    public SingleResult<List<AdminRoleDto>> getAdminRoleList() {
        SingleResult<List<AdminRoleDto>> result = new SingleResult<>(false, null);
        List<AdminRoleDto> adminRoleList = adminRoleDao.getAdminRoleList(null);
        if (null != adminRoleList) {
            result.setResult(adminRoleList);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * @param
     * @Description: 获取管理员角色表 全部数据信息(分页)
     * @return
     */
    @Override
    public SingleResult<PageInfo> getAdminRolePagesList(AdminRoleDto adminRoleDto) {
        SingleResult<PageInfo> result = new SingleResult<>(false, null);
        PageHelper.startPage(adminRoleDto.getCurrentPage(), adminRoleDto.getPageSize());
        List<AdminRoleDto> adminRoleList = adminRoleDao.getAdminRoleList(adminRoleDto);
        PageInfo<AdminRoleDto> pageInfo  = new PageInfo<>(adminRoleList);
        if(adminRoleList != null){
            result.setResult(pageInfo);
            result.setSuccess(true);
        }
        return result;
    }

    /**
    * @param
    * @Description: 增加角色
    * @return
    */
    @Override
    public SingleResult<Integer> addRole(AdminRoleDto adminRoleParam) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        int n = adminRoleDao.insertRole(adminRoleParam);
        if (n > 0) {
            result.setResult(n);
            result.setSuccess(true);
        }
        return result;
    }


    /**
    * @param
    * @Description: 更新角色信息
    * @return
    */
    @Override
    public SingleResult<Integer> updateRole(AdminRoleDto adminRoleParam) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        int n = adminRoleDao.updateRole(adminRoleParam);
        if (n > 0) {
            result.setResult(n);
            result.setSuccess(true);
        }
        return result;
    }


    /**
    * @param
    * @Description: 根据编号rid 删除角色
    * @return
    */
    @Transactional
    @Override
    public SingleResult<Integer> deleteRoleByIds(String rids) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try{
            String[] ids_ = rids.split(",");
            for (String rid : ids_) {
                int n = adminRoleDao.deleteRoleByRid(XmoPayUtils.isEmpty(rid) ? null : Integer.valueOf(rid));
                //删除当前角色下所有菜单栏关系
                AdminAuthDto adminAuthDto = new AdminAuthDto();
                adminAuthDto.setRoleId(XmoPayUtils.isEmpty(rid) ? null : Integer.valueOf(rid));
                int m = adminAuthDao.deleteAuth(adminAuthDto);
            }
            result.setResult(1);
            result.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(0);
            result.setSuccess(false);
            throw new RuntimeException("Transactional rollback runtimeException!");
        }
        return result;
    }
}
