package com.xmopay.common.filter;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * com.xmopay.filter
 *
 * @author monica.
 * @date 4:05 PM, 2018/4/25
 */
@WebServlet(urlPatterns="/druid/*",
        initParams={
                @WebInitParam(name="allow",value=""),// IP白名单(没有配置或者为空，则允许所有访问)
                @WebInitParam(name="deny",value=""),// IP黑名单 (deny优先于allow)
                @WebInitParam(name="loginUsername",value="sa"),// 登录druid管理页面用户名
                @WebInitParam(name="loginPassword",value="1d85138a")// 登录druid管理页面密码
        })
public class DruidServlet extends StatViewServlet {
}
