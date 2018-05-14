package com.xmopay.common.utils;


import org.apache.commons.codec.digest.DigestUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by mimi on 6/05/2018.
 */
public class StringUtils {

    /**
     * 查找某个单词是否在给定的字符串中
     * @param source
     * @param word
     * @param delim the delimiters
     * @return
     */
    public static boolean findWordInString(String source, String word, String delim) {
        StringTokenizer stringTokenizer = new StringTokenizer(source, delim);
        while (stringTokenizer.hasMoreElements()) {
            String e = (String) stringTokenizer.nextElement();
            if (word.equals(e)) {
                return true;
            }
        }
        return false;
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
    public static String buildBillsn() {
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
     * 获取6为数字和字码的混合码
     *
     * @return
     */
    public static String genRandomMix(int ran_length) {
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

    //生成IP串
    public static String genIpAddressStr(String ip, int start, int end) {
        String ips = "";
        for (int i = start; i <= end; i++) {
            ips += ip + i + ",";
        }
        return ips;
    }

    /**
     * 订单号生成规则
     *
     * @param ordersnRule
     * @return
     */
    public static String getOrderSNByRule(String ordersnRule) {
        try {
            String order_sn = buildOrdersn();
            if (ordersnRule == null || "".equals(ordersnRule)) {
                return order_sn;
            }

            //默认规则
            if ("default".equals(ordersnRule)){
                return order_sn;
            }

            // 订单号规则: 32位MD5字符串
            if ("ordersn_rule_01".equals(ordersnRule)) {
                return DigestUtils.md5Hex(order_sn);
            }

            // 订单号规则：6位随机码+2位业务编码+年的后1位+月+日+飞秒+时+分。
            if ("ordersn_rule_02".equals(ordersnRule)) {
                String random = getRandomNum(6);
                String bizCode = "10";
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
                String date = sdf.format(new Date());
                return random + bizCode + date;
            }

            // 订单号规则：年的第三位数+2位业务编码+年的后1位+月+日+商户UID前6位+飞秒+时+分
            if ("ordersn_rule_03".equals(ordersnRule)) {
                String bizCode = "30";
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
                String date = sdf.format(new Date());
                return date.substring(0, 1) + bizCode + date.substring(1, 2) + date.substring(2, 6) + StringUtils.genRandomMix(6) + date.substring(6, 12) + date.substring(12, 15);
            }

            // 订单号规则: WX+年月日时分秒飞秒+HASHCODE
            if ("ordersn_rule_04".equals(ordersnRule)) {
                int machineId = 1; //最大支持1-9个集成机器部署
                int hashCodeV = UUID.randomUUID().toString().hashCode();

                SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
                String date = sdf.format(new Date());
                String newDate = date.substring(0, 1) + date.substring(2, 13) + date.substring(1, 2);
                return "WX" + newDate + Math.abs(hashCodeV) + machineId;
            }

            //订单号规则: 秒+飞秒+HASHCODE+4位随机码
            if ("ordersn_rule_05".equals(ordersnRule)) {
                int hashCodeV = UUID.randomUUID().toString().hashCode();

                SimpleDateFormat sdf = new SimpleDateFormat("ssSSS");
                String key = sdf.format(new Date());
                return Math.abs(hashCodeV) + key + getRandomNum(4);
            }

            //订单号规则: HASHCODE+业务码+4位随机码
            if ("ordersn_rule_06".equals(ordersnRule)) {
                int hashCodeV = UUID.randomUUID().toString().hashCode();
                return Math.abs(hashCodeV) + "BM" + genRandomMix(4).toUpperCase();
            }

            //订单号规则: twitter的snowflake生成规整： 月日时分秒飞秒+2位业务编码+snowflake
            if ("ordersn_rule_07".equals(ordersnRule)) {
                String bizCode = "70";
                IdWorker idWorker = new IdWorker(0, 0);
                long id = idWorker.nextId(); //18位

                SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
                String date = sdf.format(new Date());

                return date.substring(5, 15) + bizCode +id;
            }

            //订单号规则: 年+月+时+周几+年几天+月几天+毫秒+时间戳
            if ("ordersn_rule_08".equals(ordersnRule)) {
                long currentTimeMillis = System.currentTimeMillis(); //13位
                Calendar calendar = Calendar.getInstance();
                // 显示年份
                int year = calendar.get(Calendar.YEAR);
                // 显示月份 (从0开始, 实际显示要加一)
                String month = "";
                int month_int = calendar.get(Calendar.MONTH)+1;
                if(month_int<10){
                    month = "0"+month_int;
                }else{
                    month = month_int+"";
                }
                // 显示小时
                String hour_of_day = "";
                int hour_of_day_int = calendar.get(Calendar.HOUR_OF_DAY);
                if(hour_of_day_int<10){
                    hour_of_day = "0"+hour_of_day_int;
                }else{
                    hour_of_day = hour_of_day_int+"";
                }
                // 本周几
                String week="";
                int week_int = calendar.get(Calendar.DAY_OF_WEEK);
                if(week_int<10){
                    week = "0"+week_int;
                }else{
                    week = week_int+"";
                }
                // 今年的第 N 天
                String day_of_year = "";
                int day_of_year_int = calendar.get(Calendar.DAY_OF_YEAR);
                if(day_of_year_int<10){
                    day_of_year = "0"+day_of_year_int;
                }else{
                    day_of_year = day_of_year_int+"";
                }
                // 本月第 N 天 日
                String day_of_month = "";
                int day_of_month_int = calendar.get(Calendar.DAY_OF_MONTH);
                if(day_of_month_int<10){
                    day_of_month = "0"+day_of_month_int;
                }else{
                    day_of_month = day_of_month_int+"";
                }
                //毫秒
                String millisecond = "";
                int millisecond_int = calendar.get(Calendar.MILLISECOND);
                if(millisecond_int<10){
                    millisecond = "0"+millisecond_int;
                }else{
                    millisecond = millisecond_int+"";
                }
                return String.valueOf(year) + month + hour_of_day + week + day_of_year + day_of_month + millisecond + currentTimeMillis ;
            }

            //订单号规则: 业务编码+时间戳+年月日时分秒飞秒错乱排
            if ("ordersn_rule_09".equals(ordersnRule)) {
                long currentTimeMillis = System.currentTimeMillis(); //13位
                String bizCode = "90";
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
                String date = sdf.format(new Date());
                return bizCode + currentTimeMillis + date.substring(12, 15) + date.substring(2, 6) + date.substring(0, 2) + date.substring(6, 12);
            }

            //订单号规则: snowflake+时间戳
            if ("ordersn_rule_10".equals(ordersnRule)) {
                IdWorker idWorker = new IdWorker(2, 1);
                long id = idWorker.nextId(); //18位
                long currentTimeMillis = System.currentTimeMillis(); //13位
                return (id+""+currentTimeMillis).substring(0, 30);
            }

            //订单号规则: HASHCODE+snowflake
            if ("ordersn_rule_11".equals(ordersnRule)) {
                IdWorker idWorker = new IdWorker(3, 3);
                long id = idWorker.nextId(); //18位
                int hashCodeV = UUID.randomUUID().toString().hashCode();
                return ""+Math.abs(hashCodeV)+id;
            }

            //订单号规则: 业务码+HASHCODE+时间戳
            if ("ordersn_rule_12".equals(ordersnRule)) {
                long currentTimeMillis = System.currentTimeMillis(); //13位
                int hashCodeV = UUID.randomUUID().toString().hashCode();
                String bizCode = "12";
                SimpleDateFormat sdf = new SimpleDateFormat("ssSSS");
                String date = sdf.format(new Date());
                return bizCode+Math.abs(hashCodeV)+currentTimeMillis+date;
            }

            //强制判断一旦为空，重新赋值.
            return buildOrdersn();
        } catch (Exception e){
            e.printStackTrace();
            return buildOrdersn(); //异常时候返回默认值
        }
    }
}
