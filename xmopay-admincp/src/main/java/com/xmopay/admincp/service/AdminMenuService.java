package com.xmopay.admincp.service;

import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.AdminMenuDto;

import java.util.List;

/**
 * Created by mimi on 4/05/2018.
 */
public interface AdminMenuService {

    SingleResult<List<AdminMenuDto>> getParentMenus();

    SingleResult<List<AdminMenuDto>> findMenusByRoleId(int roleId);
    
    /**
    * @param
    * @Description: 获取所有菜单
    * @return
    */
    SingleResult<List<AdminMenuDto>> getMenusList();

    /**
    * @param
    * @Description: 根据menu_id 查找菜单信息
    * @return
    */
    SingleResult<AdminMenuDto> findMenusByMId(Integer mid);

    /**
    * @param 
    * @Description: 
    * @return
    */
    SingleResult<AdminMenuDto> ajaxByMenusValidate(AdminMenuDto menuParam);

    
    /**
    * @param
    * @Description: 添加
    * @return
    */
    SingleResult<Integer> addMenus(AdminMenuDto menuParam);

    
    /**
    * @param
    * @Description: 更新菜单信息
    * @return
    */
    SingleResult<Integer> updateMenu(AdminMenuDto reqMenu);

    /**
     * @param
     * @return
     * @Description: 查询下级菜单信息
     */
    SingleResult<List<AdminMenuDto>> getAdminMenusList(AdminMenuDto adminMenus);

    /**
     * @param
     * @return
     * @Description: 删除菜单
     */
    SingleResult<Integer> deleteAdminMenu(AdminMenuDto adminMenuDto);
}
