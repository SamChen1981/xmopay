package com.xmopay.admincp.common;

import com.xmopay.admincp.dto.AdminUserDto;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorInterceptor implements HandlerInterceptor {

    private final String[] SYSADMIN_PASS_PATH = new String[]{"login", "adlogin", "checkGoogleCode", ".js", ".css"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("base", request.getRequestURL().toString().replace(request.getRequestURI(), "") + request.getContextPath() + "/");

        //登陆不做拦截
        String requestUri = request.getRequestURI();
        boolean doFilter = true;
        for (String methods : SYSADMIN_PASS_PATH) {
            if (requestUri.indexOf(methods) != -1) {
                doFilter = false;
                break;
            }
        }
        if (doFilter) {
            AdminUserDto user = WebCommon.getSessionUserInfo(request);
            if(user == null) {
                response.sendRedirect("/user/login");
                return false;
            }
        }
        return true;
    }
}
