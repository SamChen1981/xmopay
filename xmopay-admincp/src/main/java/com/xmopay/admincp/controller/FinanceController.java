package com.xmopay.admincp.controller;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.Pagesutils;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.common.WebCommon;
import com.xmopay.admincp.common.XmopayResponse;
import com.xmopay.admincp.dto.AdminUserDto;
import com.xmopay.admincp.dto.BillingsDto;
import com.xmopay.admincp.service.BillingsService;
import com.xmopay.common.constant.StatusConstants;
import com.xmopay.common.utils.XmoPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping(value = "finance")
@Slf4j
public class FinanceController {

    @Autowired
    private BillingsService billingsService;

    /**
     * @param
     * @return
     * @Description: [页面]财务管理 - 财务明细
     */
    @RequestMapping(value = "index")
    public String index(HttpServletRequest request) {
        try {
            String startTime   = request.getParameter("startTime");
            String endTime     = request.getParameter("endTime");
            String partnerId   = request.getParameter("partner_id");
            String orderSn     = request.getParameter("order_sn");
            String billingSn   = request.getParameter("billing_sn");
            String minPayMoney = request.getParameter("min_paymoney");
            String maxPayMoney = request.getParameter("max_paymoney");
            String billType    = request.getParameter("bill_type");//流水类型
            String inajax      = request.getParameter("inajax"); //ajax模式

            BillingsDto billingsDto = new BillingsDto();
            billingsDto.setPartnerId(partnerId);
            billingsDto.setOrderSn(orderSn);
            billingsDto.setBillingSn(billingSn);
            billingsDto.setPayStatus(StatusConstants.TRADE_SUCCESS);//后台查询交易成功订单
            billingsDto.setBillType(XmoPayUtils.isEmpty(billType) ? null : Integer.valueOf(billType));
            billingsDto.setMinPayMoney(minPayMoney);
            billingsDto.setMaxPayMoney(maxPayMoney);

            //如果开始时间和结束时间都为空就默认显示前3天
            if (XmoPayUtils.isEmpty(startTime) && XmoPayUtils.isEmpty(endTime)) {
                startTime = DateFormatUtils.format(DateUtils.addDays(new Date(), -3), "yyyy-MM-dd 00:00:00");
                endTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
            }
            billingsDto.setStartTime(startTime);
            billingsDto.setEndTime(endTime);

            request.setAttribute("partner_id",   partnerId);
            request.setAttribute("order_sn",     orderSn);
            request.setAttribute("billing_sn",   billingSn);
            request.setAttribute("startTime",    startTime);
            request.setAttribute("endTime",      endTime);
            request.setAttribute("min_paymoney", minPayMoney);
            request.setAttribute("max_paymoney", maxPayMoney);
            request.setAttribute("bill_type",    billType);
            request.setAttribute("paramMap",     billingsDto);
            request.setAttribute("inajax",       (inajax != null) ? inajax : "0"); //ajax分页

            String index  = request.getParameter("pageIndex");
            int pageIndex = XmoPayUtils.isEmpty(index) ? 1 : Integer.valueOf(index);//当前第一页
            int pageSize  = 20;//每页显示的记录

            billingsDto.setCurrentPage(pageIndex);
            billingsDto.setPageSize(pageSize);
            billingsDto.setSortString("TRADE_TIME.desc");
            SingleResult<PageInfo> singleResult = billingsService.getBillingsList(billingsDto);
            if (singleResult.isSuccess()) {
                PageInfo pageUtils = singleResult.getResult();
                request.setAttribute("page", pageUtils);
                request.setAttribute("lists", pageUtils.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, pageUtils, "listDiv"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "finance/index";
    }

    /**
     * @param
     * @Description: [Action]交易信息汇总
     */
    @ResponseBody
    @RequestMapping(value = "getTotal")
    public XmopayResponse<Map> getTotal(HttpServletRequest request){
        XmopayResponse<Map> result = new XmopayResponse<>(XmopayResponse.FAILURE, "",null);
        //记录日志需要
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        int sessionMuid = sessionUser.getMuId();
        //日志
        String logmsg = "[用户常规事件] 当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);
        try{
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String partnerId = request.getParameter("partner_id");    // 商户ID
            String orderSn = request.getParameter("order_sn");
            String billingSn = request.getParameter("billing_sn");
            String minPaymoney = request.getParameter("min_paymoney");
            String maxPaymoney = request.getParameter("max_paymoney");
            String billType = request.getParameter("bill_type");//流水类型

            BillingsDto billingsDto = new BillingsDto();
            billingsDto.setPartnerId(partnerId);
            billingsDto.setOrderSn(orderSn);
            billingsDto.setBillingSn(billingSn);
            billingsDto.setPayStatus(StatusConstants.TRADE_SUCCESS);
            billingsDto.setBillType(XmoPayUtils.isEmpty(billType) ? null : Integer.valueOf(billType));
            billingsDto.setMinPayMoney(minPaymoney);
            billingsDto.setMaxPayMoney(maxPaymoney);

            //如果开始时间和结束时间都为空就默认显示前3天
            if (XmoPayUtils.isEmpty(startTime) && XmoPayUtils.isEmpty(endTime)) {
                startTime = DateFormatUtils.format(DateUtils.addDays(new Date(), -3), "yyyy-MM-dd 00:00:00");
                endTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
            }
            billingsDto.setStartTime(startTime);
            billingsDto.setEndTime(endTime);
            SingleResult<Map> billingsTotal = billingsService.getBillingsTotal(billingsDto);
            if(billingsTotal.isSuccess()){
                log.info(logmsg,"查询交易汇总信息成功");
                result.setResultData(billingsTotal.getResult());
                result.setResultCode(XmopayResponse.SUCCESS);
                return result;
            }
            log.info(logmsg,"查询交易汇总信息失败！");
            result.setResultMsg("查询交易汇总信息失败！");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            log.info(logmsg,"查询交易汇总信息异常！");
            result.setResultMsg("查询交易汇总信息异常！");
            result.setResultCode(XmopayResponse.EXCEPTION);
            return result;
        }
    }

}
