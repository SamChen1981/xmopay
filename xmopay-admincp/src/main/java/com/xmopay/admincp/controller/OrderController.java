package com.xmopay.admincp.controller;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.Pagesutils;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.common.WebCommon;
import com.xmopay.admincp.common.XmopayResponse;
import com.xmopay.admincp.dto.*;
import com.xmopay.admincp.service.GatewayChannelService;
import com.xmopay.admincp.service.MessageQueueService;
import com.xmopay.admincp.service.RefundService;
import com.xmopay.admincp.service.TradeOrderService;
import com.xmopay.common.constant.BankCodeEnum;
import com.xmopay.common.constant.StatusConstants;
import com.xmopay.common.constant.XmoPayConstants;
import com.xmopay.common.utils.XmoPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping(value = "orders")
@Slf4j
public class OrderController {

    @Autowired
    private TradeOrderService tradeOrderService;

    @Autowired
    private GatewayChannelService gatewayChannelService;

    @Autowired
    private MessageQueueService messageQueueService;

    @Autowired
    private RefundService refundService;

    /**
     * @param
     * @return
     * @Description: [页面]收款订单
     */
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request) {
        try {
            String partnerId = request.getParameter("partner_id");
            String orderSn = request.getParameter("order_sn");
            String outTradeNo = request.getParameter("out_trade_no");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String minPayMoney = request.getParameter("min_paymoney");
            String maxPayMoney = request.getParameter("max_paymoney");
            String checkStatus = request.getParameter("check_status");
            String noticeStatus = request.getParameter("notice_status");
            String orderStatus = request.getParameter("order_status");
            String tradeIp = request.getParameter("trade_ip");
            String bankCode = request.getParameter("bank_code");
            String channelCode = request.getParameter("channel_code");
            String payType = request.getParameter("pay_type");
            String inajax = request.getParameter("inajax");


            request.setAttribute("partner_id", partnerId);
            request.setAttribute("order_sn", orderSn);
            request.setAttribute("out_trade_no", outTradeNo);
            request.setAttribute("min_paymoney", minPayMoney);
            request.setAttribute("max_paymoney", maxPayMoney);
            request.setAttribute("notice_status", noticeStatus);
            request.setAttribute("check_status", checkStatus);
            request.setAttribute("order_status", orderStatus);
            request.setAttribute("trade_ip", tradeIp);
            request.setAttribute("bank_code", bankCode);
            request.setAttribute("channel_code", channelCode);
            request.setAttribute("pay_type", payType);

            TradeOrderDto tradeOrderDto = new TradeOrderDto();
            tradeOrderDto.setOrderSn(orderSn);
            tradeOrderDto.setMinPayMoney(minPayMoney);
            tradeOrderDto.setMaxPayMoney(maxPayMoney);
            tradeOrderDto.setOutTradeNo(outTradeNo);
            tradeOrderDto.setBankCode(bankCode);
            tradeOrderDto.setChannelCode(channelCode);
            tradeOrderDto.setOrderStatus(XmoPayUtils.isEmpty(orderStatus) ? null : Integer.valueOf(orderStatus));
            tradeOrderDto.setTradeIp(tradeIp);
            tradeOrderDto.setPayType(payType);
            tradeOrderDto.setTranType(XmoPayConstants.TYPE_ONLINE_PAY);

            //如果开始时间和结束时间都为空就默认显示今天
            if (XmoPayUtils.isEmpty(startTime) && XmoPayUtils.isEmpty(endTime)) {
                // DateTimeUtils.getDateTimeToString
                startTime = DateFormatUtils.format(DateUtils.addDays(new Date(), -3), "yyyy-MM-dd 00:00:00");
                endTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
            }
            tradeOrderDto.setStartTime(startTime);
            tradeOrderDto.setEndTime(endTime);
            request.setAttribute("startTime", startTime);
            request.setAttribute("endTime", endTime);
            request.setAttribute("paramMap", tradeOrderDto); //参数传入页面
            request.setAttribute("inajax", (inajax != null) ? inajax : "0"); //ajax分页

            //利用枚举类获取银行列表，并存入list
            List<BankCodeEnum> bankList = new ArrayList<>();
            for (BankCodeEnum bankCodeEnum : BankCodeEnum.values()) {
                bankList.add(bankCodeEnum);
            }
            request.setAttribute("bankList", bankList);

            //分页显示记录
            String index = request.getParameter("pageIndex");
            int pageIndex = XmoPayUtils.isEmpty(index) ? 1 : Integer.parseInt(index);
            int pageSize = 20;

            tradeOrderDto.setCurrentPage(pageIndex);
            tradeOrderDto.setPageSize(pageSize);

            SingleResult<PageInfo> singleResult = tradeOrderService.getTradeOrderList(tradeOrderDto);
            if (singleResult.isSuccess()) {
                PageInfo pageUtils = singleResult.getResult();
                request.setAttribute("page", pageUtils);
                request.setAttribute("lists", pageUtils.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, pageUtils, "listDiv"));
            }

            //读取全部网关
            GatewayChannelDto gatewayChannel = new GatewayChannelDto();
            gatewayChannel.setChannelType(XmoPayConstants.PAY_IN);  //支付网关
            gatewayChannel.setStatus(1);  //状态为开启
            SingleResult<List<GatewayChannelDto>> gatewayListResult = gatewayChannelService.findGatewayChannelList(gatewayChannel);
            if (gatewayListResult.isSuccess()) {
                request.setAttribute("gatewaylist", gatewayListResult.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "orders/index";
    }

    /**
     * @param
     * @return
     * @Description: [Action]进款管理-收款订单-汇总
     */
    @ResponseBody
    @RequestMapping(value = "getTotal")
    public XmopayResponse<Map> getTotal(HttpServletRequest request) {
        XmopayResponse<Map> result = new XmopayResponse<>(XmopayResponse.FAILURE, "汇总失败", null);
        //记录日志需要
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid = sessionUser.getMuId();
        try {
            String logmsg = "[用户常规事件] 当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);
            String partnerId = request.getParameter("partner_id");
            String orderSn = request.getParameter("order_sn");
            String outTradeNo = request.getParameter("out_trade_no");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String minPaymoney = request.getParameter("min_paymoney");
            String maxPaymoney = request.getParameter("max_paymoney");
            String checkStatus = request.getParameter("check_status");
            String noticeStatus = request.getParameter("notice_status");
            String orderStatus = request.getParameter("order_status");
            String bankCode = request.getParameter("bank_code");
            String tradeIp = request.getParameter("trade_ip");
            String channelCode = request.getParameter("channel_code");
            String payType = request.getParameter("pay_type");

            TradeOrderDto tradeOrderDto = new TradeOrderDto();
            tradeOrderDto.setPartnerId(partnerId);
            tradeOrderDto.setOrderSn(orderSn);
            tradeOrderDto.setMinPayMoney(minPaymoney);
            tradeOrderDto.setMaxPayMoney(maxPaymoney);
            tradeOrderDto.setOutTradeNo(outTradeNo);
            tradeOrderDto.setBankCode(bankCode);
            tradeOrderDto.setOrderStatus(XmoPayUtils.isEmpty(orderStatus) ? null : Integer.valueOf(orderStatus));
            tradeOrderDto.setTradeIp(tradeIp);
            tradeOrderDto.setChannelCode(channelCode);
            tradeOrderDto.setPayType(payType);
            tradeOrderDto.setTranType(XmoPayConstants.TYPE_ONLINE_PAY);//支付类型 支付

            //如果开始时间和结束时间都为空就默认显示今天
            if (XmoPayUtils.isEmpty(startTime) && XmoPayUtils.isEmpty(endTime)) {
                startTime = DateFormatUtils.format(DateUtils.addDays(new Date(), -3), "yyyy-MM-dd 00:00:00");
                endTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
            }
            tradeOrderDto.setStartTime(startTime);
            tradeOrderDto.setEndTime(endTime);

            SingleResult<Map<String, Object>> tradeOrderTotal = tradeOrderService.getTradeOrderTotal(tradeOrderDto);
            if (tradeOrderTotal.isSuccess()) {
                result.setResultData(tradeOrderTotal.getResult());
                log.info(logmsg, "查询收款订单汇总信息成功");
                result.setResultCode(XmopayResponse.SUCCESS);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param request
     * @return
     * @Description: [页面]商户订单 - 详情
     */
    @RequestMapping(value = "/detail")
    public String detail(HttpServletRequest request) {
        try {
            String partnerId = request.getParameter("partner_id");
            String orderSn = request.getParameter("order_sn");

            TradeOrderDto tradeOrderDto = new TradeOrderDto();
            tradeOrderDto.setPartnerId(partnerId);
            tradeOrderDto.setOrderSn(orderSn);

            SingleResult<TradeOrderDto> singleResult = tradeOrderService.getTradeOrderInfo(tradeOrderDto);
            if (singleResult.isSuccess()) {
                TradeOrderDto tradeInfo = singleResult.getResult();
                request.setAttribute("tradeInfo", tradeInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "orders/detail";
    }

    /**
     * @param request
     * @return
     * @Description: [Action]修改交易订单详情 状态
     */
    @ResponseBody
    @RequestMapping(value = "/editOrder")
    public XmopayResponse<String> editOrder(HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse(XmopayResponse.FAILURE, "修改交易订单详情失败", null);
        try {
            //记录日志需要
            AdminUserDto adminUser = WebCommon.getSessionUserInfo(request);
            Integer muid = adminUser.getMuId();
            //日志
            String logmsg = "[用户常规事件] 当前登录者=" + muid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

            String orderSn = request.getParameter("order_sn");
            String modify_order_status = request.getParameter("modify_order_status");

            if (XmoPayUtils.isEmpty(orderSn)) {
                log.error(logmsg, "订单号为空!");
                resultResp.setResultMsg("订单号为空!");
                return resultResp;
            }
            if (XmoPayUtils.isEmpty(modify_order_status)) {
                log.error(logmsg, "可调整状态未选择!");
                resultResp.setResultMsg("可调整状态未选择!");
                return resultResp;
            }

            AdminUserDto adminUserDto = WebCommon.getSessionUserInfo(request);
            int roleId = adminUserDto.getRoleId();

            TradeOrderDto tradeOrderDto = new TradeOrderDto();
            tradeOrderDto.setOrderSn(orderSn);

            SingleResult<TradeOrderDto> traderOrderInfoResult = tradeOrderService.getTradeOrderInfo(tradeOrderDto);
            if (!traderOrderInfoResult.isSuccess()) {
                log.error(logmsg, "订单不存在, 目前仅限查询当前库数据!");
                resultResp.setResultMsg("订单不存在, 目前仅限查询当前库数据!");
                return resultResp;
            }
            TradeOrderDto tradeInfo = traderOrderInfoResult.getResult();
            //'订单状态: WAIT_TO_BANK-未提交到银行  WAIT_BANK_NOTIFY-已提交待处理  TRADE_SUCCESS-交易成功
            // TRADE_FAILURE-交易失败  TRADE_EXCEPTION - 异常订单 TRADE_CLOSE - 订单自动关闭', -->
            //（允许：已提交待处理-->成功[需要超级权限]；已提交待处理-->失败[需要超级权限]；未提交到银行-->订单自动关闭; 风控订单-->成功）
            Integer order_status = tradeInfo.getOrderStatus();
            Integer modifyOrderStatus = XmoPayUtils.isEmpty(modify_order_status) ? null : Integer.valueOf(modify_order_status);

            boolean situation1 = (order_status == StatusConstants.TRADE_WAIT_BANK_HANDLE && modifyOrderStatus == StatusConstants.TRADE_FAILURE);
            boolean situation2 = (order_status == StatusConstants.TRADE_WAIT_BANK_HANDLE && modifyOrderStatus == StatusConstants.TRADE_SUCCESS);
            boolean situation3 = (order_status == StatusConstants.TRADE_WAIT_TO_BANK && modifyOrderStatus == StatusConstants.TRADE_CLOSE);
            boolean situation4 = (order_status == StatusConstants.TRADE_EXCEPTION && modifyOrderStatus == StatusConstants.TRADE_SUCCESS);

            //已提交待处理-->成功[需要超级权限]；已提交待处理-->失败[需要超级权限]；
            if (roleId != 1) {
                if (situation1) {
                    log.error(logmsg, "无权将已提交待处理状态更改成功状态,需要超级权限!");
                    resultResp.setResultMsg("无权将已提交待处理状态更改成功状态,需要超级权限!");
                    return resultResp;
                }
                if (situation2) {
                    log.error(logmsg, "无权将已提交待处理状态更改失败状态,需要超级权限!");
                    resultResp.setResultMsg("无权将已提交待处理状态更改失败状态,需要超级权限!");
                    return resultResp;
                }
            }

            if (situation1 || situation2 || situation3 || situation4) {
                SingleResult<String> result = tradeOrderService.editTrade(tradeInfo, modifyOrderStatus, adminUserDto);

                resultResp.setResultMsg(result.getResult());
                if (!result.isSuccess()) {
                    log.error(logmsg, "修改商户订单状态失败!");
                    return resultResp;
                }
                log.info(logmsg, "修改商户订单状态成功!");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;
            } else if (order_status == StatusConstants.TRADE_SUCCESS && modifyOrderStatus == StatusConstants.TRADE_FAILURE) {
                //成功改失败，先放着
                log.error(logmsg, "该功能还未完善!");
                resultResp.setResultMsg("该功能还未完善!");
                return resultResp;
            } else {
                log.error(logmsg, "订单不允许操作!");
                resultResp.setResultMsg("订单不允许操作!");
                return resultResp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    /**
     * @param
     * @return
     * @Description: [页面]消息队列
     */
    @RequestMapping(value = "/queue")
    public String queue(HttpServletRequest request) {
        try {
            String orderSn = request.getParameter("order_sn");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String threadStatus = request.getParameter("thread_status");
            String consumerStatus = request.getParameter("consumer_status");
            String inajax = request.getParameter("inajax"); //ajax模式


            request.setAttribute("order_sn", orderSn);
            request.setAttribute("startTime", startTime);
            request.setAttribute("endTime", endTime);
            request.setAttribute("thread_status", threadStatus);
            request.setAttribute("consumer_status", consumerStatus);
            request.setAttribute("inajax", (inajax != null) ? inajax : "0"); //ajax分页

            MessageQueueDto messageQueueDto = new MessageQueueDto();
            messageQueueDto.setOrderSn(orderSn);
            messageQueueDto.setStartTime(startTime);
            messageQueueDto.setEndTime(endTime);
            messageQueueDto.setConsumerStatus(XmoPayUtils.isEmpty(consumerStatus) ? null : Integer.valueOf(consumerStatus));
            messageQueueDto.setThreadStatus(XmoPayUtils.isEmpty(threadStatus) ? null : Integer.valueOf(threadStatus));

            // 分页显示记录
            String index = request.getParameter("pageIndex");
            int pageIndex = XmoPayUtils.isEmpty(index) ? 1 : Integer.parseInt(index);
            int pageSize = 20;

            messageQueueDto.setCurrentPage(pageIndex);
            messageQueueDto.setPageSize(pageSize);
            SingleResult<PageInfo> singleResult = messageQueueService.getMessageQueueList(messageQueueDto);
            if (singleResult.isSuccess()) {
                PageInfo pageUtils = singleResult.getResult();
                request.setAttribute("page", pageUtils);
                request.setAttribute("lists", pageUtils.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, pageUtils, "listDiv"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "orders/queue";
    }

    /**
     * @param
     * @return
     * @Description: [Action]删除消息队列
     */
    @ResponseBody
    @RequestMapping(value = "/deleteQueue")
    public XmopayResponse<String> deleteQueue(HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse<>(XmopayResponse.FAILURE, "删除消息队列失败", null);
        try {
            AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
            Integer muid = sessionUser.getMuId();
            int roleId = sessionUser.getRoleId();
            String mqids = request.getParameter("mqids"); //批量提现操作 参数
            String mqid = request.getParameter("mqid");  //单独提现操作 参数

            //日志
            String logmsg = "[用户常规事件] 当前登录者=" + muid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

            if (XmoPayUtils.isNull(sessionUser)) {
                log.error(logmsg, "登录失效请重新登录!");
                resultResp.setResultMsg("登录失效请重新登录!");
                return resultResp;
            }

            if (roleId != 1) {
                log.error(logmsg, "用户不是超级管理员，没有删除消息队列权限!");
                resultResp.setResultMsg("用户不是超级管理员，没有删除消息队列权限!");
                return resultResp;
            }

            if (!XmoPayUtils.isEmpty(mqids)) {  //批量删除
                String[] ids_ = mqids.split(",");
                for (String mqid_ : ids_) {
                    MessageQueueDto messageQueueDto = new MessageQueueDto();
                    messageQueueDto.setMqid(XmoPayUtils.isEmpty(mqid_) ? null : Integer.valueOf(mqid_));
                    messageQueueService.deleteMessageQueueByMqid(messageQueueDto);
                }

                log.info(logmsg, "批量删除成功!");
                resultResp.setResultMsg("批量删除成功!");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;
            } else if (!XmoPayUtils.isEmpty(mqid)) {//单独删除
                MessageQueueDto messageQueueDto = new MessageQueueDto();
                messageQueueDto.setMqid(Integer.valueOf(mqid));
                SingleResult<Integer> deleteResult = messageQueueService.deleteMessageQueueByMqid(messageQueueDto);
                if (deleteResult.isSuccess()) {
                    log.info(logmsg, "删除成功!");
                    resultResp.setResultMsg("删除成功!");
                    resultResp.setResultCode(XmopayResponse.SUCCESS);
                    return resultResp;
                } else {
                    log.error(logmsg, "删除失败!");
                    resultResp.setResultMsg("删除失败!");
                    return resultResp;
                }
            } else {
                log.error(logmsg, "参数为空!");
                resultResp.setResultMsg("参数为空!");
                return resultResp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服!");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    /**
     * @param
     * @return
     * @Description: [Action]批量修改线程状态为待处理
     */
    @ResponseBody
    @RequestMapping(value = "/updateQueue")
    public XmopayResponse<String> updateQueue(HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse<>(XmopayResponse.FAILURE,"修改线程状态失败",null);
        try {
            //记录日志需要
            AdminUserDto adminUser = WebCommon.getSessionUserInfo(request);
            Integer muid = adminUser.getMuId();
            //日志
            String logmsg = "[用户常规事件] 当前登录者=" + muid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

            String mqids = request.getParameter("mqids");
            if (XmoPayUtils.isEmpty(mqids)) {
                log.error(logmsg, "参数为空!");
                resultResp.setResultMsg("参数为空！");
                return resultResp;
            }
            String[] ids_ = mqids.split(",");
            for (String mqid : ids_) {
                MessageQueueDto messageQueueDto = new MessageQueueDto();
                messageQueueDto.setMqid(XmoPayUtils.isEmpty(mqid) ? null : Integer.valueOf(mqid));
                messageQueueService.updateQueueByMqid(messageQueueDto);
            }

            log.info(logmsg, "修改成功!");
            resultResp.setResultMsg("修改成功！");
            resultResp.setResultCode(XmopayResponse.SUCCESS);
            return resultResp;
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    /**
     * @param
     * @return
     * @Description: [页面]填写退款理由
     */
    @RequestMapping("refundReason")
    public String refundReason(HttpServletRequest request) {
        try {
            String orderSn   = request.getParameter("order_sn");
            String partnerId = request.getParameter("partner_id");

            if (XmoPayUtils.isEmpty(orderSn)) {
                return "sysadmin/index";
            }
            TradeOrderDto tradeOrderDto = new TradeOrderDto();
            tradeOrderDto.setPartnerId(partnerId);
            tradeOrderDto.setOrderSn(orderSn);

            SingleResult<TradeOrderDto> tradeOrderInfo = tradeOrderService.getTradeOrderInfo(tradeOrderDto);
            request.setAttribute("tradeOrderInfo", tradeOrderInfo.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "orders/refundReason";
    }

    /**
     * @param request
     * @return
     * @Description: [页面]退款订单列表
     */
    @RequestMapping(value = "/refund")
    public String refund(HttpServletRequest request) {
        try {
            String refundType    = request.getParameter("refund_type");
            String partnerId     = request.getParameter("partner_id");
            String orderSn       = request.getParameter("order_sn");
            String billingSn     = request.getParameter("billing_sn");
            String outTradeNo    = request.getParameter("out_trade_no"); //商户订单号
            String startTime     = request.getParameter("startTime");
            String endTime       = request.getParameter("endTime");
            String minPayMoney   = request.getParameter("min_paymoney");
            String maxPayMoney   = request.getParameter("max_paymoney");
            String refundStatus  = request.getParameter("refund_status");
            String inajax        = request.getParameter("inajax"); //ajax模式

            request.setAttribute("refund_type",   refundType);
            request.setAttribute("partner_id",    partnerId);
            request.setAttribute("order_sn",      orderSn);
            request.setAttribute("out_trade_no",  outTradeNo);
            request.setAttribute("min_paymoney",  minPayMoney);
            request.setAttribute("max_paymoney",  maxPayMoney);
            request.setAttribute("billing_sn",    billingSn);
            request.setAttribute("startTime",     startTime);
            request.setAttribute("endTime",       endTime);
            request.setAttribute("refund_status", refundStatus);
            request.setAttribute("inajax",        (inajax != null) ? inajax : "0"); //ajax分页

            RefundDto refundDto = new RefundDto();
            refundDto.setPartnerId(partnerId);
            refundDto.setRefundType(XmoPayUtils.isEmpty(refundType) ? null : Integer.valueOf(refundType));
            refundDto.setOrderSn(orderSn);
            refundDto.setMinPayMoney(minPayMoney);
            refundDto.setMaxPayMoney(maxPayMoney);
            refundDto.setOutTradeNo(outTradeNo);
            refundDto.setBillingSn(billingSn);
            refundDto.setStartTime(startTime);
            refundDto.setEndTime(endTime);
            refundDto.setRefundStatus(XmoPayUtils.isEmpty(refundStatus) ? null : Integer.valueOf(refundStatus));

            //分页显示记录
            String index  = request.getParameter("pageIndex");
            int pageIndex = XmoPayUtils.isEmpty(index) ? 1 : Integer.parseInt(index);
            refundDto.setCurrentPage(pageIndex);
            refundDto.setPageSize(20);
            SingleResult<PageInfo> singleResult = refundService.getRefundList(refundDto);
            if (singleResult.isSuccess()) {
                PageInfo pageUtils = singleResult.getResult();
                request.setAttribute("page", pageUtils);
                request.setAttribute("lists", pageUtils.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, pageUtils, "listDiv"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "orders/refund";
    }

}
