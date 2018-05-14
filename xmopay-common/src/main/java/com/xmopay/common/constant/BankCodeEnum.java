package com.xmopay.common.constant;

public enum BankCodeEnum {

    BANK_CCB("BANK_CCB", "中国建设银行"),
    BANK_CEB("BANK_CEB", "中国光大银行"),
    BANK_ABC("BANK_ABC", "中国农业银行"),
    BANK_ICBC("BANK_ICBC", "中国工商银行"),
    BANK_CMBC("BANK_CMBC", "中国民生银行"),
    BANK_CMB("BANK_CMB", "招商银行"),
    BANK_BOC("BANK_BOC", "中国银行"),
    BANK_CITIC("BANK_CITIC", "中信银行"),
    BANK_GDB("BANK_GDB", "广发银行"),
    BANK_CIB("BANK_CIB", "兴业银行"),
    BANK_PAB("BANK_PAB", "平安银行"),
    BANK_CZB("BANK_CZB", "浙商银行"),
    BANK_BEA("BANK_BEA", "东亚银行"),
    BANK_BOS("BANK_BOS", "上海银行"),
    BANK_BON("BANK_BON", "南京银行"),
    BANK_TCCB("BANK_TCCB", "天津银行"),
    BANK_BOBJ("BANK_BOBJ", "北京银行"),
    BANK_SPDB("BANK_SPDB", "浦发银行"),
    BANK_HXBC("BANK_HXBC", "华夏银行"),
    BANK_CBHB("BANK_CBHB", "渤海银行"),
    BANK_NBCB("BANK_NBCB", "宁波银行"),
    BANK_HZCB("BANK_HZCB", "杭州银行"),
    BANK_BOCOM("BANK_BOCOM", "交通银行"),
    BANK_SRCB("BANK_SRCB", "上海农商银行"),
    BANK_BJRCB("BANK_BJRCB", "北京农商银行"),
    BANK_PSBC("BANK_PSBC", "中国邮政储蓄银行"),
    BANK_ZJTLCB("BANK_ZJTLCB", "浙江泰隆商业银行");

//    BANK_ABC("BANK_ABC", "乌鲁木齐市商业银行"),
//    BANK_ABC("BANK_ABC", "郑州银行"),
//    BANK_ABC("BANK_ABC", "宁夏银行"),
//    BANK_ABC("BANK_ABC", "重庆银行"),
//    BANK_ABC("BANK_ABC", "温州银行"),
//    BANK_ABC("BANK_ABC", "恒丰银行"),
//    BANK_ABC("BANK_ABC", "花旗银行"),
//    BANK_ABC("BANK_ABC", "广州银行"),
//    BANK_ABC("BANK_ABC", "苏州银行"),
//    BANK_ABC("BANK_ABC", "徽商银行"),
//    BANK_ABC("BANK_ABC", "哈尔滨银行"),
//    BANK_ABC("BANK_ABC", "贵阳银行"),
//    BANK_ABC("BANK_ABC", "兰州银行"),
//    BANK_ABC("BANK_ABC", "南昌银行"),
//    BANK_ABC("BANK_ABC", "长沙银行"),
//    BANK_ABC("BANK_ABC", "齐商银行");

    private String respCode;
    private String respName;

    BankCodeEnum(String respCode, String respName) {
        this.respCode = respCode;
        this.respName = respName;
    }

    // 普通方法
    public static String getName(String respName) {
        for (BankCodeEnum c : BankCodeEnum.values()) {
            if (respName.equals(c.getRespName())) {
                return c.respCode;
            }
        }
        return null;
    }

    public static String getBankCodeEnumValue(String respCode) {
        for (BankCodeEnum BankCodeEnum : BankCodeEnum.values()) {
            if (BankCodeEnum.getRespCode().equals(respCode)) {
                return BankCodeEnum.getRespName();
            }
        }
        return null;
    }

    public static BankCodeEnum getBankCodeEnum(String respCode) {
        for (BankCodeEnum BankCodeEnum : BankCodeEnum.values()) {
            if (respCode.equals(BankCodeEnum.getRespCode())) {
                return BankCodeEnum;
            }
        }
        return null;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespName() {
        return respName;
    }

    public void setRespName(String respName) {
        this.respName = respName;
    }


}
