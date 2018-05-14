package com.xmopay.demo.common;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("base", request.getRequestURL().toString().replace(request.getRequestURI(), "") + request.getContextPath() + "/");

        return true;
    }
}
