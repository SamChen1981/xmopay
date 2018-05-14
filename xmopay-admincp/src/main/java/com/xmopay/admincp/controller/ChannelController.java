package com.xmopay.admincp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.Pagesutils;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.common.WebCommon;
import com.xmopay.admincp.common.XmopayResponse;
import com.xmopay.admincp.dto.AdminUserDto;
import com.xmopay.admincp.dto.GatewayAgencyDto;
import com.xmopay.admincp.dto.GatewayBalanceDto;
import com.xmopay.admincp.dto.GatewayChannelDto;
import com.xmopay.admincp.service.GatewayAgencyService;
import com.xmopay.admincp.service.GatewayBalanceService;
import com.xmopay.admincp.service.GatewayChannelService;
import com.xmopay.common.utils.XmoPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "channel")
@Slf4j
public class ChannelController {

    @Autowired
    private GatewayAgencyService gatewayAgencyService;

    @Autowired
    private GatewayChannelService gatewayChannelService;

    @Autowired
    private GatewayBalanceService gatewayBalanceService;


    /**
     * 渠道列表
     *
     * @return
     */
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request) {
        try {
            String channelName = request.getParameter("channel_name");
            String channelCode = request.getParameter("channel_code");
            String agencyCode = request.getParameter("agency_code");
            Integer channelType = XmoPayUtils.isEmpty(request.getParameter("channel_type")) ? null : Integer.parseInt(request.getParameter("channel_type"));
            String inajax = request.getParameter("inajax");
            String pageIndex = request.getParameter("pageIndex");

            int pageIdx = !XmoPayUtils.isEmpty(pageIndex) ? Integer.parseInt(pageIndex) : 1;

            GatewayAgencyDto gatewayAgencyDto = new GatewayAgencyDto();
            gatewayAgencyDto.setAgencyCode(agencyCode);

            GatewayChannelDto gatewayChannelDto = new GatewayChannelDto();
            gatewayChannelDto.setCurrentPage(pageIdx);
            gatewayChannelDto.setChannelName(channelName);
            gatewayChannelDto.setChannelType(channelType);
            gatewayChannelDto.setChannelCode(channelCode);
            gatewayChannelDto.setGatewayAgencyDto(gatewayAgencyDto);
            gatewayChannelDto.setPageSize(20);
            SingleResult<PageInfo> singleResult = gatewayChannelService.getChannelPageList(gatewayChannelDto);
            if (singleResult.isSuccess()) {
                PageInfo page = singleResult.getResult();
                request.setAttribute("page", page);
                request.setAttribute("lists", page.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, page, "listDiv"));
            } else {
                request.setAttribute("lists", null);
            }

            request.setAttribute("gatewayChannel", gatewayChannelDto);
            request.setAttribute("inajax", (inajax != null) ? inajax : "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "configs/channel.index";
    }

    /**
     * 编辑渠道
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit")
    public String editChannel(HttpServletRequest request) {
        String channelId = request.getParameter("channelId");
        if (XmoPayUtils.isEmpty(channelId) || "0".equals(channelId)) {
            request.setAttribute("action", "addGatewayChannel");
        } else {
            SingleResult<GatewayChannelDto> gatewayChannelResult = gatewayChannelService.getGatewayChannelById(Integer.parseInt(channelId));
            if (gatewayChannelResult.isSuccess()) {
                GatewayChannelDto gatewayChannelDto = gatewayChannelResult.getResult();
                request.setAttribute("channelParamsList", JSON.parseObject(gatewayChannelDto.getChannelParams(), Map.class));
                request.setAttribute("channel", gatewayChannelDto);
            }
            request.setAttribute("action", "updateGatewayChannel");
        }

        // 机构
        GatewayAgencyDto gatewayAgencyDto = new GatewayAgencyDto();
        gatewayAgencyDto.setAgencyStatus(1);
        SingleResult<List<GatewayAgencyDto>> gatewayAgencyResult = gatewayAgencyService.findGatewayAgencyList(gatewayAgencyDto);
        if (gatewayAgencyResult.isSuccess()) {
            request.setAttribute("agencies", gatewayAgencyResult.getResult());
        }
        return "configs/channel.edit";
    }

    /**
     * 添加网关渠道
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "addGatewayChannel", method = RequestMethod.POST)
    public XmopayResponse<Integer> addGatewayChannel(HttpServletRequest request) {
        //记录日志需要
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid = sessionUser.getMuId();
        //日志
        String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        XmopayResponse<Integer> result = new XmopayResponse(XmopayResponse.FAILURE, "添加失败", 0);
        try {
            String status = request.getParameter("status");
            String channelKey = request.getParameter("channelKey");
            String channelType = request.getParameter("channelType");
            String channelName = request.getParameter("channelName");
            String channelAgency = request.getParameter("channelAgency");
            String channelParams = request.getParameter("channelParams");
            String channelSecret = request.getParameter("channelSecret");

            XmopayResponse<Integer> r1 = verifyChannelParams(request, result);
            if (1 > r1.getResultData()) {
                return result;
            }
            String[] channelAgencyArr = channelAgency.split("\\|");
            String agencyCode = channelAgencyArr[1];
            String[] agencyCodeArr = agencyCode.split("_");

            StringBuilder channelCode = new StringBuilder();
            channelCode.append(agencyCodeArr[0]);
            channelCode.append("_");
            channelCode.append("0".equals(channelType) ? "TRADE" : "TRANS");
            channelCode.append("_");
            channelCode.append(channelKey);

            GatewayChannelDto dto = new GatewayChannelDto();
            dto.setStatus(Integer.parseInt(status));
            dto.setAgencyId(Integer.parseInt(channelAgencyArr[0]));
            dto.setChannelCode(channelCode.toString());
            dto.setDateline(new Date());
            dto.setUpdateTime(new Date());
            dto.setChannelKey(channelKey);
            dto.setChannelType(Integer.parseInt(channelType));
            dto.setChannelName(channelName);
            dto.setChannelSecret(channelSecret);
            dto.setChannelParams(channelParams);
            SingleResult state = gatewayChannelService.insertGatewayChannel(dto);
            if (state.isSuccess()) {
                log.info(logmsg, "添加网关渠道成功!");
                result.setResultMsg("添加成功");
                result.setResultCode(XmopayResponse.SUCCESS);
            }
        } catch (Exception e) {
            log.error(logmsg, "添加异常！");
            e.printStackTrace();
            result.setResultCode(XmopayResponse.EXCEPTION);
            result.setResultMsg("添加异常");
        }
        return result;
    }

    /**
     * 修改渠道
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "updateGatewayChannel", method = RequestMethod.POST)
    public XmopayResponse<Integer> updateGatewayChannel(HttpServletRequest request) {
        //记录日志需要
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid          = sessionUser.getMuId();
        //日志
        String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        XmopayResponse<Integer> result = new XmopayResponse<>(XmopayResponse.FAILURE, "更新失败", 0);
        try {
            String channelId = request.getParameter("channelId");
            String status = request.getParameter("status");
            String channelKey = request.getParameter("channelKey");
            String channelType = request.getParameter("channelType");
            String channelName = request.getParameter("channelName");
            String channelAgency = request.getParameter("channelAgency");
            String channelParams = request.getParameter("channelParams");
            String channelSecret = request.getParameter("channelSecret");

            XmopayResponse<Integer> r1 = verifyChannelParams(request, result);
            if (1 > r1.getResultData()) {
                return result;
            }

            String[] channelAgencyArr = channelAgency.split("\\|");
            String agencyCode = channelAgencyArr[1];
            String[] agencyCodeArr = agencyCode.split("_");

            StringBuilder channelCode = new StringBuilder();
            channelCode.append(agencyCodeArr[1]);
            channelCode.append("_");
            channelCode.append("0".equals(channelType) ? "TRADE" : "TRANS");
            channelCode.append("_");
            channelCode.append(channelKey);

            GatewayChannelDto dto = new GatewayChannelDto();
            dto.setStatus(Integer.parseInt(status));
            dto.setAgencyId(Integer.parseInt(channelAgencyArr[0]));
            dto.setChannelCode(channelCode.toString());
            dto.setUpdateTime(new Date());
            dto.setChannelKey(channelKey);
            dto.setChannelType(Integer.parseInt(channelType));
            dto.setChannelName(channelName);
            dto.setChannelSecret(channelSecret);
            dto.setChannelParams(channelParams);
            dto.setChannelId(Integer.parseInt(channelId));
            SingleResult state = gatewayChannelService.updateGatewayChannel(dto);
            if (state.isSuccess()) {
                log.info(logmsg, "修改网关渠道成功!");
                result.setResultMsg("更新成功");
                result.setResultCode(XmopayResponse.SUCCESS);
            }
        } catch (Exception e) {
            log.error(logmsg, "更新异常！");
            result.setResultData(0);
            result.setResultMsg("更新异常");
            result.setResultCode(XmopayResponse.EXCEPTION);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除渠道
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "deleteGatewayChannel", method = RequestMethod.POST)
    public XmopayResponse<Integer> deleteGatewayChannel(@RequestParam(value = "channel_id") Integer channelId,
                                                          HttpServletRequest request) {
        XmopayResponse<Integer> respResult = new XmopayResponse<>(XmopayResponse.FAILURE, "删除失败", 0);
        try {
            if (channelId == null || channelId <= 0) {
                respResult.setResultMsg("参数异常");
                return respResult;
            }
            SingleResult<Integer> result = gatewayChannelService.deleteGatewayChannel(channelId);
            if (result.isSuccess()) {
                // 添加操作日志
                String logmsg = "当前登录者=" + WebCommon.getSessionUserInfo(request).getMuId()
                        + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap())
                        + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);
                log.info(ChannelController.class.getSimpleName() + ".deleteGatewayChannel(), " + logmsg);

                respResult.setResultMsg("删除成功");
                respResult.setResultCode(XmopayResponse.SUCCESS);
                return respResult;
            }
        } catch (Exception e) {
            log.error(ChannelController.class.getSimpleName() + ".deleteGatewayChannel(), 删除异常=" + e.getMessage());
            respResult.setResultMsg("删除异常");
            respResult.setResultCode(XmopayResponse.EXCEPTION);
        }
        return respResult;
    }

    /**
     * 机构列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/agencyList")
    public String agencyList(HttpServletRequest request) {
        try {
            String inajax = request.getParameter("inajax");
            String agencyName = request.getParameter("agencyName");
            String pageIndex = request.getParameter("pageIndex");
            int pageIdx = !XmoPayUtils.isEmpty(pageIndex) ? Integer.parseInt(pageIndex) : 1;

            GatewayAgencyDto dto = new GatewayAgencyDto();
            dto.setCurrentPage(pageIdx);
            dto.setAgencyName(agencyName);
            dto.setPageSize(20);
            SingleResult<PageInfo> singleResult = gatewayAgencyService.getGatewayAgencyPageList(dto);
            if (singleResult.isSuccess()) {
                PageInfo page = singleResult.getResult();
                request.setAttribute("page", page);
                request.setAttribute("lists", page.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, page, "listDiv"));
            } else {
                request.setAttribute("lists", null);
            }
            request.setAttribute("gatewayAgency", dto);
            request.setAttribute("inajax", (inajax != null) ? inajax : "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "configs/channel.agency.list";
    }

    /**
     * 机构编辑
     * @return
     */
    @RequestMapping(value = "/editAgency")
    public String editAgency(HttpServletRequest request) {
        String gaId = request.getParameter("gaid");
        if (XmoPayUtils.isEmpty(gaId) || "0".equals(gaId)) {
            request.setAttribute("action", "doAddAgency");
        } else {
            SingleResult<GatewayAgencyDto> result = gatewayAgencyService.getGatewayAgencyById(Integer.parseInt(gaId));
            if (result.isSuccess()) {
                GatewayAgencyDto dto = result.getResult();
                String agencyCode = dto.getAgencyCode();
                request.setAttribute("agencyCode", agencyCode.substring(0, agencyCode.lastIndexOf("_")));
                request.setAttribute("agencyType", agencyCode.substring(agencyCode.lastIndexOf("_") + 1, agencyCode.length()));
                request.setAttribute("agencyParamsList", JSON.parseObject(dto.getAgencyParams(), Map.class));
                request.setAttribute("agency", dto);
            }
            request.setAttribute("action", "updateAgency");
        }
        return "configs/channel.agency.edit";
    }

    /**
     * 添加机构
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "doAddAgency", method = RequestMethod.POST)
    public XmopayResponse<Integer> doAddAgency(HttpServletRequest request) {
        XmopayResponse<Integer> respResult = new XmopayResponse<>(XmopayResponse.FAILURE, "添加失败", 0);

        //记录日志需要
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid      = sessionUser.getMuId();
        //日志
        String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        try {
            String agencyName = request.getParameter("agencyName");
            String agencyType = request.getParameter("agencyType");
            String agencyCode = request.getParameter("agencyCode");
            String agencyStatus = request.getParameter("agencyStatus");
            String agencyParams = request.getParameter("agencyParams");
            if (XmoPayUtils.isEmpty(agencyName)) {
                log.error(logmsg, "请填写机构名称！");
                respResult.setResultMsg("请填写机构名称");
                return respResult;
            }

            if (XmoPayUtils.isEmpty(agencyType)) {
                log.error(logmsg, "请填写机构类型！");
                respResult.setResultMsg("请填写机构类型");
                return respResult;
            }

            if (XmoPayUtils.isEmpty(agencyCode)) {
                log.error(logmsg, "请填写机构代码！");
                respResult.setResultMsg("请填写机构代码");
                return respResult;
            }

            respResult = checkExtendsParams(respResult, agencyParams);
            if (respResult.getResultData() < 1) {
                return respResult;
            }

            GatewayAgencyDto dto = new GatewayAgencyDto();
            dto.setAgencyName(agencyName);
            dto.setAgencyCode(agencyCode + "_" + agencyType);
            dto.setAgencyParams(agencyParams);
            dto.setAgencyStatus(Integer.parseInt(agencyStatus));
            dto.setUpdateTime(new Date());
            dto.setDateline(new Date());
            SingleResult<Integer> state = gatewayAgencyService.insertGatewayAgency(dto);
            if (state.isSuccess()) {
                log.info(logmsg, "添加机构成功!");
                respResult.setResultMsg("添加成功");
                respResult.setResultCode(XmopayResponse.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(logmsg, "添加异常！");
            respResult.setResultMsg("添加异常");
            respResult.setResultCode(XmopayResponse.EXCEPTION);
        }
        return respResult;
    }

    /**
     * 更新机构
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "updateAgency", method = RequestMethod.POST)
    public XmopayResponse<Integer> updateAgency(HttpServletRequest request) {
        XmopayResponse<Integer> result = new XmopayResponse<>(XmopayResponse.FAILURE, "更新失败", 0);
        //记录日志需要
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid          = sessionUser.getMuId();
        //日志
        String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        try {
            String gaid = request.getParameter("gaid");
            String agencyName = request.getParameter("agencyName");
            String agencyType = request.getParameter("agencyType");
            String agencyCode = request.getParameter("agencyCode");
            String agencyStatus = request.getParameter("agencyStatus");
            String agencyParams = request.getParameter("agencyParams");
            if (XmoPayUtils.isEmpty(agencyName)) {
                log.error(logmsg, "请填写机构名称！");
                result.setResultMsg("请填写机构名称");
                return result;
            }

            if (XmoPayUtils.isEmpty(agencyType)) {
                log.error(logmsg, "请填写机构类型！");
                result.setResultMsg("请填写机构类型");
                return result;
            }

            if (XmoPayUtils.isEmpty(agencyCode)) {
                log.error(logmsg, "请填写机构代码！");
                result.setResultMsg("请填写机构代码");
                return result;
            }

            result = checkExtendsParams(result, agencyParams);
            if (result.getResultData() < 1) {
                return result;
            }

            GatewayAgencyDto dto = new GatewayAgencyDto();
            dto.setGaid(Integer.parseInt(gaid));
            dto.setAgencyName(agencyName);
            dto.setAgencyCode(agencyCode + "_" + agencyType);
            dto.setAgencyParams(agencyParams);
            dto.setAgencyStatus(Integer.parseInt(agencyStatus));
            dto.setUpdateTime(new Date());

            SingleResult state = gatewayAgencyService.updateGatewayAgency(dto);
            if (state.isSuccess()) {
                log.info(logmsg, "更新机构成功!");
                result.setResultMsg("更新成功");
                result.setResultCode(XmopayResponse.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(logmsg, "更新异常！");
            result.setResultMsg("更新异常");
            result.setResultCode(XmopayResponse.EXCEPTION);
        }
        return result;
    }

    /**
     * @param
     * @return
     * @Description: [页面]渠道余额列表
     */
    @RequestMapping(value = "balanceList")
    public String balanceList(HttpServletRequest request) {
        try {
            String gateway_code = request.getParameter("gateway_code");
            String gateway_name = request.getParameter("gateway_name");
            String batch_id = request.getParameter("batch_id");
            String inajax = request.getParameter("inajax"); //ajax模式

            request.setAttribute("gateway_code", gateway_code);
            request.setAttribute("gateway_name", gateway_name);
            request.setAttribute("batch_id", batch_id);
            request.setAttribute("inajax", (inajax != null) ? inajax : "0"); //ajax分页

            GatewayBalanceDto gatewayBalanceDto = new GatewayBalanceDto();
            gatewayBalanceDto.setBatchId(batch_id);
            gatewayBalanceDto.setGatewayCode(gateway_code);
            gatewayBalanceDto.setGatewayName(gateway_name);
            //查询时间的起止时间为 当前时间前一个小时至当前时间
            String startTime = DateFormatUtils.format(DateUtils.addHours(new Date(), -1), "yyyy-MM-dd HH:mm:ss");
            String endTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            gatewayBalanceDto.setStartTime(startTime);
            gatewayBalanceDto.setEndTime(endTime);

            String index = request.getParameter("pageIndex");
            int pageIndex = XmoPayUtils.isEmpty(index) ? 1 : Integer.valueOf(index);//当前第一页
            int pageSize = 20;//每页显示的记录
            gatewayBalanceDto.setCurrentPage(pageIndex);
            gatewayBalanceDto.setPageSize(pageSize);

            SingleResult<PageInfo> singleResult = gatewayBalanceService.getGatewayBalancePageList(gatewayBalanceDto);
            if (singleResult.isSuccess()) {
                PageInfo page = singleResult.getResult();
                request.setAttribute("page", page);
                request.setAttribute("lists", page.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, page, "listDiv"));
            }
            SingleResult<Map> totalResult = gatewayBalanceService.getTotal(gatewayBalanceDto);
            if (totalResult.isSuccess()) {
                request.setAttribute("balance_total", totalResult.getResult().get("BALANCE_TOTAL"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "configs/channelBalance";
    }

    /**
     * @Description: 一键同步渠道余额查询
     * @returns:
     */
    @ResponseBody
    @RequestMapping(value = "synchBalance", method = RequestMethod.POST)
    public XmopayResponse<String> synchBalance(HttpServletRequest request) {
        XmopayResponse<String> resultResp = new XmopayResponse(XmopayResponse.FAILURE, "", null);
        try {
            //记录日志需要
            AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
            int sessionMuid = sessionUser.getMuId();
            //日志
            String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

            // 开启状态 0关闭 1开启',
            // 网关类型 0＝支付网关 1=下发网关'
            GatewayChannelDto gatewayChannelDto = new GatewayChannelDto();
            gatewayChannelDto.setChannelCode("JYTPAY");
            gatewayChannelDto.setChannelType(1); //网关类型 0＝支付网关 1=下发网关
            gatewayChannelDto.setStatus(1);

            SingleResult<List<GatewayChannelDto>> listSingleResult = gatewayChannelService.getAutoGatewayChannelList(gatewayChannelDto);
            if (!listSingleResult.isSuccess()) {
                log.error(logmsg, "手动查询渠道余额：暂无需要查询余额的渠道");
                resultResp.setResultMsg("手动查询渠道余额：暂无需要查询余额的渠道");
                return resultResp;
            }
            List<GatewayChannelDto> gatewayList = listSingleResult.getResult();
            log.error(logmsg, "手动查询渠道余额：有" + gatewayList.size() + "个渠道");
            if (gatewayList == null || gatewayList.size() == 0) {
                log.error(logmsg, "手动查询渠道余额：暂无需要查询余额的渠道");
                resultResp.setResultMsg("手动查询渠道余额：暂无需要查询余额的渠道");
                return resultResp;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String batch_id = sdf.format(new Date());
            for (GatewayChannelDto gatewayChannel : gatewayList) {
                String CHANNEL = gatewayChannel.getChannelCode().split("_")[0];
                if (!CHANNEL.contains("DINPAY") && !CHANNEL.contains("JYTPAY") && !CHANNEL.contains("YMDPAY")) {
                    continue;
                }

                Map params = new HashMap();
                params.put("CHANNEL_CODE", gatewayChannel.getChannelCode());
                params.put("CHANNEL", CHANNEL);
                params.put("CHANNEL_PARAMS", gatewayChannel.getChannelParams());
                params.put("CHANNEL_KEY", gatewayChannel.getChannelKey());
                params.put("CHANNEL_SECRET", gatewayChannel.getChannelSecret());

                String agencyParams = gatewayChannel.getGatewayAgencyDto().getAgencyParams();
                JSONObject agency_params = new JSONObject();
                if (!XmoPayUtils.isEmpty(agencyParams)) {
                    agency_params = JSON.parseObject(agencyParams.toString());
                }
                String transfer_query_url = agency_params.containsKey("transfer_query_url") ? agency_params.getString("transfer_query_url") : "";
                params.put("TRANSFER_QUERY_URL", transfer_query_url);
                //对接查余额接口
                SingleResult<Map> singleResult = null;
//                try {
//                    BaseChannelSerivce baseChannelSerivce = (BaseChannelSerivce) BeanTool.getBean(CHANNEL+"ChannelServiceImpl");
//                    singleResult = baseChannelSerivce.doAccountBalance(params);
//                }catch (Exception e){
//                    e.printStackTrace();
//                    continue;
//                }
                if (singleResult == null) {
                    log.info(logmsg, "手动查询网关余额：网关 channel_code=" + gatewayChannel.getChannelCode() + ", channel_name=" + gatewayChannel.getChannelName() + ", 查询渠道余额失败，未实现其接口");
                    continue;
                }
                if (!singleResult.isSuccess()) {
                    log.info(logmsg, "手动查询网关余额：网关 channel_code=" + gatewayChannel.getChannelCode() + ", channel_name=" + gatewayChannel.getChannelName() + ", retMessage=");
                    continue;
                }
                Map queryResultMap = singleResult.getResult();
                if (queryResultMap == null) {
                    log.info(logmsg, "手动查询网关余额：网关 channel_code=" + gatewayChannel.getChannelCode() + ", channel_name=" + gatewayChannel.getChannelName() + ", 返回值Map为空 resultMap=" + queryResultMap);
                    continue;
                }

                log.info(logmsg, "手动查询网关余额：网关 channel_code=" + gatewayChannel.getChannelCode() + ", channel_name=" + gatewayChannel.getChannelName() + ", resultMap=" + queryResultMap);

                //更新网关
                GatewayChannelDto gatewayChannelDto1 = new GatewayChannelDto();
                gatewayChannelDto1.setChannelCode(gatewayChannel.getChannelCode());
                gatewayChannelDto1.setChannelBalance(queryResultMap.get("balance").toString());
                SingleResult<Integer> integerSingleResult = gatewayChannelService.updateAutoGatewayChannelByChannelId(gatewayChannelDto1);
                if (integerSingleResult.isSuccess()) {
                    log.info("[网关余额同步] 网关名 channel_name=" + gatewayChannel.getChannelName() + ", channel_code=" + gatewayChannel.getChannelCode() + ", 接口响应成功，更新数据成功");
                } else {
                    log.info("[网关余额同步] 网关名 channel_name=" + gatewayChannel.getChannelName() + ", channel_code=" + gatewayChannel.getChannelCode() + ", 接口响应成功，更新数据失败, ");
                }
                //记录进入明细
                GatewayBalanceDto gatewayBalance = new GatewayBalanceDto();
                gatewayBalance.setGatewayType(gatewayChannel.getChannelType());
                gatewayBalance.setGatewayCode(gatewayChannel.getChannelCode());
                gatewayBalance.setGatewayName(gatewayChannel.getChannelName());
                gatewayBalance.setBalance(new BigDecimal(queryResultMap.get("balance").toString()));
                gatewayBalance.setLastTradeTime(queryResultMap.get("query_time") == null ? new Date() : DateUtils.parseDate(queryResultMap.get("query_time").toString(), new String[]{"yyyyMMddHHmmss"}));
                gatewayBalance.setDateline(new Date());
                gatewayBalance.setMerViralAcct(queryResultMap.get("mer_viral_acct").toString());
                gatewayBalance.setBatchId(batch_id);
                SingleResult<Integer> integerSingleResultBalance = gatewayBalanceService.insertGatewayBalance(gatewayBalance);
                if (integerSingleResultBalance.isSuccess()) {
                    log.info("[网关余额同步] 网关名称 channel_name=" + gatewayChannel.getChannelName() + ", 网关代码 channel_code=" + gatewayChannel.getChannelCode() + ", 接口响应成功，更新数据成功=" + queryResultMap.get("msg"));
                }
                // 删除历史记录
                GatewayBalanceDto gatewayBalanceDto = new GatewayBalanceDto();
                gatewayBalanceDto.setDateline(DateUtils.addSeconds(new Date(), -3600));
                SingleResult<Integer> delResult = gatewayBalanceService.deleteGatewayBalance(gatewayBalanceDto);
                if (delResult.isSuccess() && delResult.getResult() > 0) {
                    log.info("[网关余额同步] 网关名称 channel_name=" + gatewayChannel.getChannelName() + ", 网关代码 channel_code=" + gatewayChannel.getChannelCode() + ", 删除3600秒之前数据成功!");
                } else {
                    log.info("[网关余额同步] 网关名称 channel_name=" + gatewayChannel.getChannelName() + ", 网关代码 channel_code=" + gatewayChannel.getChannelCode() + ", 删除记录失败");
                }
            }
            log.info(logmsg, "手动同步成功！");
            resultResp.setResultMsg("手动同步成功!");
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
     * 检查渠道参数
     * @param request
     * @param result
     * @return
     */
    private XmopayResponse<Integer> verifyChannelParams(HttpServletRequest request, XmopayResponse<Integer> result) {
        //记录日志需要
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        int sessionMuid          = sessionUser.getMuId();
        //日志
        String logmsg = "当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        result.setResultData(0);
        String channelName = request.getParameter("channelName");
        if (XmoPayUtils.isEmpty(channelName)) {
            log.error(logmsg, "渠道名称不可为空！");
            result.setResultMsg("渠道名称不可为空!");
            return result;
        }
        log.info(logmsg, "检查渠道参数成功！");
        result.setResultData(1);
        return result;
    }

    private XmopayResponse<Integer> checkExtendsParams(XmopayResponse result, String agencyParams) {
        result.setResultData(0);
        try {
            Map<String, String> data = JSON.parseObject(agencyParams, Map.class);
            Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                if (XmoPayUtils.isEmpty(entry.getKey())) {
                    result.setResultMsg("其他参数的KEY不可为空!");
                    return result;
                }
                if (XmoPayUtils.isEmpty(entry.getValue())) {
                    result.setResultMsg("其他参数的VALUE不可为空!");
                    return result;
                }
            }
            result.setResultData(1);
        } catch (Exception e) {
        }
        return result;
    }
}
