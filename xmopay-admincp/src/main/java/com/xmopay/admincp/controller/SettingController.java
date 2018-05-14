package com.xmopay.admincp.controller;

import com.alibaba.fastjson.JSON;
import com.xmopay.admincp.common.RedisCacheUtils;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.common.WebCommon;
import com.xmopay.admincp.common.XmopayResponse;
import com.xmopay.admincp.dto.AdminUserDto;
import com.xmopay.admincp.dto.SettingDto;
import com.xmopay.admincp.service.AdminUserService;
import com.xmopay.admincp.service.SettingService;
import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.common.utils.XmoPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "settings")
@Slf4j
public class SettingController {
    
    @Autowired
    private SettingService settingService;

    /**
     * @param
     * @Description: 系统管理 - 基本设置
     * @return
     */
    @RequestMapping(value = "/basic")
    public String basic(HttpServletRequest request) {
        try {
            Map map = new HashMap();
            //展示
            SingleResult<List<SettingDto>> singleResult = settingService.getSettingsList();
            if(singleResult.isSuccess()){
                List<SettingDto> settingList = singleResult.getResult();
                for(SettingDto settingInfo : settingList){
                    map.put(settingInfo.getKey(), settingInfo.getValue());
                }
            }
            request.setAttribute("map", map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "settings/basic";
    }

    /**
     * @param
     * @Description: [Action]系统管理 - 修改基本设置
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editBasic")
    public XmopayResponse<String> editBasic(HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse<>(XmopayResponse.FAILURE,"",null);
        try {
            AdminUserDto user = WebCommon.getSessionUserInfo(request);
            Integer sessionMuid = user.getMuId();
            //日志
            String logmsg = "[用户常规事件] 当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

            String serverName = request.getServerName();
            String type       = request.getParameter("type");//0展示 1添加

            if (!XmoPayUtils.isEmpty(type) && "1".equals(type.trim())) {
                String sitename      = request.getParameter("sitename");
                String copyright     = request.getParameter("copyright");
                String systemNotice  = request.getParameter("system_notice");
                String ipWhiteList    = request.getParameter("ip_white_list"); //回调白名单
                String appskin        = request.getParameter("appskin");//应用名
                String appname        = request.getParameter("appname");//应用名
                String appdomain      = request.getParameter("appdomain");//应用域名
                String apiUrl         = request.getParameter("api_url");//应用域名
                String apiTestParams  = request.getParameter("api_test_params");
                String apiSmsParams   = request.getParameter("api_sms_params");
                String apiSmsParamsZh = request.getParameter("api_sms_params_zh");
                String spiEmailParams = request.getParameter("api_email_params");

                Map map = new HashMap();
                map.put("sitename",          sitename);
                map.put("copyright",         copyright);
                map.put("system_notice",     systemNotice);
                map.put("ip_white_list",     ipWhiteList.replace("，", ","));
                map.put("appskin",           appskin);
                map.put("appname",           appname);
                map.put("appdomain",         appdomain);
                map.put("api_url",           apiUrl);
                map.put("api_test_params",   apiTestParams);
                map.put("api_sms_params",    apiSmsParams);
                map.put("api_sms_params_zh", apiSmsParamsZh);
                map.put("api_email_params",  spiEmailParams);

                List<String> keys = new ArrayList<String>(map.keySet()); //???
                for (String key : keys) {
                    String value = (String) map.get(key);
                    SettingDto settingDto = new SettingDto();
                    settingDto.setKey(key);
                    settingDto.setValue(value);
                    SingleResult<Integer> singleResult = settingService.replaceIntoSettings(settingDto);
                    if(!singleResult.isSuccess()){
                        log.error(logmsg, "保存失败！");
                        resultResp.setResultMsg("保存失败");
                        return resultResp;
                    }
                }

                Map jsonMap = new HashMap();
                jsonMap.put("sitename",      sitename);
                jsonMap.put("copyright",     copyright);
                jsonMap.put("system_notice", systemNotice);
                jsonMap.put("ip_white_list", ipWhiteList);
                jsonMap.put("appskin",       appskin);
                jsonMap.put("appname",       appname);
                jsonMap.put("appdomain",     appdomain);
                jsonMap.put("api_url",       apiUrl);

                RedisCacheUtils.cacheSet(serverName + XmoPayConstants.CONFIGSET, JSON.toJSONString(jsonMap));
            }
            log.info(logmsg, "保存成功！");
            resultResp.setResultCode(XmopayResponse.SUCCESS);
            resultResp.setResultMsg("保存成功");
            return resultResp;
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            resultResp.setResultMsg("系统异常，请联系客服！");
            return resultResp;
        }

    }

}
