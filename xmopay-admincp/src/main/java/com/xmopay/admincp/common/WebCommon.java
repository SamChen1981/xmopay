package com.xmopay.admincp.common;

import com.xmopay.admincp.dto.AdminUserDto;
import com.xmopay.common.constant.XmoPayConstants;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebCommon {

    /**
     * 后台会员Session信息
     * @return
     */
    public static AdminUserDto getSessionUserInfo(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (AdminUserDto) session.getAttribute(XmoPayConstants.SYSADMIN_SESSION);
    }

    /**
     * 判断现在是否是中文版本
     * @param request
     * @return 中文版本返回true
     */
    public static boolean languageIsCN(HttpServletRequest request){
        //获取当前默认的语言
        HttpSession session = request.getSession();
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        session.setAttribute("languageName", localeResolver.resolveLocale(request));
        String languageName = session.getAttribute("languageName").toString();
        boolean languageIsCN = true;
        if("en_US".equals(languageName)){
            languageIsCN = false;   //英文网页
        }
        return languageIsCN;
    }

}
