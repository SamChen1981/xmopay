package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.AdminMenuDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by mimi on 3/05/2018.
 */
@Mapper
public interface AdminMenuDao {

    List<AdminMenuDto> getParentMenus();

    List<AdminMenuDto> findMenusByRoleId(int roleId);

    /**
    * @param
    * @Description: 获取所有菜单列表
    */
    List<AdminMenuDto> getMenusList();

    /**
    * @param
    * @Description: 根据menu_id 获取菜单详情
    * @return
    */
    AdminMenuDto getMenusByMId(Integer mid);

    /**
    * @param
    * @Description: 
    */
    AdminMenuDto ajaxByMenusValidate(AdminMenuDto menuParam);

    /**
    * @param
    * @Description: 添加
    * @return
    */
    int insertMenus(AdminMenuDto menuParam);

    /**
    * @param
    * @Description: 更新菜单信息
    * @return
    */
    int updateMenu(AdminMenuDto reqMenu);

    /**
     * @param
     * @return
     * @Description: 查询下级菜单
     */
    List<AdminMenuDto> getAdminMenusList(AdminMenuDto adminMenus);

    /**
     * @param
     * @return
     * @Description: 删除菜单
     */
    Integer deleteAdminMenu(AdminMenuDto adminMenuDto);
}
