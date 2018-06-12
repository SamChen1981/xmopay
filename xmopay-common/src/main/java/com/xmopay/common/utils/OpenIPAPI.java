package com.xmopay.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.xmopay.common.utils.http.HttpClientUtils;

/**
 * Created by geek on 16/8/20 下午4:43.
 */
public class OpenIPAPI {

    //总入口方法
    public static JSONObject getQueryIP(String ip){
        JSONObject countryMap = new JSONObject();
        if(XmoPayUtils.isCollectionNull(countryMap)){
            countryMap = getQuerySinaIP(ip);
        }
        if(XmoPayUtils.isCollectionNull(countryMap)){
            countryMap = getQueryTBIP(ip);
        }
        return countryMap;
    }

    public static JSONObject getQueryTBIP(String ip){
        try {
            String url = "http://ip.taobao.com/service/getIpInfo.php?ip="+ip;
            String result = HttpClientUtils.rsyncGet(url);
            if(XmoPayUtils.isEmpty(result)){
                return null;
            }
            JSONObject json = JSONObject.parseObject(result);
            if (XmoPayUtils.isNull(json)) {
                return null;
            }
            String code = json.getString("code"); //0：成功，1：失败。
            if (XmoPayUtils.isEmpty(code) || !"0".equals(code)) {
                return null;
            }
            String data = json.getString("data");
            if (XmoPayUtils.isEmpty(data)) {
                return null;
            }
            if (!XmoPayUtils.isEmpty(data) && !result.startsWith("{")) {
                return null;
            }
            JSONObject dataJson = JSONObject.parseObject(data);
            if (XmoPayUtils.isNull(dataJson)) {
                return null;
            }
            //（json格式的）country国家 、region省（自治区或直辖市）、city市（县）、isp运营商
            return dataJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getQuerySinaIP(String ip){
        try {
            String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip="+ip;
            String result = HttpClientUtils.rsyncGet(url);
            //{"ret":1,"start":-1,"end":-1,"country":"柬埔寨","province":"Phnum Penh","city":"Phnom Penh","district":"","isp":"","type":"","desc":""}
            if(XmoPayUtils.isEmpty(result)){
                return null;
            }
            if (!XmoPayUtils.isJson(result)) {
                return null;
            }
            JSONObject json = JSONObject.parseObject(result);
            if (XmoPayUtils.isNull(json)) {
                return null;
            }
            String ret = json.getString("ret"); //1：成功，0：失败。
            if (XmoPayUtils.isEmpty(ret) || !"1".equals(ret)) {
                return null;
            }
            //（json格式的）国家 、省（自治区或直辖市）、市（县）、运营商
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
