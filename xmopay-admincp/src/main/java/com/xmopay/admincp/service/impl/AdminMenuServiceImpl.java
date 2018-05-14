package com.xmopay.admincp.service.impl;

import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.AdminMenuDao;
import com.xmopay.admincp.dto.AdminMenuDto;
import com.xmopay.admincp.service.AdminMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by monica on 21/04/2018.
 */
@Service
public class AdminMenuServiceImpl implements AdminMenuService {

    @Autowired
    private AdminMenuDao adminMenuDao;

    @Override
    public SingleResult<List<AdminMenuDto>> getParentMenus() {
        SingleResult<List<AdminMenuDto>> result = new SingleResult<>(false, null);
        List<AdminMenuDto> list = adminMenuDao.getParentMenus();
        if (null != list && list.size() > 0) {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    public SingleResult<List<AdminMenuDto>> findMenusByRoleId(int roleId) {
        SingleResult<List<AdminMenuDto>> result = new SingleResult<>(false, null);
        List<AdminMenuDto> list = adminMenuDao.findMenusByRoleId(roleId);
        if (null != list && list.size() > 0) {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }

    /**
    * @param
    * @Description: 获取所有菜单
    * @return
    */
    @Override
    public SingleResult<List<AdminMenuDto>> getMenusList() {
        SingleResult<List<AdminMenuDto>> result = new SingleResult<>(false, null);
        List<AdminMenuDto> list = adminMenuDao.getMenusList();
        if (null != list && list.size() > 0) {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }

    /**
    * @param
    * @Description: 根据menu_id 查找菜单详情
    * @return
    */
    @Override
    public SingleResult<AdminMenuDto> findMenusByMId(Integer mid) {
        SingleResult<AdminMenuDto> result = new SingleResult<>(false, null);
        AdminMenuDto adminMenu = adminMenuDao.getMenusByMId(mid);
        if (null != adminMenu ) {
            result.setResult(adminMenu);
            result.setSuccess(true);
        }
        return result;
    }

    /**
    * @param
    * @Description: 
    * @return
    */
    @Override
    public SingleResult<AdminMenuDto> ajaxByMenusValidate(AdminMenuDto menuParam) {
        SingleResult<AdminMenuDto> result = new SingleResult<>(false, null);
        AdminMenuDto adminMenu = adminMenuDao.ajaxByMenusValidate(menuParam);
        if (null != adminMenu ) {
            result.setResult(adminMenu);
            result.setSuccess(true);
        }
        return result;
    }


    /**
    * @param
    * @Description: 添加
    * @return
    */
    @Override
    public SingleResult<Integer> addMenus(AdminMenuDto menuParam) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        int n  = adminMenuDao.insertMenus(menuParam);
        if (n > 0) {
            result.setResult(n);
            result.setSuccess(true);
        }
        return result;
    }

    /**
    * @param
    * @Description: 更新菜单信息
    * @return
    */
    @Override
    public SingleResult<Integer> updateMenu(AdminMenuDto reqMenu) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        int n  = adminMenuDao.updateMenu(reqMenu);
        if (n > 0) {
            result.setResult(n);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * @param
     * @return
     * @Description: 查询下级菜单信息
     */
    @Override
    public SingleResult<List<AdminMenuDto>> getAdminMenusList(AdminMenuDto adminMenus) {
        SingleResult<List<AdminMenuDto>> result = new SingleResult<>(false, null);
        List<AdminMenuDto> adminMenusList = adminMenuDao.getAdminMenusList(adminMenus);
        if (null != adminMenusList && adminMenusList.size() > 0) {
            result.setResult(adminMenusList);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * @param
     * @return
     * @Description: 删除菜单
     */
    @Override
    public SingleResult<Integer> deleteAdminMenu(AdminMenuDto adminMenuDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        Integer n = adminMenuDao.deleteAdminMenu(adminMenuDto);
        if(n >0){
            result.setResult(n);
            result.setSuccess(true);
        }
        return result;
    }
}
