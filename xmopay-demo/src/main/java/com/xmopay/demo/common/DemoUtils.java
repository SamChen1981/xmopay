package com.xmopay.demo.common;

import lombok.experimental.var;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class DemoUtils {

    public static boolean isEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0 || value.trim().length() ==0 ) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将POST过来反馈信息转换一下
     * @param requestParams 返回参数信息
     * @return Map 返回一个只有字符串值的MAP
     */
    public static Map formRequestMap(Map requestParams) {
        Map params = null;
        if (requestParams != null && requestParams.size() > 0) {
            params = new HashMap();
            String name = "";
            String[] values = null;
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                name = (String) iter.next();
                values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                params.put(name, clearString(valueStr));
            }
        }
        return params;
    }

    public static String clearString(String param) {
        if (param == null || "".equals(param.trim()) || "null".equals(param.trim())) {
            return null;
        } else
            return param.trim();
    }

    /**
     * 获取订单号
     * @return
     */
    public static String buildOrdersn() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String key = sdf.format(new Date());
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < 4; i++) {
            num = num * 10;
        }
        return key + String.valueOf((int) (random * num));
    }

    /**
     * 构造提交表单HTML数据
     * @param sParaTemp 请求参数数组
     * @param actionUrl 请求地址
     * @param strMethod 提交方式。两个值可选：post、get
     * @return 提交表单HTML文本
     */
    public static String buildForm(Map<String, String> sParaTemp, String actionUrl, String strMethod) {
        List<String> keys = new ArrayList<String>(sParaTemp.keySet());

        StringBuffer sbHtml = new StringBuffer();

        sbHtml.append("<form target=\"_self\" id=\"paysubmit\" name=\"paysubmit\" action=\"" + actionUrl + "\" method=\"" + strMethod + "\">");

        for (int i = 0; i < keys.size(); i++) {
            String name = keys.get(i);
            String value = sParaTemp.get(name);
            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
        }

        // submit按钮控件请不要含有name属性
        sbHtml.append("<input type=\"submit\" value=\"btnsubmit\" style=\"display:none;\"></form>");
        sbHtml.append("<script>document.forms['paysubmit'].submit();</script>");

        return sbHtml.toString();
    }

    /**
     * 重定向页面[跳转至上游银行]
     * @param response
     * @param msg
     */
    public static void redirectToBank(HttpServletResponse response, String msg) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            String tipsBoxHtml = "<div class=\"container\">";
            tipsBoxHtml += "<div class=\"col-xs-12 col-sm-12 col-md-12\">";
            tipsBoxHtml += "<div class=\"pay-boxs\">";
            tipsBoxHtml += "<h4> <img src=\"/static/images/loading.gif\" width=\"18\"> 正在跳转至银行支付页面，请稍候...</h4><br>";
            tipsBoxHtml += "<p class=\"small\">提示：由于网络原因，到达银行页面可能需要点时间，请不要关闭窗口！</p>";
            tipsBoxHtml += "<p class=\"small\">Now is jumping to the bank page, Please wait for a moment. Don't close the window.</p>";
            tipsBoxHtml += "</div>";
            tipsBoxHtml += "</div>";
            tipsBoxHtml += "</div>";

            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"zh-CN\">");
            out.println("<head>");
            out.println("<meta charset=\"utf-8\" />");
            out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />");
            out.println("<title>Online for payment is jumping to the bank page, please wait...</title>");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
//            out.println("<link rel=\"shortcut icon\" href=\"/default/assets/images/favicon.ico\">");
            out.println("<link rel=\"stylesheet\" href=\"/static/plugins/bootstrap/css/bootstrap.min.css\">");
            out.println("</head>");
            out.println("</body>");
            out.print(tipsBoxHtml);//跳转loading提示框
            out.print(msg);//表单信息
            out.println("</body>");
            out.println("</html>");
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重定向二维码页面
     * @param response
     * @param msg
     */
    public static void redirectToQRHTML(HttpServletResponse response, String msg, String payTitle) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            String tipsBoxHtml = "<div class=\"container\">";
            tipsBoxHtml += "<div class=\"col-xs-12 col-sm-12 col-md-12\">";
            tipsBoxHtml += "<div class=\"pay-boxs\">";
            tipsBoxHtml += "<h4> 请使用"+payTitle+"手机客户端扫一扫</h4><br>";
            tipsBoxHtml += "<p class=\"small\">提示：二维码为一次性，请不要关闭窗口！</p>";
            tipsBoxHtml += "<p class=\"small\">QR code is one time. Don't close the window.</p>";
            tipsBoxHtml += "<div id=\"qrcode\"></div>";
            tipsBoxHtml += "</div>";
            tipsBoxHtml += "</div>";
            tipsBoxHtml += "</div>";

            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"zh-CN\">");
            out.println("<head>");
            out.println("<meta charset=\"utf-8\" />");
            out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />");
            out.println("<title>Online for payment is jumping to the bank page, please wait...</title>");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
            out.println("<link rel=\"stylesheet\" href=\"/static/plugins/bootstrap/css/bootstrap.min.css\">");
            out.println("</head>");
            out.println("<body>");
            out.print(tipsBoxHtml);//跳转loading提示框
//            out.print(msg);//表单信息
            out.println("<script src=\"/static/js/jquery.min.js\"></script>");
            out.println("<script src=\"/static/js/qrcode.min.js\"></script>");
            out.println("<script src=\"/static/plugins/bootstrap/js/bootstrap.min.js\"></script>");
            out.println("</body>");
            out.println("</html>");

            //JS
            out.println("<script>");
            out.println("var qrcode = new QRCode(document.getElementById(\"qrcode\"), {width : 200, height : 200});");
            out.println("qrcode.makeCode(\""+msg+"\")");
            out.println("</script>");

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提示页面
     * @param response
     * @param msg
     */
    public static void redirectHtml(HttpServletResponse response, String msg) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            out.println("<HTML>");
            out.println("  <HEAD><TITLE>The Online Payment...</TITLE></HEAD>");
            out.println("  <BODY>");
            out.print(msg);
            out.println("  </BODY>");
            out.println("</HTML>");
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
