package com.xmopay.demo.controller;

import com.xmopay.demo.common.DemoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@Controller
@RequestMapping(value = "notify")
@Slf4j
public class NotifyController {

    /**
     * 商户异步处理接收
     */
    @RequestMapping(value = "/async")
    public void async(HttpServletRequest request, HttpServletResponse resp) {
        try {
            PrintWriter out = resp.getWriter();

            Map requestParams = DemoUtils.formRequestMap(request.getParameterMap());
            log.info("接收异步参数========>>>>"+requestParams);

            out.print("SUCCESS");
            out.flush();
            out.close();
            return;
        }catch (Exception e){
            e.printStackTrace();
        }
        return;
    }
}
