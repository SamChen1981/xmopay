package com.xmopay.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xmopay.common.utils.SignUtils;
import com.xmopay.common.utils.http.HttpClientUtils;
import com.xmopay.demo.common.DemoUtils;
import com.xmopay.demo.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "recharge")
@Slf4j
public class RechargeController {

    /**
     * @Description: 统一扫码
     * @param:
     * @author: geek
     * @return:
     */
    @RequestMapping(value = "/qrPay")
    public String qrPay(HttpServletRequest request) {
        return "index";
    }

    @RequestMapping(value = "/doPay")
    public void doQrPay(HttpServletRequest request, HttpServletResponse response) {
        try {
            String amount = request.getParameter("amount");
            String bankCode = request.getParameter("bank_code");
            if(DemoUtils.isEmpty(amount) || DemoUtils.isEmpty(bankCode)){
                DemoUtils.redirectHtml(response, "订单参数为空");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String outTradeNo = DemoUtils.buildOrdersn();
            String inputCharset = "UTF-8";
            String returnUrl = "http://www.none.com";
            String notifyUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "") + request.getContextPath() + "/notify/async";

            String pay_type = null;
            if(bankCode.indexOf("ALIPAY")>-1 || bankCode.indexOf("WXPAY")>-1){
                pay_type = "SCAN_PAY";
            }else{
                pay_type = "BANK_PAY";
            }

            Map<String, String> params = new HashMap<>(14);
            params.put("interface_name", "XMO_ONLINE_PAY");
            params.put("production_description", "订单描述");
            params.put("bank_code", bankCode);
            params.put("amount", amount);
            params.put("pay_type", pay_type);
            params.put("notify_url", notifyUrl);
            params.put("return_url", returnUrl);
            params.put("version", "V1.0");
            params.put("partner_id", Constants.partner_id);
            params.put("sign_type", "MD5");
            params.put("out_trade_no", "E0S" + outTradeNo);
            params.put("input_charset", inputCharset);
            params.put("memo", "备注");
            params.put("summary", "订单标题-测试单");
            params.put("out_trade_time", sdf.format(new Date()));

            String sign = SignUtils.signString(params, Constants.partnerMd5Key);
            params.put("sign", sign);

//            log.info("1 params ========>>>>>>>"+params);
//            String payFromStr = "{\"respCode\":\"RESPONSE_SUCCESS\",\"respMessage\":\"请求响应成功\",\"resp_result\":{\"qr_url\":\"weixin://wxpay/bizpayurl?pr=OrHbFWa\",\"order_sn\":\"1805071614143435185\"}}";
//            String payFromStr = "{\"respCode\":\"RESPONSE_SUCCESS\",\"respMessage\":\"请求响应成功\",\"resp_result\":{\"qr_url\":\"https://qr.alipay.com/bax08056inalvfqbwjwm00b1\",\"order_sn\":\"1805071629115723746\"}}";

            if(pay_type.equals("SCAN_PAY")){
                //进行HTTP请求
                String payResultStr = HttpClientUtils.rsyncPost( Constants.gatawayURL, params );
                log.info("2 payFromStr ========>>>>>>>"+payResultStr);

                if(payResultStr.indexOf("<form")>-1) {
                    DemoUtils.redirectHtml(response, "网关调用失败!");
                    return;
                }

                JSONObject json = JSON.parseObject(payResultStr);
                String respCode = json.getString("respCode");
                if(!respCode.equals("RESPONSE_SUCCESS")){
                    DemoUtils.redirectHtml(response, json.getString("respMessage"));
                }
                JSONObject resp_result = json.getJSONObject("resp_result");
                String qr_url = resp_result.getString("qr_url");

                String payTitle = "微信";
                if (qr_url.indexOf("qr.alipay") > -1) {
                    payTitle = "支付宝";
                }
                if (qr_url.indexOf("qpay.qq.com") > -1) {
                    payTitle = "QQ";
                }
                DemoUtils.redirectToQRHTML(response, qr_url, payTitle);
            }else {
                String payFromStr = DemoUtils.buildForm(params, Constants.gatawayURL, "POST");

                log.info("2 payFromStr ========>>>>>>>"+payFromStr);

                DemoUtils.redirectToBank(response, payFromStr);
            }
        }catch (Exception e){
            e.printStackTrace();
            DemoUtils.redirectHtml(response, "系统错误!");
            return;
        }
    }

    /**
     * @Description: 网关支付
     * @param:
     * @return:
     */
    @RequestMapping(value = "/payment")
    public String payment(HttpServletRequest request) {
        return "payment";
    }

}
