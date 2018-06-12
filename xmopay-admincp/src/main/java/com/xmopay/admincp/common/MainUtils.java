package com.xmopay.admincp.common;

import com.xmopay.common.utils.XmoPayUtils;

public class MainUtils {

    public static void main(String[] args) {
        test();
    }

    public static void test(){
        try {
            //3ce7d8a461e76c5bf0935a08d57be3a3
            //cab5727d440b90b7f9d238ff6ce6b548
            String pwd = XmoPayUtils.getCertifiedSigned(XmoPayUtils.MD5("admin123"), "uiao4r");
            System.out.println(pwd);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
