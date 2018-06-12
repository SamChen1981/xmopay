package com.xmopay.admincp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.*;
import com.xmopay.admincp.dto.*;
import com.xmopay.admincp.service.*;
import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.common.utils.OpenIPAPI;
import com.xmopay.common.utils.XmoPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * com.xmopay.controller
 *
 * @author monica.
 * @date 9:00 PM, 2018/4/25
 */
@Controller
@RequestMapping(value = "user")
@Slf4j
public class AdminController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminRoleService adminRoleService;

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private AdminMenuService adminMenuService;

    @Autowired
    private HandleLogsService handleLogsService;

    @RequestMapping(value = "login")
    public String login() {
        return "login";
    }

    @ResponseBody
    @RequestMapping(value = "checkGoogleCode")
    public XmopayResponse<String> checkGoogleCode(javax.servlet.http.HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse<>(XmopayResponse.FAILURE, "验证GoogleCode失败", null);
        try {
            //日志
            String logmsg = "事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

            String username = request.getParameter("username");
            if (XmoPayUtils.isEmpty(username)) {
                resultResp.setResultMsg("用户名为空,请输入");
                return resultResp;
            }

            SingleResult<AdminUserDto> adminUserDtoResult = adminUserService.findAdminUserByName(username);
            if (!adminUserDtoResult.isSuccess()) {
                log.error(logmsg, "此账户还未注册,请先注册!");
                resultResp.setResultMsg("此账户还未注册,请先注册!");
                return resultResp;
            }
            AdminUserDto adminUserDto = adminUserDtoResult.getResult();
            System.out.println(adminUserDto.toString());

            if ("-1".equals(adminUserDto.getStatus().toString())) {
                log.error(logmsg, "黑名单或者失效用户,请重新注册!");
                resultResp.setResultMsg("黑名单或者失效用户,请重新注册!");
                return resultResp;
            }

            if ("0".equals(adminUserDto.getStatus().toString()) || "2".equals(adminUserDto.getStatus().toString())) {
                log.error(logmsg, "未激活用户,请联系客服!");
                resultResp.setResultMsg("未激活用户,请联系客服!");
                return resultResp;
            }
            if(!XmoPayUtils.isEmpty(adminUserDto.getBindIp()) && !adminUserDto.getBindIp().equals(XmoPayUtils.getIpAddr(request))){
                log.error(logmsg, "管理员已绑定登陆IP，IP错误无法登陆!");
                resultResp.setResultMsg("管理员已绑定登陆IP，IP错误无法登陆!");
                return resultResp;
            }

            String SECRET = "";
            String IS_BINDING = "";

            String googlecode = adminUserDto.getGoogleCode() == null ? "" : adminUserDto.getGoogleCode();

            if (!XmoPayUtils.isEmpty(googlecode)) {
                Map googleMap = JSON.parseObject(googlecode);
                SECRET = googleMap.get("SECRET").toString();
                IS_BINDING = googleMap.get("IS_BINDING").toString();
            }

            if ("".equals(SECRET) || "".equals(IS_BINDING)) {
                log.info(logmsg, "未绑定Google身份验证!");
                resultResp.setResultMsg("未绑定Google身份验证");
                return resultResp;
            }
            if ("0".equals(IS_BINDING)) {
                log.info(logmsg, "未绑定Google身份验证!");
                resultResp.setResultMsg("未绑定Google身份验证");
                return resultResp;
            }
            log.info(logmsg, "查询验证信息!");
            resultResp.setResultMsg("查询验证信息");
            resultResp.setResultCode(XmopayResponse.SUCCESS);
            return resultResp;
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    /**
     * 用户登录(AJAX)
     */
    @ResponseBody
    @RequestMapping(value = "/dologin")
    public XmopayResponse<String> dologin(javax.servlet.http.HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse(XmopayResponse.FAILURE, "", null);

        //日志
        String logmsg = "事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);
        try {
            String username = request.getParameter("username").trim();
            String password = request.getParameter("password").trim();
            String googlecode = request.getParameter("googlecode").trim();

            //判断用户信息是否为空
            if (XmoPayUtils.isEmpty(username) || XmoPayUtils.isEmpty(password)) {
                log.info(logmsg, "账号或者密码为空!");
                resultResp.setResultMsg("请输入账号或者密码");
                return resultResp;
            }

            //判断用户信息是否正确，正确则登录
            SingleResult<AdminUserDto> adminUserResult = adminUserService.findAdminUserByName(username);
            if (!adminUserResult.isSuccess()) {
                log.info(logmsg, "无此管理员用户!");
                resultResp.setResultMsg("无此管理员用户");
                return resultResp;
            }
            AdminUserDto adminUserDto = adminUserResult.getResult();

            //验证用户状态
            if ("0".equals(adminUserDto.getStatus())) {
                log.info(logmsg, "此账户被冻结,请联系管理员!");
                resultResp.setResultMsg("此账户被冻结,请联系管理员");
                return resultResp;
            }
            if(!XmoPayUtils.isEmpty(adminUserDto.getBindIp()) && !adminUserDto.getBindIp().equals(XmoPayUtils.getIpAddr(request))){
                log.error(logmsg, "管理员已绑定登陆IP，IP错误无法登陆!");
                resultResp.setResultMsg("管理员已绑定登陆IP，IP错误无法登陆!");
                return resultResp;
            }

            //验证用户登录密码是否正确
            if (!adminUserDto.getPassword().equals(XmoPayUtils.getCertifiedSigned(password, adminUserDto.getSalts()))) {
                log.info(logmsg, "账户密码错误，请重输!");
                resultResp.setResultMsg("账户密码错误，请重输");
                return resultResp;
            } else {
                // 在这里把用户角色加入
                SingleResult<AdminRoleDto> adminRoleDtoResult = adminRoleService.getUserRole(adminUserDto.getRoleId());
                if (adminRoleDtoResult.isSuccess()) {
//                    adminUser.setAdminRole(adminRoleDtoResult.getResult());
                }

                //google 验证参数
                String gcode = adminUserDto.getGoogleCode() == null ? "" : adminUserDto.getGoogleCode().toString();

                String secret = "";
                String isBinding = "0";
                if (!"".equals(gcode)) {
                    Map googleMap = JSON.parseObject(gcode);
                    secret = googleMap.get("SECRET").toString();
                    isBinding = googleMap.get("IS_BINDING").toString();
                }

                //验证google安全码
                if ("1".equals(isBinding)) { //当绑定时才进行验证
                    if (googlecode == null || "".equals(googlecode) || googlecode.trim().length() == 0) {
                        resultResp.setResultMsg("请输入Google验证码");
                        return resultResp;
                    }

                    if ("".equals(secret)) {
                        resultResp.setResultMsg("激活Google身份验证器,请联系管理员");
                        return resultResp;
                    }

                    if (!GoogleManger.checkGoogleCode(secret, googlecode, adminUserDto.getMuId().toString())) {
                        resultResp.setResultMsg("请输入正确的Google验证码");
                        return resultResp;
                    }
                }

                //更新用户登录信息
                adminUserDto.setLastIp(XmoPayUtils.getIpAddr(request));
                adminUserDto.setLastLogin(new Date());
                adminUserService.updateAdminUser(adminUserDto);

                //设置登录后的session
                HttpSession session = request.getSession();
                session.setAttribute(XmoPayConstants.SYSADMIN_SESSION, adminUserDto);

                //记录日志
                HandleLogsDto handleLogsDto = new HandleLogsDto();
                handleLogsDto.setPuserid(adminUserDto.getMuId().toString());
                handleLogsDto.setPartnerId("0");
                handleLogsDto.setHandleType("TYPE_SYSADMIN_HANDLE");
                handleLogsDto.setHandleCode("SYSADMIN_LOGIN");
                handleLogsDto.setHandleEvents("登陆成功!");
                handleLogsDto.setHandleParams(logmsg.replace("{}", "登陆成功, 欢迎进入系统..."));
                handleLogsDto.setHandleStatus(1);
                handleLogsDto.setHandleIp(XmoPayUtils.getIpAddr(request));
                handleLogsDto.setDateline(new Date());
                handleLogsService.insertHandleLogs(handleLogsDto);

                //输出登录成功信息
                log.info(logmsg, "登陆成功, 欢迎进入系统...");
                resultResp.setResultMsg("登陆成功, 欢迎进入系统...");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    /**
     * 用户退出
     *
     * @return
     */
    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        //日志
        String logmsg = "事件描述={退出系统成功！}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);
        log.info(logmsg);
        request.getSession().removeAttribute(XmoPayConstants.SYSADMIN_SESSION);
        return "login";
    }

    /**
     */
    @RequestMapping(value = "/adminUserList")
    public String adminUserList(HttpServletRequest request) {
        try {
            String inajax = request.getParameter("inajax");
            String username = request.getParameter("username");
            String muid = request.getParameter("muid");

            request.setAttribute("username", username);
            request.setAttribute("muid", muid);
            request.setAttribute("inajax", (inajax != null) ? inajax : "0"); //ajax分页

            AdminUserDto adminUserDto = new AdminUserDto();
            adminUserDto.setUserName(username);
            adminUserDto.setMuId(XmoPayUtils.isEmpty(muid) ? null : Integer.valueOf(muid));

            //分页显示记录
            String index = request.getParameter("pageIndex");
            int pageIndex = XmoPayUtils.isEmpty(index) ? 1 : Integer.valueOf(index);
            int pageSize = 20;
            adminUserDto.setCurrentPage(pageIndex);
            adminUserDto.setPageSize(pageSize);

            SingleResult<PageInfo> adminUserListResult = adminUserService.getAdminUserList(adminUserDto);
            if (adminUserListResult.isSuccess()) {
                PageInfo page = adminUserListResult.getResult();
                request.setAttribute("page", page);
                request.setAttribute("lists", page.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, page, "listDiv"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admincp/admin.user.list";
    }

    /**
     * @param
     * @return
     * @Description: [页面]添加用户
     */
    @RequestMapping(value = "toEditUser")
    public String toEditUser(HttpServletRequest request) {
        String muid = request.getParameter("muid");
        request.setAttribute("muid", muid);
        if (XmoPayUtils.isEmpty(muid)) {  //添加
            request.setAttribute("mark", "to_add");
        } else {  //编辑
            SingleResult<AdminUserDto> adminUserResult = adminUserService.getAdminUserInfoByMuId(Integer.parseInt(muid));
            request.setAttribute("userMap", adminUserResult.getResult());
            request.setAttribute("mark", "to_edit");
        }

        //查询角色信息ADMIN_ROLE
        SingleResult<List<AdminRoleDto>> adminRoleListResult = adminRoleService.getAdminRoleList();
        if (adminRoleListResult.isSuccess()) {
            request.setAttribute("rolelist", adminRoleListResult.getResult());
        }
        return "admincp/admin.user.form";
    }

    /**
     * @param
     * @return
     * @Description: [Action]添加管理员
     */
    @ResponseBody
    @RequestMapping(value = "/addUser")
    public XmopayResponse<String> addUser(HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse(XmopayResponse.FAILURE, "添加管理员失败！", null);
        try {
            AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
            Integer sessionMuid = sessionUser.getMuId();
            //日志
            String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

            String muid = request.getParameter("muid");
            String roleId = request.getParameter("roleid");
            String username = request.getParameter("username");
            String repassword = request.getParameter("repassword");
            String oldpassword = request.getParameter("oldpassword");
            String status = request.getParameter("status");
            String bindIp = request.getParameter("bindIp");//财务报表查看时将在线支付拆开的权限：0 - 合并显示 1-分拆显示
            String salts = XmoPayUtils.genRandomNum(6);

            AdminUserDto adminParam = new AdminUserDto();
            adminParam.setRoleId(XmoPayUtils.isEmpty(roleId) ? null : Integer.valueOf(roleId));
            adminParam.setUserName(username);
            adminParam.setStatus(XmoPayUtils.isEmpty(status) ? null : Integer.valueOf(status));
            adminParam.setLastLogin(new Date());
            adminParam.setBindIp(bindIp);

            if (XmoPayUtils.isEmpty(muid)) { //添加管理员
                log.error(logmsg, "管理员ID参数为空!");
                adminParam.setPassword(XmoPayUtils.getCertifiedSigned(repassword, salts));
                adminParam.setSalts(salts);

                SingleResult<Integer> singleResult = adminUserService.addAdminUser(adminParam);
                if (singleResult.isSuccess()) {
                    log.info(logmsg, "增加管理员信息成功!");
                    //记录日志需要
                    resultResp.setResultMsg("添加管理员成功!");
                    resultResp.setResultCode(XmopayResponse.SUCCESS);
                    return resultResp;
                }
                log.error(logmsg, "增加管理员信息失败!");
                resultResp.setResultMsg("添加管理员失败!");
                return resultResp;
            } else { //编辑管理员
                if (!XmoPayUtils.isEmpty(repassword)) {
                    AdminUserDto adminUser = null;
                    SingleResult<AdminUserDto> result = adminUserService.getAdminUserInfoByMuId(XmoPayUtils.isEmpty(muid) ? null : Integer.valueOf(muid));
                    if (!result.isSuccess()) {
                        log.error(logmsg, "编辑的管理员不存在!");
                        resultResp.setResultMsg("编辑的管理员不存在!");
                        return resultResp;
                    }
                    adminUser = result.getResult();
                    String editsalts = adminUser.getSalts();
                    String originalPwd = adminUser.getPassword();
                    //原始密码不正确
                    if (!originalPwd.equals(XmoPayUtils.getCertifiedSigned(oldpassword, editsalts))) {
                        log.error(logmsg, "原始密码不正确!");
                        resultResp.setResultMsg("原始密码不正确!");
                        return resultResp;
                    }
                    adminParam.setPassword(XmoPayUtils.getCertifiedSigned(repassword, editsalts));
                }
                adminParam.setMuId(XmoPayUtils.isEmpty(muid) ? null : Integer.valueOf(muid));
                SingleResult<Integer> updateResult = adminUserService.updateAdminUser(adminParam);
                if (!updateResult.isSuccess()) {
                    log.error(logmsg, "编辑管理员失败!");
                    resultResp.setResultMsg("编辑管理员失败!");
                    return resultResp;
                }

                //记录日志需要
                log.info(logmsg, "编辑管理员成功!");
                resultResp.setResultMsg("编辑管理员成功!");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    // =========================================角色===============================================

    /**
     * @param
     * @return
     * @Description: [页面]管理员 角色列表首页
     */
    @RequestMapping(value = "/roleList")
    public String roleList(HttpServletRequest request) {
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        String inajax = request.getParameter("inajax"); //ajax模式
        //分页显示记录
        String index = request.getParameter("pageIndex");
        int pageIndex = XmoPayUtils.isEmpty(index) ? 1 : Integer.parseInt(index);//当前第一页
        int pageSize = 20;//每页显示的记录

        AdminRoleDto adminRoleDto = new AdminRoleDto();
        adminRoleDto.setCurrentPage(pageIndex);
        adminRoleDto.setPageSize(pageSize);
        adminRoleDto.setRoleName("超级管理员");//查询的角色列表包含超级管理员
        if (sessionUser.getRoleId() != 1) {
            SingleResult<AdminRoleDto> sessionAdminRoleResult = adminRoleService.getUserRole(sessionUser.getRoleId());
            if (sessionAdminRoleResult.isSuccess()) {
                adminRoleDto.setRoleName(sessionAdminRoleResult.getResult().getRoleName());
            }
        }

        SingleResult<PageInfo> singleResult = adminRoleService.getAdminRolePagesList(adminRoleDto);
        if (singleResult.isSuccess()) {
            PageInfo page = singleResult.getResult();
            request.setAttribute("page", page);
            request.setAttribute("lists", page.getList());
            request.setAttribute("pages", Pagesutils.showAjaxPage(request, page, "listDiv"));
        }
        request.setAttribute("inajax", (inajax != null) ? inajax : "0"); //ajax分页
        return new String("admincp/admin.role.list");
    }


    /**
     * @param
     * @return
     * @Description: [页面]添加角色
     */
    @RequestMapping(value = "toEditRole")
    public String toEditRole(HttpServletRequest request) {
        String rid = request.getParameter("rid");
        if (XmoPayUtils.isEmpty(rid)) {
            request.setAttribute("mark", "to_add");
        } else {
            SingleResult<AdminRoleDto> roleResult = adminRoleService.getUserRole(Integer.valueOf(rid));
            if (roleResult.isSuccess()) {
                request.setAttribute("roleMap", roleResult.getResult());
                request.setAttribute("mark", "to_edit");
            }
        }
        return new String("admincp/admin.role.form");
    }


    /**
     * @param
     * @return
     * @Description: [Action] 添加角色和修改角色，接收角色ID，为空则表示添加操作，不为空则为修改操作
     */
    @ResponseBody
    @RequestMapping(value = "addRole")
    public XmopayResponse<String> addRole(HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse<String>(XmopayResponse.FAILURE, "角色操作失败！", null);

        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid = sessionUser.getMuId();
        //日志
        String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        try {
            String rid = request.getParameter("rid");
            String rolename = request.getParameter("rolename");
            String roledesc = request.getParameter("roledesc");
            AdminRoleDto adminRoleParam = new AdminRoleDto();
            adminRoleParam.setRoleName(rolename);
            adminRoleParam.setRoleDesc(roledesc);
            adminRoleParam.setDateLine(new Date());

            if (XmoPayUtils.isEmpty(rid)) {  //增加
                log.error(logmsg, "角色ID为空，进行添加角色操作...");
                SingleResult<AdminRoleDto> validateResult = adminRoleService.getUserRoleByRoleName(rolename);
                if (validateResult.isSuccess()) {
                    log.error(logmsg, "添加角色失败：角色名字已经存在!");
                    resultResp.setResultMsg("添加角色失败：角色名字已经存在!");
                    return resultResp;
                }

                SingleResult<Integer> addResult = adminRoleService.addRole(adminRoleParam);
                if (!addResult.isSuccess()) {
                    log.error(logmsg, "添加角色失败!");
                    resultResp.setResultMsg("添加角色失败!");
                    return resultResp;
                }
                log.info(logmsg, "添加角色成功!");
                resultResp.setResultMsg("添加角色成功!");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;

            } else {   //修改
                adminRoleParam.setRid(Integer.valueOf(rid));
                SingleResult<Integer> updateResult = adminRoleService.updateRole(adminRoleParam);
                if (!updateResult.isSuccess()) {
                    log.error(logmsg, "更新角色失败!");
                    resultResp.setResultMsg("更新角色失败!");
                    return resultResp;
                }
                log.info(logmsg, "更新角色成功!");
                resultResp.setResultMsg("更新角色成功!");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }


    /**
     * @param
     * @return
     * @Description: [Action]删除角色
     */
    @ResponseBody
    @RequestMapping(value = "deleteRole")
    public XmopayResponse<String> deleteRole(HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse(XmopayResponse.FAILURE, "", null);
        String ids = request.getParameter("ids");
        try {
            AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
            Integer sessionMuid = sessionUser.getMuId();
            //日志
            String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

            SingleResult<Integer> deleteResult = adminRoleService.deleteRoleByIds(ids);
            if (deleteResult.isSuccess()) {
                log.info(logmsg, "删除角色成功!");
                resultResp.setResultMsg("删除角色成功!");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;
            } else {
                log.error(logmsg, "删除角色时出错!");
                resultResp.setResultMsg("删除角色时出错!");
                return resultResp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    // =========================================权限===============================================

    /**
     * @param
     * @return
     * @Description: [页面]根据角色赋菜单权限
     */
    @RequestMapping(value = "/toAuthMenu")
    public String toAuthMenu(HttpServletRequest request) {
        String rid = request.getParameter("rid");
        if (!XmoPayUtils.isEmpty(rid)) {
            SingleResult<AdminRoleDto> getResult = adminRoleService.getUserRole(Integer.valueOf(rid));
            if (getResult.isSuccess()) {
                request.setAttribute("roleMap", getResult.getResult());
            }
            SingleResult<List<AdminMenuDto>> menusResult = adminMenuService.getMenusList(); //所有菜单
            SingleResult<List<AdminMenuDto>> mResult = adminMenuService.findMenusByRoleId(Integer.parseInt(rid)); //当前角色所拥有的菜单

            request.setAttribute("menusList", menusResult.isSuccess() ? menusResult.getResult() : null);
            request.setAttribute("myMenusList", mResult.isSuccess() ? mResult.getResult() : null);
        }
        return "admincp/admin.menus.auth";
    }


    /**
     * @param
     * @return
     * @Description: [Action]给角色更新权限
     */
    @ResponseBody
    @RequestMapping(value = "addPrivilege")
    public XmopayResponse<String> addPrivilege(HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse(XmopayResponse.FAILURE, "", null);

        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid = sessionUser.getMuId();
        //日志
        String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        String menuIds = request.getParameter("menu_ids");  //已经选中菜单ids
        String roleId = request.getParameter("role_id");   //角色ID
        try {
            if (XmoPayUtils.isEmpty(menuIds) || ",".equals(menuIds)) {
                log.error(logmsg, "失败，未选中需要赋权的菜单栏，无需更新!");
                resultResp.setResultMsg("失败，未选中需要赋权的菜单栏，无需更新！");
                return resultResp;
            }
            if (XmoPayUtils.isEmpty(roleId)) {
                log.error(logmsg, "失败，角色为空，无需更新!");
                resultResp.setResultMsg("失败，角色为空，无需更新！");
                return resultResp;
            }

            //删除当前角色下所有菜单栏关系
            AdminAuthDto delAuth = new AdminAuthDto();
            delAuth.setRoleId(Integer.valueOf(roleId));
            adminAuthService.deleteAuth(delAuth);
            log.error(logmsg, "删除当前角色下所有菜单栏关系!");
            //添加关联权限表
            String[] menu_ids_ = menuIds.split(",");
            for (String menu_id : menu_ids_) {
                if (XmoPayUtils.isEmpty(menu_id)) {
                    continue;
                }

                AdminAuthDto addAuth = new AdminAuthDto();
                addAuth.setRoleId(Integer.valueOf(roleId));
                addAuth.setMenuId(Integer.valueOf(menu_id));
                adminAuthService.addAuth(addAuth);
            }
            log.info(logmsg, "更新菜单权限成功!");
            resultResp.setResultMsg("更新菜单权限成功!");
            resultResp.setResultCode(XmopayResponse.SUCCESS);
            return resultResp;
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    // =========================================菜单===============================================

    /**
     * @param
     * @return
     * @Description: [页面]菜单列表===添加菜单
     */
    @RequestMapping(value = "/ztreeMenus")
    public String ztreeMenus(HttpServletRequest request) {
        SingleResult<List<AdminMenuDto>> menuResult = adminMenuService.getParentMenus();

        request.setAttribute("parentMenu", menuResult.isSuccess() ? menuResult.getResult() : null);//父节点
        SingleResult<List<AdminMenuDto>> allResult = adminMenuService.getMenusList();

        request.setAttribute("seedMenu", allResult.isSuccess() ? allResult.getResult() : null);//所有节点
        return "admincp/admin.menus.ztreeform";
    }

    /**
     * @param
     * @return
     * @Description: [Action]删除系统管理菜单
     */
    @ResponseBody
    @RequestMapping(value = "/delZtreeById")
    public XmopayResponse<Map> delZtreeById(HttpServletRequest request) {
        XmopayResponse<Map> resultResp = new XmopayResponse(XmopayResponse.FAILURE, "", null);
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid = sessionUser.getMuId();
        //日志
        String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        try {
            Map resultMap = new HashMap();
            String mmid = request.getParameter("mid");
            SingleResult<AdminMenuDto> singleResult = adminMenuService.findMenusByMId(Integer.parseInt(mmid));
            if (!singleResult.isSuccess()) {
                log.error(logmsg, "删除系统管理菜单失败，该菜单不存在!");
                resultResp.setResultMsg("该菜单不存在！");
                return resultResp;
            }
            AdminMenuDto adminMenuDto = singleResult.getResult();
            resultMap.put("menusMap", adminMenuDto);

            //查询下级菜单
            AdminMenuDto adminMenus = new AdminMenuDto();
            adminMenus.setMenuParent(Integer.parseInt(mmid));
            SingleResult<List<AdminMenuDto>> findSeedMenuResult = adminMenuService.getAdminMenusList(adminMenus);

            if (findSeedMenuResult.isSuccess()) {
                log.error(logmsg, "该菜单还有子菜单，请先删除子菜单!");
                resultResp.setResultMsg("该菜单还有子菜单，请先删除子菜单！");
                return resultResp;
            }

            SingleResult<Integer> delMenuResult = adminMenuService.deleteAdminMenu(adminMenuDto);
            if (delMenuResult.isSuccess()) {
                log.info(logmsg, "菜单删除成功!");
                resultResp.setResultData(resultMap);
                resultResp.setResultMsg("菜单删除成功！");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;
            }
            log.error(logmsg, "菜单删除失败!");
            resultResp.setResultData(resultMap);
            resultResp.setResultMsg("菜单删除失败！");
            return resultResp;

        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    /**
     * 获取单个菜单的信息
     */
    @ResponseBody
    @RequestMapping(value = "/getZtreeById")
    public XmopayResponse<AdminMenuDto> getZtreeById(HttpServletRequest request) {
        XmopayResponse<AdminMenuDto> resultResp = new XmopayResponse(XmopayResponse.FAILURE, "", null);
        try {
            String mid = request.getParameter("mid");
            SingleResult<AdminMenuDto> menuResult = adminMenuService.findMenusByMId(XmoPayUtils.isEmpty(mid) ? null : Integer.parseInt(mid));//.findByMenusId(mid);
            if (!menuResult.isSuccess()) {
                //失败也就是没有数据
                resultResp.setResultMsg("菜单不存在!");
                return resultResp;
            }
            resultResp.setResultMsg("获取到菜单!");
            resultResp.setResultCode(XmopayResponse.SUCCESS);
            resultResp.setResultData(menuResult.getResult());
            return resultResp;

        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    /**
     * 执行添加菜单
     */
    @ResponseBody
    @RequestMapping(value = "/updateMenus")
    public XmopayResponse<String> updateMenus(HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse(XmopayResponse.FAILURE, "", null);
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid = sessionUser.getMuId();
        //日志
        String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        String menuId = request.getParameter("menu_id");
        String menuCode = request.getParameter("menu_code");
        String menuName = request.getParameter("menu_name");
        String menuFlag = request.getParameter("menu_flag");
        String menuDesc = request.getParameter("menu_desc");
        String menuParent = request.getParameter("menu_parent");
        String menuUrl = request.getParameter("menu_url");
        String displayOrder = request.getParameter("displayorder");
        String status = request.getParameter("status");

        AdminMenuDto reqMenu = new AdminMenuDto();
        reqMenu.setMenuCode(menuCode);
        reqMenu.setMenuName(menuName);
        reqMenu.setMenuDesc(menuDesc);

        if ("0".equals(menuFlag)) {
            reqMenu.setMenuParent(0);
            menuUrl = "";
        } else {
            reqMenu.setMenuParent(XmoPayUtils.isEmpty(menuParent) ? null : Integer.valueOf(menuParent));
        }
        reqMenu.setMenuUrl(menuUrl);
        reqMenu.setMenuFlag(XmoPayUtils.isEmpty(menuFlag) ? null : Integer.valueOf(menuFlag));
        reqMenu.setDisplayOrder(XmoPayUtils.isEmpty(displayOrder) ? null : Integer.valueOf(displayOrder));
        reqMenu.setStatus(XmoPayUtils.isEmpty(status) ? null : Integer.valueOf(status));

        try {
            int n = 0;
            if (XmoPayUtils.isEmpty(menuId)) {//添加

                if (!XmoPayUtils.isEmpty(menuUrl)) { //再判断下要添加的菜单URL是否存在
                    AdminMenuDto menuParam = new AdminMenuDto();
                    menuParam.setMenuUrl(menuUrl);
                    SingleResult<AdminMenuDto> menuResult = adminMenuService.ajaxByMenusValidate(menuParam);
                    if (!menuResult.isSuccess()) {
                        log.error(logmsg, menuUrl + "未查询到此菜单记录!");
                        //数据库没有就添加
                        SingleResult<Integer> addResult = adminMenuService.addMenus(reqMenu);
                        n = addResult.isSuccess() ? 1 : addResult.getResult();
                    }
//                    n = 0;
                } else {
                    SingleResult<Integer> addResult = adminMenuService.addMenus(reqMenu);
                    n = addResult.isSuccess() ? 1 : 0;
                }
            } else {//修改
                reqMenu.setMenuId(XmoPayUtils.isEmpty(menuId) ? null : Integer.valueOf(menuId));
                SingleResult<Integer> updateResult = adminMenuService.updateMenu(reqMenu);
                n = updateResult.isSuccess() ? 1 : 0;
            }
            if (n == 0) {
                log.error(logmsg, "添加菜单失败!");
                resultResp.setResultMsg("失败!");
                return resultResp;
            } else {
                log.info(logmsg, "添加菜单成功!");
                resultResp.setResultMsg("成功!");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }
}
