package com.xmopay.openapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xmopay.common.model.CallbackRequestModel;
import com.xmopay.common.utils.StringUtils;
import com.xmopay.common.utils.XmoPayUtils;
import com.xmopay.openapi.core.ChannelRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

@Controller
@RequestMapping(value = "notify")
public class NotifyController {
    private static Logger logger = LoggerFactory.getLogger("notify_info");

    /**
     * 接口列表
     */
    @Value("${payment.callback.api-names:aliPay|wxPay}")
    private String apiNames;

    @Autowired
    private ChannelRemote channelRemote;

    /**
     * 异步处理method=RequestMethod.POST
     */
    @RequestMapping(value = "/async/{gateway}", method = RequestMethod.POST)
    public void async(@PathVariable(value = "gateway") String gateway, HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String returnCode = "";
            // #0 监听异步地址 范围
            if (!StringUtils.findWordInString(apiNames, gateway, "|")) {
                logger.info("[上游交易异步通知 => XMOPAY]: 来自网关参数gateway={}, 跳出交易异步处理逻辑，请通知技术部门进行代码检查!", gateway);
                out.print(returnCode);
                out.flush();
                out.close();
                return;
            }

            // #1 转换参数
            Map requestParams = null;
            if ("wxPay".equals(gateway)) {
                InputStream inputStream;
                StringBuffer content = new StringBuffer();
                inputStream = request.getInputStream();
                String s = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((s = in.readLine()) != null) {
                    content.append(s);
                }
                in.close();
                inputStream.close();
                logger.info("[上游交易异步通知 => XMOPAY]: 来自网关参数gateway={}, 响应参数={}", gateway, content.toString());
                requestParams = XmoPayUtils.xmlToMap(content.toString());
            } else {
                requestParams = XmoPayUtils.formRequestMap(request.getParameterMap());
            }

            if (requestParams == null || requestParams.size() <= 0) {
                logger.info("[上游交易异步通知 => XMOPAY]: 来自网关参数gateway={}, requestParams 为null，结束异步接收!", gateway);
                out.print(returnCode);
                out.flush();
                out.close();
                return;
            }

            String requestIp = XmoPayUtils.getIpAddr(request);
            CallbackRequestModel callbackRequestModel = new CallbackRequestModel();
            callbackRequestModel.setRequestIp(requestIp);
            callbackRequestModel.setBody(JSONObject.toJSONString(requestParams));
            callbackRequestModel.setChannelCode(gateway + "ChannelServiceImpl");
            logger.info("[上游交易异步通知 => XMOPAY]: 来自来自上游网关[{}]异步通知, 请求IP={}, Request={}, 进入接口异步处理逻辑...", gateway, requestIp, requestParams);
            // #2 调用支付接口
            String ret = channelRemote.doPaymentAsyncCallback(callbackRequestModel);
            if (ret == null) {
                logger.info("[上游交易异步通知 => XMOPAY]: 来自未知网关参数[{}], 支付接口类不存在!", gateway);
                out.print(returnCode);
                out.flush();
                out.close();
                return;
            }

            Map retMap = JSON.parseObject(ret, Map.class);
            if (!"RESPONSE_SUCCESS".equals(retMap.getOrDefault("return_code", ""))) {
                logger.info("[上游交易异步通知 => XMOPAY]: 渠道接口异步处理失败，" +
                        "return_code={}, return_message={}",
                        retMap.get("return_code"), retMap.get("return_message"));
                out.print(returnCode);
                out.flush();
                out.close();
                return;
            }

            logger.info("[上游交易异步通知 => XMOPAY]: 渠道接口异步处理成功，" +
                    "return_code={}, return_message={}, return_code={}",
                    retMap.get("return_code"), retMap.get("return_message"), retMap.get("body"));
            out.print(retMap.getOrDefault("body", "fail"));
            out.flush();
            out.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
