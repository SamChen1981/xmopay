package com.xmopay.admincp.controller;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.xmopay.admincp.common.GoogleManger;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.common.WebCommon;
import com.xmopay.admincp.common.XmopayResponse;
import com.xmopay.admincp.dto.AdminAuthDto;
import com.xmopay.admincp.dto.AdminMenuDto;
import com.xmopay.admincp.dto.AdminRoleDto;
import com.xmopay.admincp.dto.AdminUserDto;
import com.xmopay.admincp.service.AdminAuthService;
import com.xmopay.admincp.service.AdminMenuService;
import com.xmopay.admincp.service.AdminRoleService;
import com.xmopay.admincp.service.AdminUserService;
import com.xmopay.common.utils.XmoPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.*;

@Controller
@RequestMapping(value = "protal")
@Slf4j
public class ProtalController {

    @Autowired
    private AdminMenuService adminMenuService;

    @Autowired
    private AdminRoleService adminRoleService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminAuthService adminAuthService;

    @RequestMapping(value = "/index")
    public String index(HttpServletRequest req) {
        try {
            AdminUserDto user = WebCommon.getSessionUserInfo(req);
            if(user == null) {
                return new String("redirect:/user/login");
            }
            req.setAttribute("users", user);

            SingleResult<AdminRoleDto> adminRole = adminRoleService.getUserRole(user.getRoleId());
            if (adminRole.isSuccess()) {
                req.setAttribute("roleName", adminRole.getResult().getRoleName());
            }

            // 生成系统菜单
            String menus = getMenusJson(user.getUserName(), user.getRoleId());
            req.setAttribute("menus", menus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String("index");
    }

    /**
     * 后台 欢迎页面
     *
     * @return
     */
    @RequestMapping(value = "/welcome")
    public String welcome(HttpServletRequest request) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "welcome";
    }

    /**
     * @param
     * @return
     * @Description: 初始化 admin 菜单栏
     */
    @ResponseBody
    @RequestMapping(value = "/initMenus")
    public XmopayResponse<String> initMenus(HttpServletRequest request) {
        XmopayResponse<String> result = new XmopayResponse<>(XmopayResponse.FAILURE, "", null);
        //记录日志需要
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid = sessionUser.getMuId();
        Integer roleId = sessionUser.getRoleId();
        //日志
        String logmsg = "[用户常规事件] 当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        try {

            String userName = request.getParameter("userName");
            if (XmoPayUtils.isEmpty(userName)) {
                result.setResultMsg("参数错误!");
                return result;
            }
            if (roleId != 1) {
                result.setResultMsg("不是超级管理员，无需初始化!");
                return result;
            }

            SingleResult<List<AdminMenuDto>> findMenusResult = adminMenuService.getAdminMenusList(null);
            if (findMenusResult.isSuccess()) {
                List<AdminMenuDto> menuDtoList = findMenusResult.getResult();
                AdminAuthDto adminAuth = new AdminAuthDto();
                adminAuth.setRoleId(roleId);
                for (AdminMenuDto adminMenuDto : menuDtoList) {
                    adminAuth.setMenuId(adminMenuDto.getMenuId());
                    adminAuthService.addAuth(adminAuth);
                }
                log.info(logmsg, "admin 菜单栏初始化成功！");
                result.setResultMsg("初始化成功!");
                result.setResultCode(XmopayResponse.SUCCESS);
                return result;
            }
            log.error(logmsg, "无菜单可供初始化！");
            result.setResultMsg("无菜单可供初始化!");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(logmsg, "admin 菜单栏初始化异常！");
            result.setResultMsg("admin 菜单栏初始化异常");
            result.setResultCode(XmopayResponse.EXCEPTION);
            return result;
        }
    }

    /**
     * 生成JSON菜单
     * @param roleId
     * @return
     */
    public String getMenusJson(String username, int roleId) {
        String menus = "";
        try {
            // 菜单list
            List<AdminMenuDto> menusList = new ArrayList();
            //获取当前角色所有菜单
            SingleResult<List<AdminMenuDto>> singleResult = adminMenuService.findMenusByRoleId(roleId);
            if (singleResult.isSuccess()) {
                menusList = singleResult.getResult();
            }
            if (menusList.size() <= 0) {
                return "";
            }

            //组装菜单栏List
            List<Map> parentMenusList = new ArrayList();
            List<Map> subMenusList = new ArrayList();
            for (AdminMenuDto menuDto : menusList) {
                if (0 == menuDto.getMenuParent()) { // '上级菜单' 0 最上级菜单
                    parentMenusList.add(JSON.parseObject(JSON.toJSONString(menuDto), Map.class));
                } else {
                    subMenusList.add(JSON.parseObject(JSON.toJSONString(menuDto), Map.class));
                }
            }

            // 1级菜单
            Map menusMap;
            TreeMap insertMap = new TreeMap();
            for (Map m : parentMenusList) {
                String menu_parent = String.valueOf(m.get("menuParent")); // '上级菜单' 0 最上级菜单
                if (!"0".equals(menu_parent)) {
                    continue;
                }
                String menuId = String.valueOf(m.get("menuId"));
                String defaultName = "dashboard".equals(String.valueOf(m.get("menuCode"))) ? String.valueOf(m.get("menuCode")) : String.valueOf(m.get("displayOrder")) + String.valueOf(m.get("menuCode"));

                menusMap = new TreeMap();
                menusMap.put("text", m.get("menuName"));

                // 子菜单
                if (null != subMenusList && subMenusList.size() > 0) {
                    menusMap = getSubMenus(menusMap, subMenusList, menuId);
                }
                insertMap.put(defaultName, menusMap);
            }

            createWelMenu(insertMap);

            menus = JSON.toJSONString(insertMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menus;
    }


    /**
     * 获取子菜单
     *
     * @param mid
     * @return
     */
    public Map getSubMenus(Map menusMap, List<Map> subMenusList, String mid) {
        if (subMenusList.size() <= 0) {
            return null;
        }

        Map map1;
        Map map2 = new TreeMap();
        String defaultMenu = "";
        for (Map m : subMenusList) {
            String menuParent = String.valueOf(m.get("menuParent"));
            if (menuParent.equals(mid)) {
                String menuCode = String.valueOf(m.get("menuCode"));
                String menuName = String.valueOf(m.get("menuName"));
                String menuUrl = String.valueOf(m.get("menuUrl"));
                String status = String.valueOf(m.get("status"));
                String displayOrder = String.valueOf(m.get("displayOrder"));

                map1 = new TreeMap();
                map1.put("text", menuName);
                map1.put("url", menuUrl);
                map1.put("display_order", displayOrder);

                map2.put(displayOrder + menuCode, map1);

                if ("1".equals(status)) {
                    defaultMenu = displayOrder + menuCode;
                }
            }
        }

        menusMap.put("default", defaultMenu);
        menusMap.put("children", map2);

        return menusMap;
    }

    /**
     * 构建首页菜单欢迎页
     */
    private Map createWelMenu(Map menusMap) {
        Map defaultMap = new HashMap();
        defaultMap.put("text", "欢迎页面");
        defaultMap.put("url", "protal/welcome");

        Map defaultSubMap = new HashMap();
        defaultSubMap.put("welcome", defaultMap);

        Map defaultMenusMap = new HashMap();
        defaultMenusMap.put("text", "常用菜单");
        defaultMenusMap.put("subtext", "常用菜单");
        defaultMenusMap.put("default", "welcome");
        defaultMenusMap.put("children", defaultSubMap);

        menusMap.put("0dashboard", defaultMenusMap);
        return menusMap;
    }


}
