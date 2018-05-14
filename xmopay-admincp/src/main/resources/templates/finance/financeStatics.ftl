<#escape x as (x)!>
    <#if inajax=="0">
    <#include "../header.ftl">
    <#--laydate 时间控件-->
    <#--配置文件 -->
    <link rel="stylesheet"  href="static/public/plugins/layui/css/layui.css"/>
    <script type="text/javascript"  src="static/public/plugins/layui/layui.js" charset="utf-8"></script>

<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li class="active" ><a href="/finance/billingStatics" style="cursor: pointer;">财务报表</a></li>
        </ul>
    </div>
</section>

<section id="listDiv" class="content-body">
    </#if>
    <!-- search div start -->
    <div id="search_div" >

        <div id="hideSearchDiv" class="row searchDiv" style="display:none">
            <div class="col-sm-12">
                <div class="page-header">
                    <h1>搜索列表</h1>
                </div>
                <form action="/finance/billingStatics" name="pagefrm" method="post" class="form-horizontal">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <label class="control-label">按类型:</label>
                        <select class="form-control input-sm" name="type" id="type" >
                            <option value="date" <#if paramMap.type?? && paramMap.type=="date">selected</#if>>按日期</option>
                            <option value="merchant" <#if paramMap.type?? && paramMap.type=="merchant">selected</#if>>按商户</option>
                        </select>
                        &nbsp;&nbsp;
                        <div id="tradeTime" style="display:inline">
                            <label class="control-label">交易时间:</label>
                            <input type="text" name="startTime" readonly id="startTime" class="form-control input-sm" value="<#if paramMap.startTime??>${paramMap.startTime}</#if>">
                            <input type="text" name="endTime"   readonly id="endTime"   class="form-control input-sm" value="<#if paramMap.endTime??>${paramMap.endTime}</#if>">
                        </div>
                    </div>
                    <div class="form-group form-inline">
                        <#if sessionAdminUserInfo??&&sessionAdminUserInfo.partnerId=='0'>
                            <div id="inputOperatorPartnerDiv" style="display:inline">
                                <label id="inputOperatorPartnerLabel" >
                                    运营商ID：<input type="text" name="inputOperatorPartnerId" value="<#if paramMap.inputOperatorPartnerId??>${paramMap.inputOperatorPartnerId}</#if>"class="form-control input-sm input-append date" id="inputOperatorPartnerId" >
                                </label>
                            </div>
                        </#if>
                        <div id="topPartnerDiv" style="display:inline">
                            <label id="topPartnerLabel" >
                                代理商ID：<input type="text" name="topPartnerId" value="<#if paramMap.topPartnerId??>${paramMap.topPartnerId}</#if>"class="form-control input-sm input-append date" id="topPartnerId" >
                            </label>
                        </div>
                        <div id="merchantDiv" style="display:inline">
                            <label id="merchantLabel" >
                                商户ID：<input type="text" name="partnerId" value="<#if paramMap.partnerId??>${paramMap.partnerId}</#if>"class="form-control input-sm input-append date" id="partnerId" >
                            </label>
                        </div>
                        <div id="merchantDiv" style="display:inline">
                            <label id="merchantLabel" >
                                商户名：<input type="text" name="partnerName" value="<#if paramMap.partnerName??>${paramMap.partnerName}</#if>"class="form-control input-sm input-append date" id="partnerId" >
                            </label>
                        </div>
                        <select name="tbname" id="tbname" class="form-control input-sm">
                                <option value="current"<#if paramMap.tbname?? && paramMap.tbname=="current">selected</#if>>实时数据</option>
                                <option value="history" <#if paramMap.tbname?? && paramMap.tbname=="history">selected</#if>>历史数据</option>
                        </select>
                        &nbsp;&nbsp;
                        <#if sessionAdminUserInfo??&&sessionAdminUserInfo.bindIp=='1'>
                            <label class="checkbox-inline">
                                <input type="checkbox" id="splitPayment" name="splitPayment" <#if paramMap.splitPayment??&&paramMap.splitPayment=='1'>checked</#if> value="1" />分拆支付
                            </label>
                        </#if>
                    </div>

                    <div class="formBtm">
                        <input type="submit" value="确定"  class="btn btn-default">
                    </div>
                </form>
            </div>
        </div>

    </div>
    <!-- search div end -->

    <div class="row list-toolbar">
        <div class="col-sm-6">
            <button id="searchHandle." onclick="toggleSearchDiv();" type="button"  class="btn btn-primary btn-sm">搜索列表</button>
            <a class="btn-a" href="javascript:exportBillingStatics();"><span>全报表导出</span></a>
        </div>
        <div class="col-sm-6">
            <nav class="pages pull-right">
                <ul id="testpage" class="pagination pagination-sm">${pages}</ul>
            </nav>
        </div>
    </div>
        <!-- list-toolbar end-->

    <!-- list-div start-->
    <div class="row" style="width: 102.5%; overflow:auto;">
        <table class="table table-bordered" style="width: auto">
            <thead>
                <tr >
                    <th style="text-align: center;" <#if paramMap.type??&&paramMap.type=="merchant">colspan="2"</#if>>类型</th>
                    <#if paramMap.splitPayment??&&paramMap.splitPayment=='1'>
                        <th style="text-align: center;" colspan="3" class="warning">网银支付</th>
                        <th style="text-align: center;" colspan="3">网银充值</th>
                        <th style="text-align: center;" colspan="3" class="warning">快捷支付</th>
                        <th style="text-align: center;" colspan="3">QQ扫码</th>
                        <th style="text-align: center;" colspan="3" class="warning">微信扫码</th>
                        <th style="text-align: center;" colspan="3">支付宝</th>
                        <th style="text-align: center;" colspan="3" class="warning">商户转账(出)</th>
                        <th style="text-align: center;" colspan="2">商户转账(入)</th>
                        <th style="text-align: center;" colspan="3" class="warning">交易退款</th>
                        <th style="text-align: center;" colspan="3">代付出款</th>
                        <th style="text-align: center;" colspan="3" class="warning">代付退款</th>
                        <th style="text-align: center;" colspan="2">调账加款</th>
                        <th style="text-align: center;" colspan="2" class="warning">调账扣款</th>
                        <th style="text-align: center;" colspan="2">冲正加款</th>
                        <th style="text-align: center;" colspan="2" class="warning">冲正扣款</th>
                    <#else>
                        <th style="text-align: center;" colspan="3" class="warning">在线支付</th>
                        <th style="text-align: center;" colspan="3">商户转账(出)</th>
                        <th style="text-align: center;" colspan="2" class="warning">商户转账(入)</th>
                        <th style="text-align: center;" colspan="3">交易退款</th>
                        <th style="text-align: center;" colspan="3" class="warning">代付出款</th>
                        <th style="text-align: center;" colspan="3">代付退款</th>
                        <th style="text-align: center;" colspan="2" class="warning">调账加款</th>
                        <th style="text-align: center;" colspan="2" >调账扣款</th>
                        <th style="text-align: center;" colspan="2"class="warning">冲正加款</th>
                        <th style="text-align: center;" colspan="2" >冲正扣款</th>
                    </#if>
                    <th style="text-align: center;">操作</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <#if paramMap.type??&&paramMap.type=="merchant"><td>商户ID</td><td style="min-width: 100px;">商户名</td><#else><td>日期</td></#if>
                    <#if paramMap.splitPayment??&&paramMap.splitPayment=='1'>
                <#--网银支付-->
                    <td class="warning">总金额</td>
                    <td class="warning">手续费</td>
                    <td class="warning">笔数</td>
                <#--网银支付-->

                <#--网银充值-->
                    <td>总金额</td>
                    <td>手续费</td>
                    <td>笔数</td>
                <#--网银充值-->

                <#--快捷支付-->
                    <td  class="warning">总金额</td>
                    <td  class="warning">手续费</td>
                    <td  class="warning">笔数</td>
                <#--快捷支付-->

                <#--QQ扫码-->
                    <td>总金额</td>
                    <td>手续费</td>
                    <td>笔数</td>
                <#--QQ扫码-->

                <#--微信扫码-->
                    <td  class="warning">总金额</td>
                    <td  class="warning">手续费</td>
                    <td  class="warning">笔数</td>
                <#--微信扫码-->

                <#--支付宝-->
                    <td>总金额</td>
                    <td>手续费</td>
                    <td>笔数</td>
                <#--支付宝-->

                <#--商户转账 出-->
                    <td  class="warning">总金额</td>
                    <td  class="warning">手续费</td>
                    <td  class="warning">笔数</td>
                <#--商户转账 出-->

                <#--商户转账 入-->
                    <td>总金额</td>
                    <td>笔数</td>
                <#--商户转账 入-->

                <#--交易退款-->
                    <td  class="warning">总金额</td>
                    <td  class="warning">手续费</td>
                    <td  class="warning">笔数</td>
                <#--交易退款-->

                <#--代付出款-->
                    <td>总金额</td>
                    <td>手续费</td>
                    <td>笔数</td>
                <#--代付出款-->

                <#--代付退款-->
                    <td  class="warning">总金额</td>
                    <td  class="warning">手续费</td>
                    <td  class="warning">笔数</td>
                <#--代付退款-->

                <#--调账加款-->
                    <td>总金额</td>
                    <td>笔数</td>
                <#--调账加款-->

                <#--调账扣款-->
                    <td  class="warning">总金额</td>
                    <td  class="warning">笔数</td>
                <#--调账扣款-->

                <#--冲正加款-->
                    <td>总金额</td>
                    <td>笔数</td>
                <#--冲正加款-->

                <#--冲正扣款-->
                    <td  class="warning">总金额</td>
                    <td  class="warning">笔数</td>
                <#--冲正扣款-->
                    <#else >
                    <#--在线支付-->
                        <td class="warning">总金额</td>
                        <td class="warning">手续费</td>
                        <td class="warning">笔数</td>
                    <#--在线支付-->
                    <#--商户转账 出-->
                        <td >总金额</td>
                        <td >手续费</td>
                        <td >笔数</td>
                    <#--商户转账 出-->
                    <#--商户转账 入-->
                        <td class="warning">总金额</td>
                        <td class="warning">笔数</td>
                    <#--商户转账 入-->
                    <#--交易退款-->
                        <td>总金额</td>
                        <td>手续费</td>
                        <td>笔数</td>
                    <#--交易退款-->
                    <#--代付出款-->
                        <td class="warning">总金额</td>
                        <td class="warning">手续费</td>
                        <td class="warning">笔数</td>
                    <#--代付出款-->

                    <#--代付退款-->
                        <td >总金额</td>
                        <td >手续费</td>
                        <td >笔数</td>
                    <#--代付退款-->

                    <#--调账加款-->
                        <td class="warning">总金额</td>
                        <td class="warning">笔数</td>
                    <#--调账加款-->

                    <#--调账扣款-->
                        <td >总金额</td>
                        <td >笔数</td>
                    <#--调账扣款-->

                    <#--冲正加款-->
                        <td class="warning">总金额</td>
                        <td class="warning">笔数</td>
                    <#--冲正加款-->

                    <#--冲正扣款-->
                        <td>总金额</td>
                        <td>笔数</td>
                    <#--冲正扣款-->
                    </#if>
                    <td>--</td>
                </tr>


                <#if lists?? &&lists?size&gt;0>
                    <#list lists as list>
                            <!-- 以下行 为数据行 -->
                            <tr>
                                <#if paramMap.type??&&paramMap.type=="merchant">
                                <td title="<#if list.PARTNER_ID??>${list.PARTNER_ID}</#if>">
                                    <#if list.PARTNER_ID?? && list.PARTNER_ID?length lt 13>
                                        ${list.PARTNER_ID}
                                    <#else>
                                        <span class="text-info">${list.PARTNER_ID?substring(12,list.PARTNER_ID?length)}</span>
                                    </#if>
                                </td>
                                <td>${list.PARTNER_NAME}</td>
                                <#else><td>${list.CONFIRM_TIME?substring(5)}</td>
                                </#if>
                        <#if paramMap.splitPayment??&&paramMap.splitPayment=='1'>
                                <#--网银在线支付-->
                                <td class="warning text-danger">${list.SUM_BANK_PAY?string("#,##0.00")}</td>
                                <td class="warning text-danger">${list.SUM_FEE_BANK_PAY?string("#,##0.00")}</td>
                                <td class="warning">${list.TOTAL_TYPE_BANK_PAY}</td>
                            <#--网银在线支付-->

                            <#--网银在线充值-->
                                <td class="text-danger">${list.SUM_BANK_RECHARGE?string("#,##0.00")}</td>
                                <td class="text-danger">${list.SUM_FEE_BANK_RECHARGE?string("#,##0.00")}</td>
                                <td>${list.TOTAL_TYPE_BANK_RECHARGE}</td>
                            <#--网银在线充值-->

                            <#--快捷支付-->
                                <td class="warning text-danger"> ${list.SUM_QPAY_QRCODE?string("#,##0.00")}</td>
                                <td class="warning text-danger">${list.SUM_FEE_QPAY_QRCODE?string("#,##0.00")}</td>
                                <td class="warning">${list.TOTAL_TYPE_QPAY}</td>
                            <#--快捷支付-->

                            <#--QQ扫码-->
                                <td class="text-danger">${list.SUM_QQPAY_QRCODE?string("#,##0.00")}</td>
                                <td class="text-danger">${list.SUM_FEE_QQPAY_QRCODE?string("#,##0.00")}</td>
                                <td>${list.TOTAL_TYPE_QQPAY_QRCODE}</td>
                            <#--QQ扫码-->

                            <#--微信扫码-->
                                <td class="warning text-danger">${list.SUM_WEIXIN_QRCODE?string("#,##0.00")}</td>
                                <td class="warning text-danger">${list.SUM_FEE_WEIXIN_QRCODE?string("#,##0.00")}</td>
                                <td class="warning">${list.TOTAL_TYPE_WEIXIN}</td>
                            <#--微信扫码-->

                            <#--支付宝-->
                                <td class="text-danger">${list.SUM_ALIPAY_QRCODE?string("#,##0.00")}</td>
                                <td class="text-danger">${list.SUM_FEE_ALIPAY_QRCODE?string("#,##0.00")}</td>
                                <td>${list.TOTAL_TYPE_ALIPAY}</td>
                            <#--支付宝-->

                            <#--商户转账 出-->
                                <td class="warning text-danger">${list.SUM_TRANS_FROM_ACCOUNT?string("#,##0.00")}</td>
                                <td class="warning text-danger">${list.SUM_FEE_TRANS_FROM_ACCOUNT?string("#,##0.00")}</td>
                                <td class="warning">${list.TOTAL_TYPE_TRANS_FROM_ACCOUNT}</td>
                            <#--商户转账 出-->

                            <#--商户转账 入-->
                                <td class="text-danger">${list.SUM_TRANS_TO_ACCOUNT?string("#,##0.00")}</td>
                                <td>${list.TOTAL_TYPE_TRANS_TO_ACCOUNT}</td>
                            <#--商户转账 入-->

                            <#--交易退款-->
                                <td class="warning text-danger">${list.SUM_REFUND_FROM_TRADE?string("#,##0.00")}</td>
                                <td class="warning text-danger">${list.SUM_FEE_REFUND_FROM_TRADE?string("#,##0.00")}</td>
                                <td class="warning">${list.TOTAL_TYPE_REFUND_FROM_TRADE}</td>
                            <#--交易退款-->

                            <#--代付出款-->
                                <td class="text-danger">${list.SUM_TRANS_TO_CARD?string("#,##0.00")}</td>
                                <td class="text-danger">${list.SUM_FEE_TRANS_TO_CARD?string("#,##0.00")}</td>
                                <td>${list.TOTAL_TYPE_TRANS_TO_CARD}</td>
                            <#--代付出款-->

                            <#--代付退款-->
                                <td class="warning text-danger">${list.SUM_REFUND_FROM_MENTION?string("#,##0.00")}</td>
                                <td class="warning text-danger">${list.SUM_FEE_REFUND_FROM_MENTION?string("#,##0.00")}</td>
                                <td class="warning">${list.TOTAL_TYPE_REFUND_FROM_MENTION}</td>
                            <#--代付退款-->

                            <#--调账加款-->
                                <td class="text-danger">${list.SUM_ADJUST_ADD_TO_MERCHANT?string("#,##0.00")}</td>
                                <td>${list.TOTAL_TYPE_ADJUST_ADD_TO_MERCHANT}</td>
                            <#--调账加款-->

                            <#--调账扣款-->
                                <td class="warning text-danger">${list.SUM_ADJUST_SUB_FROM_MERCHANT?string("#,##0.00")}</td>
                                <td class="warning">${list.TOTAL_TYPE_ADJUST_SUB_FROM_MERCHANT}</td>
                            <#--调账扣款-->

                            <#--冲正加款-->
                                <td class="text-danger">${list.SUM_CORRECT_ADD_TO_MERCHANT?string("#,##0.00")}</td>
                                <td>${list.TOTAL_TYPE_CORRECT_ADD_TO_MERCHANT}</td>
                            <#--冲正加款-->

                            <#--冲正扣款-->
                                <td class="warning text-danger">${list.SUM_CORRECT_SUB_FROM_MERCHANT?string("#,##0.00")}</td>
                                <td class="warning">${list.TOTAL_TYPE_CORRECT_SUB_FROM_MERCHANT}</td>
                            <#--冲正扣款-->
                        <#else>
                        <#--在线支付-->
                            <td class="warning text-danger">${list.SUM_ONLINE_PAY?string("#,##0.00")}</td>
                            <td class="warning text-danger">${list.SUM_FEE_ONLINE_PAY?string("#,##0.00")}</td>
                            <td class="warning">${list.TOTAL_TYPE_ONLINE_PAY}</td>
                        <#--在线支付-->
                        <#--商户转账 出-->
                            <td class="text-danger">${list.SUM_TRANS_FROM_ACCOUNT?string("#,##0.00")}</td>
                            <td class="text-danger">${list.SUM_FEE_TRANS_FROM_ACCOUNT?string("#,##0.00")}</td>
                            <td class="">${list.TOTAL_TYPE_TRANS_FROM_ACCOUNT}</td>
                        <#--商户转账 出-->

                        <#--商户转账 入-->
                            <td class="warning text-danger">${list.SUM_TRANS_TO_ACCOUNT?string("#,##0.00")}</td>
                            <td class="warning">${list.TOTAL_TYPE_TRANS_TO_ACCOUNT}</td>
                        <#--商户转账 入-->

                        <#--交易退款-->
                            <td class="text-danger">${list.SUM_REFUND_FROM_TRADE?string("#,##0.00")}</td>
                            <td class="text-danger">${list.SUM_FEE_REFUND_FROM_TRADE?string("#,##0.00")}</td>
                            <td >${list.TOTAL_TYPE_REFUND_FROM_TRADE}</td>
                        <#--交易退款-->

                        <#--代付出款-->
                            <td class="warning text-danger">${list.SUM_TRANS_TO_CARD?string("#,##0.00")}</td>
                            <td class="warning text-danger">${list.SUM_FEE_TRANS_TO_CARD?string("#,##0.00")}</td>
                            <td class="warning">${list.TOTAL_TYPE_TRANS_TO_CARD}</td>
                        <#--代付出款-->

                        <#--代付退款-->
                            <td class="text-danger">${list.SUM_REFUND_FROM_MENTION?string("#,##0.00")}</td>
                            <td class="text-danger">${list.SUM_FEE_REFUND_FROM_MENTION?string("#,##0.00")}</td>
                            <td >${list.TOTAL_TYPE_REFUND_FROM_MENTION}</td>
                        <#--代付退款-->

                        <#--调账加款-->
                            <td class="warning text-danger">${list.SUM_ADJUST_ADD_TO_MERCHANT?string("#,##0.00")}</td>
                            <td class="warning">${list.TOTAL_TYPE_ADJUST_ADD_TO_MERCHANT}</td>
                        <#--调账加款-->

                        <#--调账扣款-->
                            <td class="text-danger">${list.SUM_ADJUST_SUB_FROM_MERCHANT?string("#,##0.00")}</td>
                            <td >${list.TOTAL_TYPE_ADJUST_SUB_FROM_MERCHANT}</td>
                        <#--调账扣款-->

                        <#--冲正加款-->
                            <td class="warning text-danger">${list.SUM_CORRECT_ADD_TO_MERCHANT?string("#,##0.00")}</td>
                            <td class="warning">${list.TOTAL_TYPE_CORRECT_ADD_TO_MERCHANT}</td>
                        <#--冲正加款-->

                        <#--冲正扣款-->
                            <td class="text-danger">${list.SUM_CORRECT_SUB_FROM_MERCHANT?string("#,##0.00")}</td>
                            <td>${list.TOTAL_TYPE_CORRECT_SUB_FROM_MERCHANT}</td>
                        <#--冲正扣款-->
                        </#if>
                                <td><a href="finance/exportBillingStatics?type=${paramMap.type}&<#if paramMap.type??&&paramMap.type=="merchant">partnerId=${list.PARTNER_ID}<#else>confirmTime=${list.CONFIRM_TIME}&partnerId=${list.PARTNER_ID}</#if>&tbname=${paramMap.tbname}&startTime=${paramMap.startTime}&endTime=${paramMap.endTime}<#if paramMap.splitPayment??>&splitPayment=${paramMap.splitPayment}</#if>" title="导出财务报表" class="fa fa-file-excel-o"></a></td>
                            </tr>
                    </#list>
                <tr style="background:#C1FFC1">
                    <td <#if paramMap.splitPayment??&&paramMap.splitPayment=='1'><#if paramMap.type??&&paramMap.type=="merchant">colspan="43"<#else >colspan="42"</#if><#else><#if paramMap.type??&&paramMap.type=="merchant">colspan="28"<#else >colspan="27"</#if></#if>>
                        报表信息汇总:
                        <#if totalMap??>
                            <#if paramMap.splitPayment??&&paramMap.splitPayment=='1'>
                                网银支付总金额：<b style="color:red">${totalMap.SUM_BANK_PAY?string("#,##0.00")}</b> 元
                                网银支付手续费：<b style="color:red">${totalMap.SUM_FEE_BANK_PAY?string("#,##0.00")}</b> 元
                                笔数：<b style="color:red">${totalMap.TOTAL_TYPE_BANK_PAY}</b> 笔
                                &nbsp;&nbsp;
                                网银充值总金额：<b style="color:red">${totalMap.SUM_BANK_RECHARGE?string("#,##0.00")}</b> 元
                                网银充值手续费：<b style="color:red">${totalMap.SUM_FEE_BANK_RECHARGE?string("#,##0.00")}</b> 元
                                笔数：<b style="color:red">${totalMap.TOTAL_TYPE_BANK_RECHARGE}</b> 笔
                                &nbsp;&nbsp;
                                快捷支付总金额：<b style="color:red">${totalMap.SUM_QPAY_QRCODE?string("#,##0.00")}</b> 元
                                快捷支付手续费：<b style="color:red">${totalMap.SUM_FEE_QPAY_QRCODE?string("#,##0.00")}</b> 元
                                笔数：<b style="color:red">${totalMap.TOTAL_TYPE_QPAY}</b> 笔
                                &nbsp;&nbsp;
                                QQ扫码总金额：<b style="color:red">${totalMap.SUM_QQPAY_QRCODE?string("#,##0.00")}</b> 元
                                QQ扫码手续费：<b style="color:red">${totalMap.SUM_FEE_QQPAY_QRCODE?string("#,##0.00")}</b> 元
                                笔数：<b style="color:red">${totalMap.TOTAL_TYPE_QQPAY_QRCODE}</b> 笔
                                &nbsp;&nbsp;
                                微信扫码总金额：<b style="color:red">${totalMap.SUM_WEIXIN_QRCODE?string("#,##0.00")}</b> 元
                                微信扫码手续费：<b style="color:red">${totalMap.SUM_FEE_WEIXIN_QRCODE?string("#,##0.00")}</b> 元
                                笔数：<b style="color:red">${totalMap.TOTAL_TYPE_WEIXIN}</b> 笔
                                &nbsp;&nbsp;
                                支付宝总金额：<b style="color:red">${totalMap.SUM_ALIPAY_QRCODE?string("#,##0.00")}</b> 元
                                支付宝手续费：<b style="color:red">${totalMap.SUM_FEE_ALIPAY_QRCODE?string("#,##0.00")}</b> 元
                                笔数：<b style="color:red">${totalMap.TOTAL_TYPE_ALIPAY}</b> 笔
                                &nbsp;&nbsp;
                            <#else>
                                在线支付总金额：<b style="color:red">${totalMap.SUM_ONLINE_PAY?string("#,##0.00")}</b> 元
                                在线支付手续费：<b style="color:red">${totalMap.SUM_FEE_ONLINE_PAY?string("#,##0.00")}</b> 元
                                笔数：<b style="color:red">${totalMap.TOTAL_TYPE_ONLINE_PAY}</b> 笔
                                &nbsp;&nbsp;
                            </#if>
                            商户转账(出)总金额：<b style="color:red">${totalMap.SUM_TRANS_FROM_ACCOUNT?string("#,##0.00")}</b> 元
                            商户转账(出)手续费：<b style="color:red">${totalMap.SUM_FEE_TRANS_FROM_ACCOUNT?string("#,##0.00")}</b> 元
                            笔数：<b style="color:red">${totalMap.TOTAL_TYPE_TRANS_FROM_ACCOUNT}</b> 笔
                            &nbsp;&nbsp;
                            商户转账(入)总金额：<b style="color:red">${totalMap.SUM_TRANS_TO_ACCOUNT?string("#,##0.00")}</b> 元
                            笔数：<b style="color:red">${totalMap.TOTAL_TYPE_TRANS_TO_ACCOUNT}</b> 笔
                            &nbsp;&nbsp;
                            交易退款总金额：<b style="color:red">${totalMap.SUM_REFUND_FROM_TRADE?string("#,##0.00")}</b> 元
                            交易退款手续费：<b style="color:red">${totalMap.SUM_FEE_REFUND_FROM_TRADE?string("#,##0.00")}</b> 元
                            笔数：<b style="color:red">${totalMap.TOTAL_TYPE_REFUND_FROM_TRADE}</b> 笔
                            &nbsp;&nbsp;
                            代付出款总金额：<b style="color:red">${totalMap.SUM_TRANS_TO_CARD?string("#,##0.00")}</b> 元
                            代付出款手续费：<b style="color:red">${totalMap.SUM_FEE_TRANS_TO_CARD?string("#,##0.00")}</b> 元
                            笔数：<b style="color:red">${totalMap.TOTAL_TYPE_TRANS_TO_CARD}</b> 笔
                            &nbsp;&nbsp;
                            代付退款总金额：<b style="color:red">${totalMap.SUM_REFUND_FROM_MENTION?string("#,##0.00")}</b> 元
                            代付退款手续费：<b style="color:red">${totalMap.SUM_FEE_REFUND_FROM_MENTION?string("#,##0.00")}</b> 元
                            笔数：<b style="color:red">${totalMap.TOTAL_TYPE_REFUND_FROM_MENTION}</b> 笔
                            &nbsp;&nbsp;
                            调账加款总金额：<b style="color:red">${totalMap.SUM_ADJUST_ADD_TO_MERCHANT?string("#,##0.00")}</b> 元
                            笔数：<b style="color:red">${totalMap.TOTAL_TYPE_ADJUST_ADD_TO_MERCHANT}</b> 笔
                            &nbsp;&nbsp;
                            调账扣款总金额：<b style="color:red">${totalMap.SUM_ADJUST_SUB_FROM_MERCHANT?string("#,##0.00")}</b> 元
                            笔数：<b style="color:red">${totalMap.TOTAL_TYPE_ADJUST_SUB_FROM_MERCHANT}</b> 笔
                            &nbsp;&nbsp;
                            冲正加款总金额：<b style="color:red">${totalMap.SUM_CORRECT_ADD_TO_MERCHANT?string("#,##0.00")}</b> 元
                            笔数：<b style="color:red">${totalMap.TOTAL_TYPE_CORRECT_ADD_TO_MERCHANT}</b> 笔
                            &nbsp;&nbsp;
                            冲正扣款总金额：<b style="color:red">${totalMap.SUM_CORRECT_SUB_FROM_MERCHANT?string("#,##0.00")}</b> 元
                            笔数：<b style="color:red">${totalMap.TOTAL_TYPE_CORRECT_SUB_FROM_MERCHANT}</b> 笔
                            &nbsp;&nbsp;
                        </#if>
                    </td>
                </tr>
                <#else >
                    <tr class="no-data">
                        <td <#if paramMap.splitPayment??&&paramMap.splitPayment=='1'><#if paramMap.type??&&paramMap.type=="merchant">colspan="43"<#else >colspan="42"</#if><#else><#if paramMap.type??&&paramMap.type=="merchant">colspan="28"<#else >colspan="27"</#if></#if>><em>很抱歉,未查询到任何数据信息!</em></td>
                    </tr>
                </#if>
            </tbody>
        </table>
        <#if inajax=="0">
    </div>
    <!-- list-div end -->

</section>

    <#include "../footer.ftl">
        </#if>
<script type="text/javascript">

    layui.use("laydate",function () {
        var laydate = layui.laydate;

        laydate.render({
            elem: '#startTime' //指定元素
            ,type: 'datetime' //日期时间选择器
            ,format: 'yyyy-MM-dd HH:mm:ss' //可任意组合
            ,max: 1 //1天后
            ,istime: true
        });

        //执行一个laydate实例
        laydate.render({
            elem: '#endTime' //指定元素
            ,type: 'datetime' //日期时间选择器
            ,format: 'yyyy-MM-dd HH:mm:ss' //可任意组合
            ,max: 1 //1天后
            ,istime: true
        });
    });




    function exportBillingStatics(){
        var frm = document.pagefrm;
        frm.action="finance/exportBillingStatics";
        frm.submit();
        frm.action="finance/billingStatics";
    }
</script>

</#escape>