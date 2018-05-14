package com.xmopay.admincp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.Pagesutils;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.common.WebCommon;
import com.xmopay.admincp.common.XmopayResponse;
import com.xmopay.admincp.dto.AdminUserDto;
import com.xmopay.admincp.dto.GatewayChannelDto;
import com.xmopay.admincp.dto.PartnerDto;
import com.xmopay.admincp.dto.PartnerProductDto;
import com.xmopay.admincp.service.GatewayChannelService;
import com.xmopay.admincp.service.PartnerProductService;
import com.xmopay.admincp.service.PartnerService;
import com.xmopay.common.utils.XmoPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping(value = "partners")
@Slf4j
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PartnerProductService partnerProductService;

    @Autowired
    private GatewayChannelService gatewayChannelService;

    /**
     * 商户列表
     * @param req
     * @return
     */
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest req) {
        try {
            String partnerId = req.getParameter("partner_id");
            String partnerName = req.getParameter("partner_name");
            String status = req.getParameter("status");
            String inajax = req.getParameter("inajax");

            req.setAttribute("inajax", (inajax != null) ? inajax : "0");
            // 分页显示记录
            String index = req.getParameter("pageIndex");
            Integer pageIndex = XmoPayUtils.isEmpty(index) ? 1 : Integer.parseInt(index);
            PartnerDto partnerDto = new PartnerDto();
            partnerDto.setCurrentPage(pageIndex);
            partnerDto.setPageSize(20);
            partnerDto.setPartnerName(partnerName);
            partnerDto.setStatus(XmoPayUtils.isEmpty(status) ? null : Integer.parseInt(status));
            partnerDto.setPartnerId(partnerId);

            req.setAttribute("partner", partnerDto);
            SingleResult<PageInfo> singleResult = partnerService.getPartnerPageList(partnerDto);
            if (singleResult.isSuccess()) {
                PageInfo page = singleResult.getResult();
                req.setAttribute("page", page);
                req.setAttribute("lists", page.getList());
                req.setAttribute("pages", Pagesutils.showAjaxPage(req, page, "listDiv"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "partners/partners.list";
    }


    /**
     * @Description: 注册商户
     * @returns:
     */
    @RequestMapping(value = "/signup")
    public String signup(HttpServletRequest request) {
        return "partners/partners.signup";
    }

    /**
     * @Description: 注册
     * @returns:
     */
    @ResponseBody
    @RequestMapping(value = "/register")
    public XmopayResponse<String> register(HttpServletRequest req) {
        XmopayResponse<String> resultResp = new XmopayResponse(XmopayResponse.FAILURE,"注册失败",null);
        try {
            //记录日志需要
            AdminUserDto sessionUser = WebCommon.getSessionUserInfo(req);
            Integer sessionMuid = sessionUser.getMuId();
            //日志
            String logmsg = "[用户常规事件] 当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(req.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(req);

            String partnerName = req.getParameter("partner_name");
            String email     = req.getParameter("email");
            String appTitle  = req.getParameter("app_title");
            String appDomain = req.getParameter("app_domain");
            String appGatewayUrl = req.getParameter("app_gateway_url");
            String status     = req.getParameter("status");

            if (XmoPayUtils.isEmpty(partnerName)) {
                log.error(logmsg, "商户名为空,请输入！");
                resultResp.setResultMsg("商户名为空,请输入!");
                return resultResp;
            }
            if (XmoPayUtils.isEmpty(email)) {
                log.error(logmsg, "邮箱为空,请输入!");
                resultResp.setResultMsg("邮箱为空,请输入!");
                return resultResp;
            }
            // ===========商户表===========
            // #{partnerId}, #{partnerName}, #{partnerInfo}, #{partnerMd5Key}, #{createTime}, #{updateTime}, #{apiStatus}, #{status}
            String userpwd = XmoPayUtils.generatePassword();
            String partnerId = XmoPayUtils.getRandomNum(6);
            String salts = XmoPayUtils.genRandomNum(6);

            PartnerDto partnerDto = new PartnerDto();
            partnerDto.setPartnerId(partnerId);
            partnerDto.setStatus(Integer.parseInt(status));
            partnerDto.setApiStatus(-1);
            partnerDto.setPartnerName(partnerName);
            partnerDto.setPartnerMd5Key(XmoPayUtils.MD5(XmoPayUtils.getCertifiedSigned(userpwd, salts) + partnerId));
            partnerDto.setCreateTime(new Date());
            partnerDto.setUpdateTime(new Date());

            JSONObject infoJson = new JSONObject();
            infoJson.put("email", email);
            infoJson.put("userpwd", userpwd);
            infoJson.put("salts", salts);
            infoJson.put("app_title", appTitle);
            infoJson.put("app_gateway_url", appGatewayUrl);
            infoJson.put("app_domain", appDomain);
            partnerDto.setPartnerInfo(infoJson.toJSONString());

            // #{partnerId}, #{balance}, #{freezeBalance}, #{hashCode}
            partnerDto.setBalance(BigDecimal.ZERO);
            partnerDto.setFreezeBalance(BigDecimal.ZERO);
            partnerDto.setHashCode(XmoPayUtils.getRandomNum(8));

            SingleResult<Integer> singleResult = partnerService.insertPartner(partnerDto);
            if (singleResult.isSuccess() && singleResult.getResult() == 1) {
                log.info(logmsg, "注册成功！");
                resultResp.setResultMsg("注册成功!");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;
            } else {
                log.error(logmsg, "注册失败！");
                resultResp.setResultMsg("注册失败!");
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
     * 跳转到编辑商户页面／跳转到操作员页面
     * @param req
     * @return
     */
    @RequestMapping(value = "/toEdit")
    public String toEdit(HttpServletRequest req) {
        try {
            String ptid = req.getParameter("ptid");
            String searchRange = req.getParameter("searchRange");

            req.setAttribute("searchRange", searchRange);

            if (!XmoPayUtils.isNull(ptid)) {
                PartnerDto partnerDto = new PartnerDto();
                partnerDto.setPtid(XmoPayUtils.isEmpty(ptid) ? null : Integer.parseInt(ptid));
                SingleResult<List<PartnerDto>> singleResult = partnerService.getPartnersList(partnerDto);
                if (singleResult.isSuccess()) {
                    PartnerDto partnerDtoResult = singleResult.getResult().get(0);
                    if (!XmoPayUtils.isEmpty(partnerDtoResult.getPartnerInfo()) && partnerDtoResult.getPartnerInfo().startsWith("{")) {
                        try {
                            Map partnerInfo = JSON.parseObject(partnerDtoResult.getPartnerInfo(), Map.class);
                            req.setAttribute("partnerInfo", partnerInfo);
                        } catch (Exception e) {
                        }
                    }
                    req.setAttribute("puserMap", partnerDtoResult);
                }
                return "partners/partners.form";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/partner/index?partner_type";
    }

    /**
     * @Description: 编辑商户信息
     */
    @ResponseBody
    @RequestMapping(value = "/doEditPartners")
    public XmopayResponse<String> doEditPartners(HttpServletRequest req) {
        XmopayResponse<String> resultResp = new XmopayResponse(XmopayResponse.FAILURE,"",null);
        try {
            //记录日志需要
            AdminUserDto adminUserDto = WebCommon.getSessionUserInfo(req);
            Integer sessionMuid = adminUserDto.getMuId();
            //日志
            String logmsg = "[用户常规事件] 当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(req.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(req);

            String ptid = req.getParameter("ptid");
            String partner_name = req.getParameter("partner_name");
            String apiStatus = req.getParameter("apiStatus");
            String status = req.getParameter("status");
            String apiWhiteIp = req.getParameter("api_white_ip");
            String app_title = req.getParameter("app_title");
            String app_domain = req.getParameter("app_domain");
            String app_gateway_url = req.getParameter("app_gateway_url");

            PartnerDto partnerDto = new PartnerDto();
            partnerDto.setPtid(XmoPayUtils.isEmpty(ptid) ? 0 : Integer.parseInt(ptid));

            SingleResult<PartnerDto> partnerDtoSingleResult = partnerService.getPartnerInfo(partnerDto);
            if (!partnerDtoSingleResult.isSuccess()) {
                resultResp.setResultMsg("没有该商户，请联系客服!");
                return resultResp;
            }
            Map partnerInfoMap = null;
            String partnerInfo = partnerDtoSingleResult.getResult().getPartnerInfo();
            if (!XmoPayUtils.isEmpty(partnerInfo) && partnerInfo.startsWith("{")) {
                try {
                    partnerInfoMap = JSON.parseObject(partnerInfo, Map.class);
                } catch (Exception e) {
                    partnerInfoMap = null;
                }
            }
            if (partnerInfoMap == null) {
                partnerInfoMap = new HashMap(6);
            }
            if (!XmoPayUtils.isEmpty(app_title)) {
                partnerInfoMap.put("app_title", app_title.trim());
            }
            if (!XmoPayUtils.isEmpty(app_domain)) {
                partnerInfoMap.put("app_domain", app_domain.trim());
            }
            if (!XmoPayUtils.isEmpty(app_gateway_url)) {
                partnerInfoMap.put("app_gateway_url", app_gateway_url.trim());
            }
            partnerDto.setPartnerInfo(JSON.toJSONString(partnerInfoMap));

            partnerDto.setPartnerName(partner_name);
            partnerDto.setStatus(XmoPayUtils.isEmpty(status) ? -1 : Integer.valueOf(status));
            partnerDto.setApiStatus(XmoPayUtils.isEmpty(apiStatus) ? -1 : Integer.valueOf(apiStatus));
            partnerDto.setApiWhiteIp(apiWhiteIp);
            partnerDto.setUpdateTime(new Date());

            SingleResult<Integer> updateSingleResult = partnerService.updatePartner(partnerDto);
            int n = 0;
            if (updateSingleResult.isSuccess()) {
                log.info(logmsg, "修改商户信息成功！");
                resultResp.setResultMsg("修改商户信息成功!");
                resultResp.setResultCode(XmopayResponse.SUCCESS);
                return resultResp;
            }
            log.error(logmsg, "修改商户信息失败！");
            resultResp.setResultMsg("修改商户信息失败!");
            return resultResp;
        } catch (Exception e) {
            e.printStackTrace();
            resultResp.setResultMsg("系统异常，请联系客服！");
            resultResp.setResultCode(XmopayResponse.EXCEPTION);
            return resultResp;
        }
    }

    /**
     * 商户产品列表
     * @param request
     * @return
     */
    @RequestMapping(value = "productList")
    public String productList(HttpServletRequest request) {
        try {
            String partnerId = request.getParameter("partner_id");
            String pageIndex = request.getParameter("pageIndex");
            int pageIdx   = !XmoPayUtils.isEmpty(pageIndex) ? Integer.parseInt(pageIndex) : 1;
            String inajax = request.getParameter("inajax");

            request.setAttribute("partner_id", partnerId);
            request.setAttribute("inajax",     (inajax != null) ? inajax : "0");

            PartnerDto partnerDto = new PartnerDto();
            partnerDto.setPartnerId(partnerId);
            SingleResult<PartnerDto> singleResult1 = partnerService.getPartnerInfo(partnerDto);
            if(!singleResult1.isSuccess()){
                return "partners/partners.product.index";
            }
            request.setAttribute("partner_name", singleResult1.getResult().getPartnerName());

            PartnerProductDto partnerProductDto = new PartnerProductDto();
            partnerProductDto.setPartnerId(partnerId);
            partnerProductDto.setCurrentPage(pageIdx);
            partnerProductDto.setPageSize(20);
            SingleResult<PageInfo> singleResult = partnerProductService.getPartnerProductPageList(partnerProductDto);
            if (singleResult.isSuccess()) {
                PageInfo page = singleResult.getResult();
                request.setAttribute("page", page);
                request.setAttribute("lists", page.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, page, "listDiv"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "partners/partners.product.index";
    }

    /**
     * 开通产品
     * @return
     */
    @RequestMapping(value = "openProduct")
    public String openProduct(@RequestParam(value = "partner_id", required = false) String partnerId,
                              @RequestParam(value = "ppid", required = false) Integer ppid,
                              HttpServletRequest request) {

        request.setAttribute("partner_id", partnerId);
        request.setAttribute("ppid", ppid);
        if (!XmoPayUtils.isEmpty(partnerId)) {
            PartnerDto partnerDto = new PartnerDto();
            partnerDto.setPartnerId(partnerId);
            SingleResult<PartnerDto> partnerDtoSingleResult = partnerService.getPartnerInfo(partnerDto);
            if (partnerDtoSingleResult.isSuccess()) {
                request.setAttribute("partner", partnerDtoSingleResult.getResult());
            }
        }

        request.setAttribute("tab_title", "开通");
        if (ppid != null && ppid > 0) {
            request.setAttribute("tab_title", "更新");

            PartnerProductDto partnerProductDto = new PartnerProductDto();
            partnerProductDto.setPpid(ppid);
            partnerProductDto.setPartnerId(partnerId);
            SingleResult<PartnerProductDto> singleResult = partnerProductService.getPartnersProduct(partnerProductDto);
            if (singleResult.isSuccess()) {
                request.setAttribute("partnerProduct", singleResult.getResult());
            }
        }

        // 渠道
        GatewayChannelDto gatewayChannelDto = new GatewayChannelDto();
        gatewayChannelDto.setStatus(1);
        SingleResult<List<GatewayChannelDto>> singleResult_channels = gatewayChannelService.findGatewayChannelList(gatewayChannelDto);
        if (singleResult_channels.isSuccess()) {
            request.setAttribute("gatewayChannels", singleResult_channels.getResult());
        }

        return "partners/partners.product.form";
    }

    /**
     * 给商户添加产品 OR 更新商户产品
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "doEditPartnerProduct", method = RequestMethod.POST)
    public XmopayResponse<Integer> doEditPartnerProduct(HttpServletRequest request) {
        XmopayResponse<Integer> result = new XmopayResponse<>(XmopayResponse.FAILURE, "添加失败", 0);
        //记录日志需要
        AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
        Integer sessionMuid = sessionUser.getMuId();
        //日志
        String logmsg = "[用户常规事件] 当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);

        try {
            String ppid = request.getParameter("ppid");
            String partnerId = request.getParameter("partner_id");
            String partnerName = request.getParameter("partner_name");
            String productType = request.getParameter("product_type");
            //${list.channelCode}|${list.channelName}
            String channelCode = request.getParameter("channel_code");
            String rate = request.getParameter("rate");
            String bankCode = request.getParameter("bank_code");
            String bankName = request.getParameter("bank_name");
            String status = request.getParameter("status");


            if (XmoPayUtils.isEmpty(partnerId) || XmoPayUtils.isEmpty(productType) || XmoPayUtils.isEmpty(channelCode) ||
                    XmoPayUtils.isEmpty(rate) || XmoPayUtils.isEmpty(bankCode) || XmoPayUtils.isEmpty(bankName) || XmoPayUtils.isEmpty(status)) {
                log.error(logmsg, "支付产品参数有空");
                result.setResultMsg("支付产品参数有空");
                return result;
            }

            // 判断数字类型
            Pattern pattern = Pattern.compile("^[+]?([0-9]+(.[0-9]{1,2})?)$");
            Matcher isNum = pattern.matcher(rate);
            if (!isNum.matches()) {
                log.error(logmsg, "结算费率错误");
                result.setResultMsg("结算费率必须大于0的保留2位小数的数字");
                return result;
            }

            PartnerProductDto partnerProductEditDto = new PartnerProductDto();
            partnerProductEditDto.setPartnerId(partnerId);
            partnerProductEditDto.setPartnerName(partnerName);
            partnerProductEditDto.setProductType(productType);
            partnerProductEditDto.setChannelCode(channelCode.split("\\|")[0]);
            partnerProductEditDto.setChannelName(channelCode.split("\\|")[1]);
            partnerProductEditDto.setRate( new BigDecimal(rate) );
            partnerProductEditDto.setBankCode(bankCode);
            partnerProductEditDto.setBankName(bankName);
            partnerProductEditDto.setStatus(Integer.parseInt(status));
            partnerProductEditDto.setUpdateTime(new Date());

            SingleResult<Integer> singleResult = null;

            if (XmoPayUtils.isEmpty(ppid)) {
                //增加新产品
                partnerProductEditDto.setCreateTime(new Date());
                singleResult = partnerProductService.insertPartnerProduct(partnerProductEditDto);
                log.info(logmsg, "给商户开通产品【新增产品】！");
            } else {
                //查找该产品原信息
                PartnerProductDto findPartnerProduct = new PartnerProductDto();
                findPartnerProduct.setPpid(Integer.valueOf(ppid));
                SingleResult<PartnerProductDto> findResult = partnerProductService.getPartnersProduct(findPartnerProduct);
                if (!findResult.isSuccess()) {
                    log.error(logmsg, "该产品不存在！");
                    result.setResultMsg("该产品不存在！");
                    return result;
                }
                partnerProductEditDto.setPpid(Integer.parseInt(ppid));
                singleResult = partnerProductService.updatePartnerProduct(partnerProductEditDto);

                log.info(logmsg, "给商户修改产品【更新产品】！");
            }
            if (singleResult.isSuccess()) {
                log.info(logmsg, "操作成功！");
                result.setResultMsg("操作成功");
                result.setResultCode(XmopayResponse.SUCCESS);
                return result;
            }
            if(singleResult.getResult() == -1){
                log.info(logmsg, "操作失败，商户下已有相同支付产品！");
                result.setResultMsg("操作失败，商户下已有相同支付产品！");
                result.setResultCode(XmopayResponse.FAILURE);
                return result;
            }
            log.error(logmsg, "操作失败！");
            result.setResultMsg("操作失败");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultMsg("操作异常");
            result.setResultCode(XmopayResponse.EXCEPTION);
        }
        return result;
    }
}
