package com.xmopay.openapi.controller;

import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.common.enums.ApiResultEnum;
import com.xmopay.common.utils.StringUtils;
import com.xmopay.common.utils.XmoPayUtils;
import com.xmopay.openapi.common.ApiResponse;
import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.core.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * OPENAPI URL结构描述
 * <p>
 * 交易类型：/gateway/payment
 * <p>
 */

@RestController
@RequestMapping(value = "gateway")
public class GatewayController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    private static final String XMO_COOKIE_HASH = "XMO_COOKIE_HASH";

    @Resource(name = "paymentService")
    private PaymentService paymentService;

    @RequestMapping(method = RequestMethod.POST, value = "payment")
    public ApiResponse<Map> route(HttpServletRequest request, HttpServletResponse response) {
        ApiResponse<Map> respResult = new ApiResponse();
        respResult.setRespCode(ApiResultEnum.RESPONSE_FAILURE.getRespCode());
        respResult.setRespMessage(ApiResultEnum.RESPONSE_FAILURE.getRespMsg());
        try {
            // #1 根据Request获取接口参数
            Map reqParamMap = XmoPayUtils.formRequestMap(request.getParameterMap());
            logger.info("支付入口: 请求头=" + XmoPayUtils.getHeadersInfo(request) + ", 请求参数=" + reqParamMap);
            if (reqParamMap == null) {
                respResult.setRespCode(ApiResultEnum.ILLEGAL_ORDER_PARAMS_NULL.getRespCode());
                respResult.setRespMessage(ApiResultEnum.ILLEGAL_ORDER_PARAMS_NULL.getRespMsg());
                return respResult;
            }

            // #2 网关接口名称
            final String interfaceName = (String) reqParamMap.getOrDefault("interface_name", "");
            if (XmoPayUtils.isEmpty(interfaceName)) {
                respResult.setRespCode(ApiResultEnum.UNKNOWN_GATEWAY_SERVICE_NAME.getRespCode());
                respResult.setRespMessage(ApiResultEnum.UNKNOWN_GATEWAY_SERVICE_NAME.getRespMsg());
                return respResult;
            }

            if (!StringUtils.findWordInString(XmoPayConstants.INTERFACE_NAME_LIST, interfaceName.toUpperCase(), "|")) {
                respResult.setRespCode(ApiResultEnum.UNKNOWN_GATEWAY_SERVICE_NAME.getRespCode());
                respResult.setRespMessage(ApiResultEnum.UNKNOWN_GATEWAY_SERVICE_NAME.getRespMsg());
                return respResult;
            }

            // #3 入参前置处理逻辑
            SingleResult<Map> beforeRequestCheck = beforeRequestCheck(reqParamMap);
            if (!beforeRequestCheck.isSuccess()) {
                respResult.setRespCode(beforeRequestCheck.getErrorCode());
                respResult.setRespMessage(beforeRequestCheck.getErrorMessage());
                return respResult;
            }

            // #4 种植cookieHash
            String cookieHashValue = "";
            Cookie[] cookie = request.getCookies();
            if (cookie != null) {
                for (int i = 0; i < cookie.length; i++) {
                    Cookie cook = cookie[i];
                    if (XMO_COOKIE_HASH.equalsIgnoreCase(cook.getName())) {
                        cookieHashValue = cook.getValue();
                    }
                }
            }

            // 无COOKIE的时候 种植COOKIE
            if (XmoPayUtils.isEmpty(cookieHashValue)) {
                cookieHashValue = XmoPayUtils.MD5(StringUtils.buildOrdersn());
                Cookie cookieHash = new Cookie(XMO_COOKIE_HASH, cookieHashValue);
                cookieHash.setMaxAge(5 * 365 * 24 * 60 * 60); // 5年
                cookieHash.setPath("/");
                response.addCookie(cookieHash);
            }

            // 监控参数
            final String tradeIp = XmoPayUtils.getIpAddr(request);
            reqParamMap.put("trade_hash", XmoPayUtils.isEmpty(cookieHashValue) ? XmoPayUtils.MD5(tradeIp) : cookieHashValue);
            reqParamMap.put("trade_ip", tradeIp);

            // 单笔在线支付/充值接口
            if (XmoPayConstants.XMO_ONLINE_PAY.equals(interfaceName)) {
                // 创建入库订单
                SingleResult<Map> paymentResult = paymentService.doOnlinePay(reqParamMap);
                if (!paymentResult.isSuccess()) {
                    respResult.setRespCode(paymentResult.getErrorCode());
                    respResult.setRespMessage(paymentResult.getErrorMessage());
                    return respResult;
                }

                respResult.setRespCode(ApiResultEnum.RESPONSE_SUCCESS.getRespCode());
                respResult.setRespMessage(ApiResultEnum.RESPONSE_SUCCESS.getRespMsg());
                respResult.setRespResult(paymentResult.getResult());
                return respResult;
            }

            // 最终统一返回成功结果
            respResult.setRespCode(ApiResultEnum.RESPONSE_FAILURE.getRespCode());
            respResult.setRespMessage(ApiResultEnum.RESPONSE_FAILURE.getRespMsg());
            return respResult;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("支付入口, 请求异常, ex={}" + e.getMessage(), e);
            respResult.setRespCode(ApiResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespCode());
            respResult.setRespMessage(ApiResultEnum.UNKNOWN_EXCEPTION_ERROR.getRespMsg());
            return respResult;
        }
    }
}