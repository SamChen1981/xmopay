package com.xmopay.openapi.controller;

import com.alibaba.fastjson.JSON;
import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.common.enums.ApiResultEnum;
import com.xmopay.common.utils.SignUtils;
import com.xmopay.common.utils.StringUtils;
import com.xmopay.common.utils.XmoPayUtils;
import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dto.PartnerDto;
import com.xmopay.openapi.dto.PartnerProductDto;
import com.xmopay.openapi.exception.ApiException;
import com.xmopay.openapi.service.PartnerProductService;
import com.xmopay.openapi.service.PartnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mimi on 6/05/2018
 */
public abstract class BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    private static final Map<String, String> filedParamsMap = new HashMap<>();

    static {
        // 基本参数
        filedParamsMap.put("partner_id", "Y|20");
        filedParamsMap.put("version", "Y|10");
        filedParamsMap.put("interface_name", "Y|60");
        filedParamsMap.put("input_charset", "Y|10");
        filedParamsMap.put("sign_type", "Y|3");
        filedParamsMap.put("sign", "Y|-1");
    }

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PartnerProductService partnerProductService;

    /**
     * 接口请求前置检查
     *
     * @param reqParamMap
     * @return
     */
    public SingleResult<Map> beforeRequestCheck(Map<String, String> reqParamMap) throws ApiException {
        SingleResult<Map> result = new SingleResult<>();
        result.setSuccess(false);
        result.setErrorCode(ApiResultEnum.UNKNOWN_ERROR.getRespCode());
        result.setErrorMessage(ApiResultEnum.UNKNOWN_ERROR.getRespMsg());

        try {
            // #1 参数验证
            SingleResult<Map> checkParamsResult = checkParams(reqParamMap);
            if (!checkParamsResult.isSuccess()) {
                result.setSuccess(false);
                result.setErrorCode(checkParamsResult.getErrorCode());
                result.setErrorMessage(checkParamsResult.getErrorMessage());
                return result;
            }

            // #2 商户信息是否存在
            PartnerDto partnerDto = new PartnerDto();
            partnerDto.setPartnerId(reqParamMap.get("partner_id"));
            SingleResult<PartnerDto> partnerResult = partnerService.getPartnerInfo(partnerDto);
            if (!partnerResult.isSuccess()) {
                result.setSuccess(false);
                result.setErrorCode(ApiResultEnum.PARTNER_IS_NOT_EXISTS.getRespCode());
                result.setErrorMessage(ApiResultEnum.PARTNER_IS_NOT_EXISTS.getRespMsg());
                return result;
            }

            // 商户被冻结
            if (partnerResult.getResult().getStatus() != 1) {
                result.setSuccess(false);
                result.setErrorCode(ApiResultEnum.PARTNER_HAS_BEEN_BLOCKED.getRespCode());
                result.setErrorMessage(ApiResultEnum.PARTNER_HAS_BEEN_BLOCKED.getRespMsg());
                return result;
            }

            // API权限冻结
            if (partnerResult.getResult().getApiStatus() != 1) {
                result.setSuccess(false);
                result.setErrorCode(ApiResultEnum.PARTNER_NOT_HAVE_API_PRIVILEGE.getRespCode());
                result.setErrorMessage(ApiResultEnum.PARTNER_NOT_HAVE_API_PRIVILEGE.getRespMsg());
                return result;
            }

            // 验证签名方式
            if (!SignUtils.signCheck(reqParamMap, partnerResult.getResult().getPartnerMd5Key())) {
                result.setSuccess(false);
                result.setErrorCode(ApiResultEnum.ILLEGAL_SIGN_VALUE.getRespCode());
                result.setErrorMessage(ApiResultEnum.ILLEGAL_SIGN_VALUE.getRespMsg());
                return result;
            }

            // 产品开通权限
            PartnerProductDto partnerProductDto = new PartnerProductDto();
            partnerProductDto.setStatus(1);
            partnerProductDto.setPartnerId(reqParamMap.get("partner_id"));
            partnerProductDto.setProductType(reqParamMap.get("pay_type"));
            partnerProductDto.setBankCode(reqParamMap.get("bank_code"));

            SingleResult<PartnerProductDto> partnerProductResult = partnerProductService.getPartnersProduct(partnerProductDto);
            if (!partnerProductResult.isSuccess()) {
                result.setSuccess(false);
                result.setErrorCode(ApiResultEnum.PARTNER_NOT_HAVE_PRODUCT.getRespCode());
                result.setErrorMessage(ApiResultEnum.PARTNER_NOT_HAVE_PRODUCT.getRespMsg());
                logger.info("[网关前置验证] 商户订单号={}，bank_code={}，接口权限不在其中，未被授权!", reqParamMap.get("out_trade_no"), reqParamMap.get("bank_code"));
                return result;
            }

            // 重组参数放入reqParamMap
            reqParamMap.put("aes_key", partnerResult.getResult().getPartnerAesKey());
            reqParamMap.put("partner_name", partnerResult.getResult().getPartnerName());
            reqParamMap.put("api_white_ip", partnerResult.getResult().getApiWhiteIp());
            reqParamMap.put("partner_product", JSON.toJSONString(partnerProductResult.getResult())); //商户产品信息

            // 返回验参后参数
            result.setSuccess(true);
            result.setResult(reqParamMap);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorCode(ApiResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode());
            result.setErrorMessage(ApiResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg());
            logger.info("[网关前置验证] 商户订单号={}，前置验证信息异常Exception={}", reqParamMap.get("out_trade_no"), e.getMessage());
            e.printStackTrace();
            return result;
        }
    }

    /**
     * 验证参数完整以及合法性
     * @param reqParamMap 商户提交参数集合
     * @return
     */
    public SingleResult<Map> checkParams(Map<String, String> reqParamMap) {
        SingleResult<Map> result = new SingleResult<>();
        String interfaceName = reqParamMap.get("interface_name");

        //初始值
        result.setSuccess(true);
        result.setErrorCode("VALID_PARAMETER");
        result.setErrorMessage("请求参数验证合法");

        try {
            Map<String, String> filedParams = filedParamsMap(interfaceName);

            // 检查传入的参数是否在规定参数范围内
            for (String filed : reqParamMap.keySet()) {
                if (filedParams.get(filed) == null || filedParams.get(filed) == "") {
                    result.setSuccess(false);
                    result.setErrorCode("INVALID_NULL_PARAMETER");
                    result.setErrorMessage("请求参数 {" + filed + "} 参数为非法参数，未按接口文档要求传入");
                    return result;
                }
            }

            // 根据规定参数验证传入的参数合法性
            for (String key : filedParams.keySet()) {
                String value = filedParams.get(key);
                // 是否必须
                String isMust = value.split("\\|")[0];
                // 长度0 为不限制
                String length = value.split("\\|")[1];

                // 接口传入的参数值
                String param = reqParamMap.getOrDefault(key, "").trim();

                // 参数空值,NULL校验
                if ("Y".equals(isMust) && (XmoPayUtils.isEmpty(param))) {
                    result.setSuccess(false);
                    result.setErrorCode("INVALID_NULL_PARAMETER");
                    result.setErrorMessage("请求参数 {" + key + "} 参数为空值或NULL，未按接口文档要求传入");
                    break;
                }

                // 参数长度校验
                if ("N".equals(isMust) && param == null) {
                    logger.info("请求参数 {" + key + "} 为非必填参数，当前传入值为空，无须验证");
                    continue;
                }

                if (!"-1".equals(length) && (param.length() <= 0 || param.length() > Integer.parseInt(length))) {
                    result.setSuccess(false);
                    result.setErrorCode("INVALID_PARAMETER_LENGTH");
                    result.setErrorMessage("请求参数 {" + key + "} 参数长度无效，未按接口文档要求传入, 请详细参照文档");
                    break;
                }

                // 版本号
                if ("version".equals(key)) {
                    if (!XmoPayConstants.API_VERSION.equals(param)) {
                        result.setSuccess(false);
                        result.setErrorCode("INVALID_PARAMETER_FORMAT");
                        result.setErrorMessage("请求参数 {" + key + "} 参数值无效，未按接口文档要求传入，正确的版本号, 请详细参照文档");
                        break;
                    }
                }

                // 接口名
                if ("service_name".equals(key)) {
                    if (!StringUtils.findWordInString(XmoPayConstants.INTERFACE_NAME_LIST, param, "|")) {
                        result.setSuccess(false);
                        result.setErrorCode("INVALID_PARAMETER_SERVICE_NAME");
                        result.setErrorMessage("无效的网关接口服务名 {" + key + "}，未按接口文档要求传入，请详细参照接口文档");
                        break;
                    }
                }

                // 字符类型
                if ("input_charset".equals(key)) {
                    if (!XmoPayConstants.CHARSET_UTF8.equals(param)) {
                        result.setSuccess(false);
                        result.setErrorCode("INVALID_PARAMETER_INPUT_CHARSET");
                        result.setErrorMessage("无效的编码格式类型 {" + key + "}，未按接口文档要求传入，请详细参照接口文档");
                        break;
                    }
                }

                // 签名类型
                if ("sign_type".equals(key)) {
                    if (!StringUtils.findWordInString("MD5", param, "|")) {
                        result.setSuccess(false);
                        result.setErrorCode("INVALID_PARAMETER_SIGN_TYPE");
                        result.setErrorMessage("无效的签名方式 {" + key + "}，未按接口文档要求传入，请详细参照接口文档");
                        break;
                    }
                }

                // 金额
                if ("amount".equals(key)) {
                    if (!param.matches("^[0-9]+.?[0-9]*$")) {
                        result.setSuccess(false);
                        result.setErrorCode("INVALID_PARAMETER_VALUE");
                        result.setErrorMessage("请求参数 {" + key + "} 参数格式无效，未按接口文档要求传入，参考格式: 10.01");
                        break;
                    }

                    if (Double.parseDouble(param) <= 1 && Double.parseDouble(param) > 1000000000000d) {
                        result.setSuccess(false);
                        result.setErrorCode("INVALID_PARAMETER_RANGE");
                        result.setErrorMessage("请求参数 {" + key + "} 参数最小值或最大值，未按接口文档要求传入，参数范围: Min = 1.00, Max = ∞");
                        break;
                    }
                }

                // 交易时间
                if ("out_trade_time".equals(key)) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        sdf.parse(param);
                    } catch (Exception e) {
                        result.setSuccess(false);
                        result.setErrorCode("INVALID_PARAMETER_FORMAT");
                        result.setErrorMessage("请求参数 {" + key + "} 参数格式无效，未按接口文档要求传入，参考格式: yyyy-MM-dd HH:mm:ss");
                        break;
                    }
                }

                // 异步地址
                if ("return_url".equals(key) || "notify_url".equals(key)) {
                    if (!param.matches("(http|https):\\/\\/([\\w.]+\\/?)\\S*")) {
                        result.setSuccess(false);
                        result.setErrorCode("INVALID_PARAMETER_FORMAT");
                        result.setErrorMessage("请求参数 {" + key + "} 参数格式无效，未按文档要求传入，参考格式: http(s)://");
                        break;
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setErrorCode("INVALID_PARAMETER_EXCEPTION");
            result.setErrorMessage("请求参数验证异常");
            logger.error("[请求参数验证] 请求参数验证异常，联系运营商协助检查 Exception=",  e.getMessage());
        }
        return result;
    }

    /**
     * 参数配置
     *
     * @param interfaceName
     * @return
     */
    public Map<String, String> filedParamsMap(String interfaceName) {
        // 业务参数 - 交易类型
        if (XmoPayConstants.XMO_ONLINE_PAY.equals(interfaceName)) {
            // 商品描述
            filedParamsMap.put("production_description", "N|30");
            // 支付方式
            filedParamsMap.put("pay_type", "Y|20");
            // 银行代码
            filedParamsMap.put("bank_code", "Y|20");
            filedParamsMap.put("notify_url", "Y|500");
            filedParamsMap.put("return_url", "N|500");
            filedParamsMap.put("amount", "Y|16");
            filedParamsMap.put("out_trade_no", "Y|48");
            filedParamsMap.put("memo", "N|58");
            filedParamsMap.put("summary", "N|30");
            filedParamsMap.put("out_trade_time", "N|19");
        }

        return filedParamsMap;
    }
}
