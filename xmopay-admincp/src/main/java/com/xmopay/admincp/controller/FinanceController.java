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
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    /**
     * 财务报表
     * @param request
     * @return
     */
    @RequestMapping(value = "billingStatics")
    public String billingStatics(HttpServletRequest request){
        try {
            String inajax = request.getParameter("inajax"); //ajax模式
            String type   = request.getParameter("type"); //merchant date
            type = XmoPayUtils.isEmpty(type) ? "date" : type;

            String startTime   = request.getParameter("startTime");
            String endTime     = request.getParameter("endTime");
            String partnerId   = request.getParameter("partnerId");
            String partnerName = request.getParameter("partnerName");
            String index        = request.getParameter("pageIndex");
            String splitPayment = request.getParameter("splitPayment");

            //默认查询当天
            if (XmoPayUtils.isEmpty(startTime) && XmoPayUtils.isEmpty(endTime)) {
                startTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00");
                endTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
            }
            //回显数据
            Map rparam = new HashMap();
            rparam.put("partnerId",    partnerId);
            rparam.put("partnerName",  partnerName);
            rparam.put("startTime",    startTime);
            rparam.put("endTime",      endTime);
            rparam.put("type",         type);
            rparam.put("splitPayment", splitPayment);

            request.setAttribute("paramMap", rparam);
            request.setAttribute("inajax",   (inajax != null) ? inajax : "0"); //ajax分页
            //分页显示记录
            int pageIndex = XmoPayUtils.isEmpty(index) ? 1 : Integer.parseInt(index);//当前第一页
            int pageSize  = 20;
            Map paramMap = new HashMap();
            paramMap.put("startTime",    startTime);
            paramMap.put("endTime",      endTime);
            paramMap.put("partnerId",    partnerId);
            paramMap.put("partnerName",  partnerName);
            paramMap.put("splitPayment", splitPayment);
            paramMap.put("type", type);
            paramMap.put("pageIndex", pageIndex);
            paramMap.put("pageSize",  pageSize);
            SingleResult<PageInfo> billingStatics = billingsService.getBillingStatics(paramMap);
            if (billingStatics.isSuccess()) {
                PageInfo pageUtils = billingStatics.getResult();
                request.setAttribute("page", pageUtils);
                request.setAttribute("lists", pageUtils.getList());
                request.setAttribute("pages", Pagesutils.showAjaxPage(request, pageUtils, "listDiv"));
            }
            SingleResult<Map> billingStaticsTotal = billingsService.getBillingStaticsTotal(paramMap);
            if(billingStaticsTotal.isSuccess()){
                request.setAttribute("totalMap",billingStaticsTotal.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "finance/financeStatics";
    }

    /**
     * @param
     * @return
     * @Description: [Action]导出财务报表
     * @autohr: hyt
     * @datatime: 2017/8/9 18:20
     * @return
     */
    @RequestMapping(value = "exportBillingStatics")
    public void exportBillingStatics(HttpServletRequest request, HttpServletResponse response) {
        try {
            AdminUserDto sessionUser = WebCommon.getSessionUserInfo(request);
            int sessionMuid = sessionUser.getMuId();
            //日志
            String logmsg = "[用户常规事件] 当前登录者=" + sessionMuid + ", 事件描述={}, 请求参数=" + XmoPayUtils.formRequestMap(request.getParameterMap()) + ", 请求头=" + XmoPayUtils.getHeadersInfo(request);
            String type = request.getParameter("type"); //merchant date
            type = XmoPayUtils.isEmpty(type) ? "date" : type;
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String partnerId = request.getParameter("partnerId");
            String confirmTime = request.getParameter("confirmTime");
            String splitPayment = request.getParameter("splitPayment");
            response.setContentType("text/html; charset=utf-8");
            Map<String, Object> paramMap = new HashMap<String, Object>();
            //按日期的单条记录导出
            if ("date".equals(type) && !XmoPayUtils.isEmpty(confirmTime)) {
                startTime = confirmTime + " 00:00:00";
                endTime = confirmTime + " 23:59:59";
                paramMap.put("startTime", startTime);
                paramMap.put("endTime", endTime);
            } else if("date".equals(type)){
                if (XmoPayUtils.isEmpty(startTime) && XmoPayUtils.isEmpty(endTime)) {
                    startTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00");
                    endTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
                }
                paramMap.put("startTime", startTime);
                paramMap.put("endTime",  endTime);
            }else if("merchant".equals(type)){
                if (XmoPayUtils.isEmpty(startTime) && XmoPayUtils.isEmpty(endTime)) {
                    startTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00");
                    endTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
                }
                paramMap.put("startTime", startTime);
                paramMap.put("endTime",  endTime);
            }
            paramMap.put("partnerId",partnerId);
            paramMap.put("type", type);
            paramMap.put("splitPayment",splitPayment);
            request.setAttribute("paramMap", paramMap);
            SingleResult<List<Map>> singleResult = billingsService.exportBillingStatics(paramMap);
            List<Map> typeStatisList = singleResult.getResult();
            String sheetname = "财务报表";
            //创建工作簿
            HSSFWorkbook workbook = new HSSFWorkbook();
            //生成一个表格
            HSSFSheet sheet = workbook.createSheet(sheetname);
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            // 指定单元格居中对齐
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            // 指定单元格垂直居中对齐
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 指定当单元格内容显示不下时自动换行
            cellStyle.setWrapText(true);
            // 设置单元格字体
            HSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontName("宋体");
            font.setFontHeight((short) 200);
            cellStyle.setFont(font);
            HSSFCellStyle cellStyleColor = workbook.createCellStyle();
            cellStyleColor.setAlignment(HorizontalAlignment.CENTER);
            // 指定单元格垂直居中对齐
            cellStyleColor.setVerticalAlignment(VerticalAlignment.CENTER);
            // 指定当单元格内容显示不下时自动换行
            cellStyleColor.setWrapText(true);
            // 设置单元格字体
            cellStyleColor.setFont(font);
            cellStyleColor.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
            cellStyleColor.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyleColor.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
            cellStyleColor.setBorderBottom(BorderStyle.THIN);
            cellStyleColor.setBorderLeft(BorderStyle.THIN);
            cellStyleColor.setBorderRight(BorderStyle.THIN);
            cellStyleColor.setBorderTop(BorderStyle.THIN);
            HSSFCellStyle cellStyleNum = workbook.createCellStyle();
            cellStyleColor.setAlignment(HorizontalAlignment.CENTER);
            // 指定单元格垂直居中对齐
            cellStyleNum.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleNum.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
            cellStyleNum.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);;
            cellStyleNum.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleNum.setBorderBottom(BorderStyle.THIN);
            cellStyleNum.setBorderLeft(BorderStyle.THIN);
            cellStyleNum.setBorderRight(BorderStyle.THIN);
            cellStyleNum.setBorderTop(BorderStyle.THIN);
            int add = 0;
            if(XmoPayUtils.isEmpty(splitPayment)){
                final String[] firstHeaders = new String[]{"类型","在线支付","商户转账(出)", "商户转账(入)", "交易退款", "代付出款", "代付退款", "调账加款", "调账扣款", "冲正加款", "冲正扣款"};
                final String[] secondHeaders;
                if ("merchant".equals(type)) {
                    secondHeaders = new String[]{"商户ID", "商户名", "总金额", "手续费", "笔数","总金额", "手续费", "笔数", "总金额", "笔数", "总金额	", "手续费", "笔数", "总金额", "手续费", "笔数	", "总金额", "手续费", "笔数", "总金额", "笔数", "总金额", "笔数", "总金额", "笔数", "总金额", "笔数"};
                    add = 1;
                } else {
                    secondHeaders = new String[]{"日期",  "总金额", "手续费", "笔数", "总金额", "手续费", "笔数",  "总金额", "笔数", "总金额	", "手续费", "笔数", "总金额", "手续费", "笔数	", "总金额", "手续费", "笔数", "总金额", "笔数	", "总金额", "笔数", "总金额", "笔数", "总金额", "笔数"};
                }
                HSSFRow row = sheet.createRow(0);
                HSSFCell cell;
                for (int i = 0; i < firstHeaders.length; i++) {
                    if (i == 0) {
                        if ("merchant".equals(type)) {
                            CellRangeAddress cra = new CellRangeAddress(0, 0, 0, add);
                            sheet.addMergedRegion(cra);
                        }
                        cell = row.createCell(i);
                        row.setHeight((short) 300);
                        cell.setCellStyle(cellStyle);
                    } else if (i < 3) {
                        CellRangeAddress cra = new CellRangeAddress(0, 0, i + 2 * (i - 1) + add, i + 2 * i + add);
                        sheet.addMergedRegion(cra);
                        cell = row.createCell(i + 2 * (i - 1) + add);
                        row.setHeight((short) 300);
                        if (i == 1) {
                            cell.setCellStyle(cellStyleColor);
                        } else {
                            cell.setCellStyle(cellStyle);
                        }
                    } else if (i == 3) {
                        CellRangeAddress cra = new CellRangeAddress(0, 0,  7+ add, 8 + add);
                        sheet.addMergedRegion(cra);
                        cell = row.createCell(7 + add);
                        cell.setCellStyle(cellStyleColor);
                    } else if (i < 7) {
                        if (i == 4) {
                            CellRangeAddress cra = new CellRangeAddress(0, 0, 9 + add, 11 + add);
                            sheet.addMergedRegion(cra);
                            cell = row.createCell(9 + add);
                            cell.setCellStyle(cellStyle);
                        } else if (i == 5) {
                            CellRangeAddress cra = new CellRangeAddress(0, 0, 12 + add, 14 + add);
                            sheet.addMergedRegion(cra);
                            cell = row.createCell(12 + add);
                            cell.setCellStyle(cellStyleColor);
                        } else {
                            CellRangeAddress cra = new CellRangeAddress(0, 0, 15 + add, 17 + add);
                            sheet.addMergedRegion(cra);
                            cell = row.createCell(15 + add);
                            cell.setCellStyle(cellStyle);
                        }
                    } else {
                        CellRangeAddress cra = new CellRangeAddress(0, 0, 18 + 2 * (i - 7) + add, 19 + 2 * (i - 7) + add);
                        sheet.addMergedRegion(cra);
                        cell = row.createCell(18 + 2 * (i - 7) + add);
                        if ((i - 6) % 2 == 1) {
                            cell.setCellStyle(cellStyleColor);
                        } else {
                            cell.setCellStyle(cellStyle);
                        }
                    }
                    row.setHeight((short) 300);
                    HSSFRichTextString text = new HSSFRichTextString(firstHeaders[i]);
                    cell.setCellValue(text);
                    sheet.autoSizeColumn(i);
                }
                row = sheet.createRow(1);
                boolean flag = false;

                Integer[] steps = {1 + add, 2 + add, 3 + add, 7 + add, 8 + add, 12 + add, 13 + add, 14 + add, 18 + add, 19 + add, 22 + add, 23 + add};
                for (short i = 0; i < secondHeaders.length; i++) {
                    cell = row.createCell(i);
                    row.setHeight((short) 300);
                    for (int j = 0; j < steps.length; j++) {
                        if (i == steps[j]) {
                            cell.setCellStyle(cellStyleColor);
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        cell.setCellStyle(cellStyle);
                    } else {
                        flag = false;
                    }
                    HSSFRichTextString text = new HSSFRichTextString(secondHeaders[i]);
                    cell.setCellValue(text);
                    sheet.autoSizeColumn(i); //自动改变列宽
                }
                int excelRowStep = 1; //定义excel row 初始步长
                if (typeStatisList != null && typeStatisList.size() > 0) {
                    for (Map billingMap : typeStatisList) {
                        //改变excel row 行数
                        row = sheet.createRow(excelRowStep + 1);
                        if (type.equals("merchant")) {
                            row.createCell(0).setCellValue(billingMap.get("PARTNER_ID").toString());
                            row.createCell(add).setCellValue(billingMap.get("PARTNER_NAME").toString());
                        } else {
                            row.createCell(0).setCellValue(billingMap.get("CONFIRM_TIME").toString().substring(5));
                        }
                        cell = row.createCell(1 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_ONLINE_PAY").toString()));
                        cell = row.createCell(2 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_ONLINE_PAY").toString()));
                        cell = row.createCell(3 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_ONLINE_PAY").toString()));

                        cell = row.createCell(4 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_TRANS_FROM_ACCOUNT").toString()));
                        cell = row.createCell(5 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_TRANS_FROM_ACCOUNT").toString()));
                        cell = row.createCell(6 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_TRANS_FROM_ACCOUNT").toString()));

                        cell = row.createCell(7 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_TRANS_TO_ACCOUNT").toString()));
                        cell = row.createCell(8 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_TRANS_TO_ACCOUNT").toString()));

                        cell = row.createCell(9 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_REFUND_FROM_TRADE").toString()));
                        cell = row.createCell(10 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_REFUND_FROM_TRADE").toString()));
                        cell = row.createCell(11 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_REFUND_FROM_TRADE").toString()));

                        cell = row.createCell(12 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_TRANS_TO_CARD").toString()));
                        cell = row.createCell(13 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_TRANS_TO_CARD").toString()));
                        cell = row.createCell(14 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_TRANS_TO_CARD").toString()));

                        cell = row.createCell(15 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_REFUND_FROM_MENTION").toString()));
                        cell = row.createCell(16 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_REFUND_FROM_MENTION").toString()));
                        cell = row.createCell(17 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_REFUND_FROM_MENTION").toString()));

                        cell = row.createCell(18 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_ADJUST_ADD_TO_MERCHANT").toString()));
                        cell = row.createCell(19 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_ADJUST_ADD_TO_MERCHANT").toString()));

                        cell = row.createCell(20 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_ADJUST_SUB_FROM_MERCHANT").toString()));
                        cell = row.createCell(21 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_ADJUST_SUB_FROM_MERCHANT").toString()));

                        cell = row.createCell(22 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_CORRECT_ADD_TO_MERCHANT").toString()));
                        cell = row.createCell(23 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_CORRECT_ADD_TO_MERCHANT").toString()));

                        cell = row.createCell(24 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_CORRECT_SUB_FROM_MERCHANT").toString()));
                        cell = row.createCell(25 + add);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_CORRECT_SUB_FROM_MERCHANT").toString()));

                        sheet.autoSizeColumn(excelRowStep); //自动改变列宽

                        excelRowStep++;
                    }
                    //清除list
                    typeStatisList.clear();
                }
            }else {
                final String[] firstHeaders = new String[]{"类型", "网银支付", "网银充值", "	快捷支付", "QQ扫码", "微信扫码", "支付宝", "商户转账(出)", "商户转账(入)", "交易退款", "代付出款", "代付退款", "调账加款", "调账扣款", "冲正加款", "冲正扣款"};
                final String[] secondHeaders;
                if ("merchant".equals(type)) {
                    secondHeaders = new String[]{"商户ID", "商户名", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "笔数", "总金额	", "手续费", "笔数", "总金额", "手续费", "笔数	", "总金额", "手续费", "笔数", "总金额", "笔数	", "总金额", "笔数", "总金额", "笔数", "总金额", "笔数"};
                    add = 1;
                } else {
                    secondHeaders = new String[]{"日期", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "手续费", "笔数", "总金额", "笔数", "总金额	", "手续费", "笔数", "总金额", "手续费", "笔数	", "总金额", "手续费", "笔数", "总金额", "笔数	", "总金额", "笔数", "总金额", "笔数", "总金额", "笔数"};
                }
                HSSFRow row = sheet.createRow(0);
                HSSFCell cell;
                for (int i = 0; i < firstHeaders.length; i++) {
                    if (i == 0) {
                        if ("merchant".equals(type)) {
                            CellRangeAddress cra = new CellRangeAddress(0, 0, 0, add);
                            sheet.addMergedRegion(cra);
                        }
                        cell = row.createCell(i);
                        row.setHeight((short) 300);
                        cell.setCellStyle(cellStyle);
                    } else if (i < 8) {
                        CellRangeAddress cra = new CellRangeAddress(0, 0, i + 2 * (i - 1) + add, i + 2 * i + add);
                        sheet.addMergedRegion(cra);
                        cell = row.createCell(i + 2 * (i - 1) + add);
                        row.setHeight((short) 300);
                        if (i == 1 || i == 3 || i == 5 || i == 7) {
                            cell.setCellStyle(cellStyleColor);
                        } else {
                            cell.setCellStyle(cellStyle);
                        }
                    } else if (i == 8) {
                        CellRangeAddress cra = new CellRangeAddress(0, 0, 22 + add, 23 + add);
                        sheet.addMergedRegion(cra);
                        cell = row.createCell(22 + add);
                        cell.setCellStyle(cellStyle);
                    } else if (i < 12) {
                        if (i == 9) {
                            CellRangeAddress cra = new CellRangeAddress(0, 0, 24 + add, 26 + add);
                            sheet.addMergedRegion(cra);
                            cell = row.createCell(24 + add);
                            cell.setCellStyle(cellStyleColor);
                        } else if (i == 10) {
                            CellRangeAddress cra = new CellRangeAddress(0, 0, 27 + add, 29 + add);
                            sheet.addMergedRegion(cra);
                            cell = row.createCell(27 + add);
                            cell.setCellStyle(cellStyle);
                        } else {
                            CellRangeAddress cra = new CellRangeAddress(0, 0, 30 + add, 32 + add);
                            sheet.addMergedRegion(cra);
                            cell = row.createCell(30 + add);
                            cell.setCellStyle(cellStyleColor);
                        }
                    } else {
                        CellRangeAddress cra = new CellRangeAddress(0, 0, 33 + 2 * (i - 12) + add, 34 + 2 * (i - 12) + add);
                        sheet.addMergedRegion(cra);
                        cell = row.createCell(33 + 2 * (i - 12) + add);
                        if ((i - 11) % 2 == 1) {
                            cell.setCellStyle(cellStyle);
                        } else {
                            cell.setCellStyle(cellStyleColor);
                        }
                    }
                    row.setHeight((short) 300);
                    HSSFRichTextString text = new HSSFRichTextString(firstHeaders[i]);
                    cell.setCellValue(text);
                    sheet.autoSizeColumn(i);
                }
                row = sheet.createRow(1);
                boolean flag = false;

                Integer[] steps = {1 + add, 2 + add, 3 + add, 7 + add, 8 + add, 9 + add, 13 + add, 14 + add, 15 + add, 19 + add, 20 + add, 21 + add, 24 + add, 25 + add, 26 + add, 30 + add, 31 + add, 32 + add, 35 + add, 36 + add, 39 + add, 40 + add};
                for (short i = 0; i < secondHeaders.length; i++) {
                    cell = row.createCell(i);
                    row.setHeight((short) 300);
                    for (int j = 0; j < steps.length; j++) {
                        if (i == steps[j]) {
                            cell.setCellStyle(cellStyleColor);
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        cell.setCellStyle(cellStyle);
                    } else {
                        flag = false;
                    }
                    HSSFRichTextString text = new HSSFRichTextString(secondHeaders[i]);
                    cell.setCellValue(text);
                    sheet.autoSizeColumn(i); //自动改变列宽
                }
                int excelRowStep = 1; //定义excel row 初始步长
                if (typeStatisList != null && typeStatisList.size() > 0) {
                    for (Map billingMap : typeStatisList) {
                        //改变excel row 行数
                        row = sheet.createRow(excelRowStep + 1);
                        if (type.equals("merchant")) {
                            row.createCell(0).setCellValue(billingMap.get("PARTNER_ID").toString());
                            row.createCell(add).setCellValue(billingMap.get("PARTNER_NAME").toString());
                        } else {
                            row.createCell(0).setCellValue(billingMap.get("CONFIRM_TIME").toString().substring(5));
                        }
                        cell = row.createCell(1 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_BANK_PAY").toString()));
                        cell = row.createCell(2 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_BANK_PAY").toString()));
                        cell = row.createCell(3 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_BANK_PAY").toString()));

                        row.createCell(4 + add).setCellValue(Double.valueOf(billingMap.get("SUM_BANK_RECHARGE").toString()));
                        row.createCell(5 + add).setCellValue(Double.valueOf(billingMap.get("SUM_FEE_BANK_RECHARGE").toString()));
                        row.createCell(6 + add).setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_BANK_RECHARGE").toString()));

                        cell = row.createCell(7 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_QPAY_QRCODE").toString()));
                        cell = row.createCell(8 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_QPAY_QRCODE").toString()));
                        cell = row.createCell(9 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_QPAY").toString()));

                        row.createCell(10 + add).setCellValue(Double.valueOf(billingMap.get("SUM_QQPAY_QRCODE").toString()));
                        row.createCell(11 + add).setCellValue(Double.valueOf(billingMap.get("SUM_FEE_QQPAY_QRCODE").toString()));
                        row.createCell(12 + add).setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_QQPAY_QRCODE").toString()));

                        cell = row.createCell(13 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_WEIXIN_QRCODE").toString()));
                        cell = row.createCell(14 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_WEIXIN_QRCODE").toString()));
                        cell = row.createCell(15 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_WEIXIN").toString()));

                        row.createCell(16 + add).setCellValue(Double.valueOf(billingMap.get("SUM_ALIPAY_QRCODE").toString()));
                        row.createCell(17 + add).setCellValue(Double.valueOf(billingMap.get("SUM_FEE_ALIPAY_QRCODE").toString()));
                        row.createCell(18 + add).setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_ALIPAY").toString()));

                        cell = row.createCell(19 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_TRANS_FROM_ACCOUNT").toString()));
                        cell = row.createCell(20 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_TRANS_FROM_ACCOUNT").toString()));
                        cell = row.createCell(21 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_TRANS_FROM_ACCOUNT").toString()));

                        row.createCell(22 + add).setCellValue(Double.valueOf(billingMap.get("SUM_TRANS_TO_ACCOUNT").toString()));
                        row.createCell(23 + add).setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_TRANS_TO_ACCOUNT").toString()));

                        cell = row.createCell(24 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_REFUND_FROM_TRADE").toString()));
                        cell = row.createCell(25 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_REFUND_FROM_TRADE").toString()));
                        cell = row.createCell(26 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_REFUND_FROM_TRADE").toString()));

                        row.createCell(27 + add).setCellValue(Double.valueOf(billingMap.get("SUM_TRANS_TO_CARD").toString()));
                        row.createCell(28 + add).setCellValue(Double.valueOf(billingMap.get("SUM_FEE_TRANS_TO_CARD").toString()));
                        row.createCell(29 + add).setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_TRANS_TO_CARD").toString()));

                        cell = row.createCell(30 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_REFUND_FROM_MENTION").toString()));
                        cell = row.createCell(31 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_FEE_REFUND_FROM_MENTION").toString()));
                        cell = row.createCell(32 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_REFUND_FROM_MENTION").toString()));

                        row.createCell(33 + add).setCellValue(Double.valueOf(billingMap.get("SUM_ADJUST_ADD_TO_MERCHANT").toString()));
                        row.createCell(34 + add).setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_ADJUST_ADD_TO_MERCHANT").toString()));

                        cell = row.createCell(35 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_ADJUST_SUB_FROM_MERCHANT").toString()));
                        cell = row.createCell(36 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_ADJUST_SUB_FROM_MERCHANT").toString()));

                        row.createCell(37 + add).setCellValue(Double.valueOf(billingMap.get("SUM_CORRECT_ADD_TO_MERCHANT").toString()));
                        row.createCell(38 + add).setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_CORRECT_ADD_TO_MERCHANT").toString()));

                        cell = row.createCell(39 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("SUM_CORRECT_SUB_FROM_MERCHANT").toString()));
                        cell = row.createCell(40 + add);
                        cell.setCellStyle(cellStyleNum);
                        cell.setCellValue(Double.valueOf(billingMap.get("TOTAL_TYPE_CORRECT_SUB_FROM_MERCHANT").toString()));

                        sheet.autoSizeColumn(excelRowStep); //自动改变列宽

                        excelRowStep++;
                    }
                    //清除list
                    typeStatisList.clear();
                }
            }
            SimpleDateFormat fileDateSdf = new SimpleDateFormat("yyyy年MM月dd日");
            //输出到浏览器让用户下载
            String excelName = "";
            if (!XmoPayUtils.isEmpty(partnerId)) {
                excelName += partnerId + "的";
            }
            if (XmoPayUtils.isEmpty(startTime) && XmoPayUtils.isEmpty(endTime)) {
                excelName += fileDateSdf.format(new Date()) + "_财务报表";
            }
            if (!XmoPayUtils.isEmpty(startTime) && XmoPayUtils.isEmpty(endTime)) {
                excelName += startTime.replace("-", "").replace(" ", "").replace(":", "") + "到现在_财务报表";
            }
            if (!XmoPayUtils.isEmpty(startTime) && !XmoPayUtils.isEmpty(endTime)) {
                excelName += startTime.replace("-", "").replace(" ", "").replace(":", "") + "到" + endTime.replace("-", "").replace(" ", "").replace(":", "") + "_财务报表";
            }
            if (XmoPayUtils.isEmpty(startTime) && !XmoPayUtils.isEmpty(endTime)) {
                excelName += "..." + endTime.replace("-", "").replace(" ", "").replace(":", "") + "_财务报表";
            }
            response.reset();
            response.setContentType("application/msexcel");
            response.setHeader("Content-Disposition", "attachment;Filename=" + new String(excelName.getBytes("GBK"), "iso-8859-1") + ".xls");
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
            os.close();
            log.info(logmsg, "导出成功！");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
