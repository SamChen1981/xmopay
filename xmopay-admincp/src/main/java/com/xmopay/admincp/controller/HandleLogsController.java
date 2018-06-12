package com.xmopay.admincp.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.Pagesutils;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.common.XmopayResponse;
import com.xmopay.admincp.dto.HandleLogsDto;
import com.xmopay.admincp.service.HandleLogsService;
import com.xmopay.common.utils.OpenIPAPI;
import com.xmopay.common.utils.XmoPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "hlogs")
@Slf4j
public class HandleLogsController {

    @Autowired
    private HandleLogsService handleLogsService;

    /**
     * @param
     * @return
     * @Description: 日志列表
     */
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request) {
        try {
            String puserid      = request.getParameter("puserid");
            String partnerId    = request.getParameter("partner_id");
            String orderSn      = request.getParameter("order_sn");
            String handleType   = request.getParameter("handle_type");
            String handleParams = request.getParameter("handle_params");
            String handleCode = request.getParameter("handle_code");
            String inajax       = request.getParameter("inajax");

            request.setAttribute("puserid",       puserid);
            request.setAttribute("partner_id",    partnerId);
            request.setAttribute("order_sn",      orderSn);
            request.setAttribute("handle_type",   handleType);
            request.setAttribute("handle_params", handleParams);
            request.setAttribute("handle_code", handleCode);
            request.setAttribute("inajax",        (inajax != null) ? inajax : "0"); //ajax分页

            HandleLogsDto handleLogsDto = new HandleLogsDto();
            handleLogsDto.setPuserid(puserid);
            handleLogsDto.setPartnerId(partnerId);
            handleLogsDto.setOrderSn(orderSn);
            handleLogsDto.setHandleType(handleType);
            handleLogsDto.setHandleParams(handleParams);
            handleLogsDto.setHandleCode(handleCode);

            // 分页显示记录
            String index  = request.getParameter("pageIndex");
            Integer pageIndex = XmoPayUtils.isEmpty(index) ? 1 : Integer.parseInt(index);
            Integer pageSize  = 15;

            handleLogsDto.setCurrentPage(pageIndex);
            handleLogsDto.setPageSize(pageSize);
            SingleResult<PageInfo> logsResult = handleLogsService.getHandleLogsList(handleLogsDto);
            if (logsResult.isSuccess()) {
                PageInfo pageUtils = logsResult.getResult();
                request.setAttribute("page", pageUtils);
                request.setAttribute("lists", pageUtils.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, pageUtils, "listDiv"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admincp/handle.logs";
    }

    @ResponseBody
    @RequestMapping(value = "/getIpInfo")
    public XmopayResponse<String> getIpInfo(HttpServletRequest request){
        XmopayResponse<String> resultResp = new XmopayResponse(XmopayResponse.FAILURE, "", null);
        try {
            String hlid = request.getParameter("hlid");
            if(XmoPayUtils.isEmpty(hlid)) {
                resultResp.setResultMsg("更新失败");
                resultResp.setResultCode(XmopayResponse.FAILURE);
                return resultResp;
            }
            HandleLogsDto handleLogsDto = new HandleLogsDto();
            handleLogsDto.setHlid(Integer.parseInt(hlid));

            SingleResult<HandleLogsDto> resultH = handleLogsService.getHandleLogsById(handleLogsDto);
            if (!resultH.isSuccess()) {
                resultResp.setResultMsg("更新失败");
                resultResp.setResultCode(XmopayResponse.FAILURE);
                return resultResp;
            }
            if( !XmoPayUtils.isEmpty(resultH.getResult().getIpInfo()) ) {
                resultResp.setResultMsg("无需更新");
                resultResp.setResultCode(XmopayResponse.FAILURE);
                return resultResp;
            }

            //这里分析IP
            JSONObject json = OpenIPAPI.getQueryIP(resultH.getResult().getHandleIp());
            log.info("["+resultH.getResult().getHandleIp()+"]-IP信息："+json);
            if(XmoPayUtils.isNull(json)) {
                resultResp.setResultMsg("更新失败");
                resultResp.setResultCode(XmopayResponse.FAILURE);
                return resultResp;
            }
            handleLogsDto.setHandleIp(resultH.getResult().getHandleIp());
            handleLogsDto.setIpInfo(json.toJSONString());

            handleLogsService.updateHandleLogs(handleLogsDto);
            resultResp.setResultMsg("更新成功");
            resultResp.setResultCode(XmopayResponse.SUCCESS);
            return resultResp;
        }catch (Exception e){
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }
}
