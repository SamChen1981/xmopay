package com.xmopay.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 字符处理帮助类
 */
public class XmoPayUtils {
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * request header信息
     *
     * @param request
     * @return
     */
    public static Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    public static String clearString(String param) {
        if (param == null || "".equals(param.trim()) || "null".equals(param.trim())) {
            return null;
        } else
            return param.trim();
    }

    /**
     * json字符串转map
     *
     * @param json
     * @return
     */
    public static Map<String, Object> jsonToMap(String json) {
        try {
            return JSON.parseObject(json, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isNull(Object obj) {
        return null == obj;
    }

    /**
     * 检查指定的字符串是否为空。
     * <ul>
     * <li>SysUtils.isEmpty(null) = true</li>
     * <li>SysUtils.isEmpty("") = true</li>
     * <li>SysUtils.isEmpty("   ") = true</li>
     * <li>SysUtils.isEmpty("abc") = false</li>
     * </ul>
     *
     * @param value 待检查的字符串
     * @return true/false
     */
    public static boolean isEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0 || value.trim().length() == 0) {
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
     * 检查对象是否为数字型字符串,包含负数开头的。
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        //正则表达式：验证数字
        String REGEX_NUM = "^[0-9]+(.[0-9]+)?$";
        return Pattern.matches(REGEX_NUM, str);
    }

    /**
     * 判断集合是否存在null
     *
     * @param obj
     * @return
     */
    public static boolean isCollectionNull(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return (null == map || 0 == map.size());
        } else if (obj instanceof List) {
            List list = (List) obj;
            return (null == list || 0 == list.size());
        } else if (obj instanceof Set) {
            Set set = (Set) obj;
            return (null == set || 0 == set.size());
        }
        return true;
    }

    /**
     * 把通用字符编码的字符串转化为汉字编码。
     */
    public static String unicodeToChinese(String unicode) {
        StringBuilder out = new StringBuilder();
        if (!isEmpty(unicode)) {
            for (int i = 0; i < unicode.length(); i++) {
                out.append(unicode.charAt(i));
            }
        }
        return out.toString();
    }

    /**
     * 过滤不可见字符
     */
    public static String stripNonValidXMLCharacters(String input) {
        if (input == null || ("".equals(input)))
            return "";
        StringBuilder out = new StringBuilder();
        char current;
        for (int i = 0; i < input.length(); i++) {
            current = input.charAt(i);
            if ((current == 0x9) || (current == 0xA) || (current == 0xD)
                    || ((current >= 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD))
                    || ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }

    /**
     * 密码加密处理-
     *
     * @param pwd
     * @param salts
     * @return
     */
    public static String getCertifiedSigned(String pwd, String salts) throws Exception {
        String getCertSign = MD5(pwd + salts);
        return getCertSign;
    }

    /**
     * 获取订单号
     *
     * @return
     */
    public static String buildOrdersn() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
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
     * 获取流水号
     *
     * @return
     */
    public static String buildPaysn() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String key = sdf.format(new Date());
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < 6; i++) {
            num = num * 10;
        }
        return key + String.valueOf((int) (random * num));
    }

    /**
     * 对字符串采用UTF-8编码后，用MD5进行摘要。
     */
    public static String MD5(String data) throws IOException {
        String md5Str = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(data.getBytes(CHARSET_UTF8));
            md5Str = byte2hex(bytes);
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return md5Str;
    }

    /**
     * 把字节流转换为十六进制表示方式。
     */
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex); //.toUpperCase()
        }
        return sign.toString();
    }

    /**
     * 获取6为数字和字码的混合码
     *
     * @return
     */
    public static String genRandomNum(int ran_length) {
        final int maxNum = 36;
        char[] strs = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        int count = 0;
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        while (count < ran_length) {
            int i = Math.abs(random.nextInt(maxNum));
            buffer.append(strs[i]);
            count++;
        }
        return buffer.toString();
    }

    /**
     * 获取ran_length个为数字的随机数字
     *
     * @param ran_length
     * @return
     */
    public static String getRandomNum(int ran_length) {
        String str;
        StringBuffer sb = new StringBuffer();
        Random random1 = new Random();
        for (int i = 0; i < ran_length; i++) {
            String rand = String.valueOf(random1.nextInt(10));//全数字
            sb.append(rand);
        }
        str = sb.toString();
        return str;
    }

    /**
     * 生成随机密码
     *
     * @return
     */
    public static String generatePassword() {
        String[] pa = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < 8; i++) {
            sb.append(pa[(Double.valueOf(Math.random() * pa.length).intValue())]);
        }
        String[] spe = {"`", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "=", "+", "[", "]", "{", "}", "\\", "/", "?", "<", ">"};
        sb.append(spe[(Double.valueOf(Math.random() * spe.length).intValue())]);
        sb.append((int) (Math.random() * 100));
        return sb.toString();
    }

    /**
     * 创建随机数
     *
     * @param length
     * @return
     */
    public static Long buildRandom(Long length) {
        long num = 1L;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (long) ((random * num));
    }

    //解析XML字符
    public static Element dom4jParseXML(String xml) {
        try {
            if (null == xml || 0 == xml.length()) {
                return null;
            }
            Document doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            return doc.getRootElement(); // 获取根节点
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Map转换成Xml
     *
     * @param map
     * @return
     */
    public static String mapToXmlstring(String rootName, Map<String, Object> map) {
        StringBuffer sb = new StringBuffer("");
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sb.append("<" + rootName + ">");

        Set<String> set = map.keySet();
        for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
            String key = it.next();
            Object value = map.get(key);
            sb.append("<").append(key).append(">");
            sb.append(value);
            sb.append("</").append(key).append(">");
        }
        sb.append("</" + rootName + ">");
        sb.append("</xml>");
        return sb.toString();
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) { //"***.***.***.***".length() = 15
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 字符串拼接
     *
     * @param params
     * @return
     */
    public static String createLinkString(Map params) {
        List keys = new ArrayList(params.keySet());
        Collections.sort(keys);

        StringBuilder prestr = new StringBuilder();
        String key = "";
        String value = "";
        for (int i = 0; i < keys.size(); i++) {
            key = (String) keys.get(i);
            value = (String) params.get(key);
            if ("".equals(value) || "null".equals(value) || value == null) {
                continue;
            } else {
                if (key.equalsIgnoreCase("callbackIp") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")) {
                    continue;
                }
                prestr.append(key).append("=").append(value).append("&");
            }
        }

        return prestr.length() > 0 ? prestr.deleteCharAt(prestr.length() - 1).toString() : "";


    }

    public static void webAjax(String result, String msg, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            JSONObject json = new JSONObject();
            json.put("result", result);
            json.put("msg", msg);
            response.setCharacterEncoding("utf-8");
            json.writeJSONString(response.getWriter());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拼装json提示结果
     *
     * @param json
     * @param result
     * @param msg
     */
    public static void retJsonMsg(JSONObject json, String result, String msg, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            json.put("result", result);
            json.put("msg", msg);
            response.setCharacterEncoding("utf-8");
            json.writeJSONString(response.getWriter());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * webjson
     *
     * @param json
     * @param response
     */
    public static void webjson(JSONObject json, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            json.writeJSONString(response.getWriter());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 字符串输出json
     *
     * @param result
     * @param response
     */
    public static void webJson(String result, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            if (result == null || "".equals(result)) {
                result = "response error";
            }
            out.print(result);
            out.flush();
            out.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public static void webJson(String result, Map data, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            JSONObject json = new JSONObject();
            json.put("result", result);
            json.put("info", data);
            response.setCharacterEncoding("utf-8");
            json.writeJSONString(response.getWriter());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * json 接口输出专用方法
     *
     * @param serviceName
     * @param paramsMap
     * @param response
     */
    public static void jsonResp(String serviceName, Map paramsMap, HttpServletResponse response) {
        try {
            Map jsonRespMap = new HashMap();

            PrintWriter out = response.getWriter();
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("utf-8");

            String result;
            if (paramsMap != null && !"".equals(paramsMap)) {
                jsonRespMap.put(serviceName, paramsMap);
                result = JSON.toJSONString(jsonRespMap);
            } else {
                paramsMap.put("result_code", "RESPONSE_EXCEPTION");
                paramsMap.put("result_msg", "[error] 网关渠道请求响应异常");
                jsonRespMap.put(serviceName, paramsMap);
                result = JSON.toJSONString(jsonRespMap);
            }

            out.print(result);
            out.flush();
            out.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }


    /**
     * 构造提交表单HTML数据
     *
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
     *
     * @param response
     * @param msg
     */
    public static void redirectToBank(HttpServletResponse response, String msg) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            String tipsBoxHtml = "<div class=\"container\">";
            tipsBoxHtml += "<div class=\"col-xs-12 col-sm-12 col-md-12\">";
            tipsBoxHtml += "<div class=\"pay-boxs\">";
            tipsBoxHtml += "<h4> <img src=\"/default/assets/images/loading.gif\" width=\"18\"> 正在跳转至银行支付页面，请稍候...</h4><br>";
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
            out.println("<link rel=\"stylesheet\" href=\"/default/assets/plugins/bootstrap/css/bootstrap.min.css\">");
            out.println("<link rel=\"stylesheet\" href=\"/default/assets/css/default.css\">");
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
     * 直连银行 [重定向]
     *
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

    /**
     * 收银台 [重定向]
     *
     * @param response
     */
    public static void directToCashpay(HttpServletResponse response, String url, String order_sn, String partner_id) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            out.println("<HTML>");
            out.println("  <HEAD><TITLE>The Online Payment...</TITLE></HEAD>");
            out.println("  <BODY>");
            out.print("<form target=\"_self\" id=\"cwsubmit\" name=\"cwsubmit\" action=\"" + url + "\" method=\"post\">");
            out.print("<input type=\"hidden\" name=\"pid\" value=\"" + partner_id + " \"/>");
            out.print("<input type=\"hidden\" name=\"order_sn\" value=\"" + order_sn + " \"/>");
            out.print("<input type=\"submit\" value=\"确认\" style=\"display:none;\"></form>");
            out.print("<script>document.forms['cwsubmit'].submit();</script>");
            out.println("  </BODY>");
            out.println("</HTML>");
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 错误提示
     *
     * @param response
     * @param message
     */
    public static void errorTips(HttpServletResponse response, String message) {
        try {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            out.println("<HTML>");
            out.println("  <HEAD><TITLE>The Online Payment...</TITLE></HEAD>");
            out.println("  <BODY>");
            out.print("<form target=\"_self\" id=\"cwsubmit\" name=\"cwsubmit\" action=\"/cashpay/error\" method=\"post\">");
            out.print("<input type=\"hidden\" name=\"message\" value=\"" + message + "\"/>");
            out.print("<input type=\"submit\" value=\"确认\" style=\"display:none;\"></form>");
            out.print("<script>document.forms['cwsubmit'].submit();</script>");
            out.println("  </BODY>");
            out.println("</HTML>");
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩文件
     *
     * @param subs
     * @param baseName
     * @param zos
     * @throws IOException
     */
    public static void zipFile(File[] subs, String baseName, ZipOutputStream zos) throws IOException {
        for (int i = 0; i < subs.length; i++) {
            File f = subs[i];
            zos.putNextEntry(new ZipEntry(baseName + f.getName()));
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            int r = 0;
            while ((r = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, r);
            }
            fis.close();
        }
    }

    /**
     * 拆分输入金额
     *
     * @param one_size       单笔金额
     * @param mention_amount 提交金额
     * @return
     */
    public static String splitMoney(String one_size, String mention_amount) {
        String amout = "";
        for (int i = 0; i < Math.floor(Double.parseDouble(mention_amount) / Double.parseDouble(one_size)); i++) {
            amout += new BigDecimal(one_size).setScale(2, BigDecimal.ROUND_HALF_UP) + ",";
        }
        if (Double.parseDouble(mention_amount) != Double.parseDouble(one_size)) {
            if (Double.parseDouble(mention_amount) % Double.parseDouble(one_size) == 0) {
//				amout = amout.substring(0, amout.length()-1);
            } else {
                amout += new BigDecimal(Double.parseDouble(mention_amount)
                        % Double.parseDouble(one_size)).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }
        return amout;
    }

    public static Long strToLong(String str) {
        if (!isEmpty(str))
            return Long.parseLong(str);
        else
            return 0l;
    }

    public static Integer strToInt(String str) {
        try {
            if (!isEmpty(str))
                return Integer.parseInt(str);
            else
                return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        //判断IP格式和范围
        String num = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
        String rexp = "^" + num + "\\." + num + "\\." + num + "\\." + num + "$";
//        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        return ipAddress;
    }

    /**
     * 检查指定的字符串列表是否不为空。
     */
    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        //正则表达式：验证邮箱
        String REGEX_EMAIL = "^(([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5}){1,25})$";
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 将POST过来反馈信息转换一下
     *
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

    public static String getRequestScheme(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        if (request.getHeader("x-forwarded-proto") == null) {
            return request.getScheme();
        }

        if ("http".equals(request.getHeader("x-forwarded-proto")) && request.getHeader("slb-ip") != null) {
            return "https";
        }
        return request.getHeader("x-forwarded-proto");
    }

    public static String getTraceInfo() {
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        int stacksLen = stacks.length;
        sb.append("class: ").append(stacks[1].getClassName()).append("; method: ").append(stacks[1].getMethodName()).append("; number: ").append(stacks[1].getLineNumber());

        return sb.toString();
    }

    public static String formatFloatNumber(double value) {
        if (value != 0.00) {
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.########");
            return df.format(value);
        } else {
            return "0.00";
        }
    }

    public static String createOrderSign(String partnerId, String orderSn, String outTradeNo, String amount) {
        StringBuilder orderFinger = new StringBuilder(8);
        orderFinger.append(orderSn);
        orderFinger.append("#");
        orderFinger.append(outTradeNo);
        orderFinger.append("#");
        orderFinger.append(partnerId);
        orderFinger.append("#");
        orderFinger.append(amount);
        return DigestUtils.sha256Hex(orderFinger.toString()).substring(0, 32);
    }

    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            throw ex;
        }

    }
}
