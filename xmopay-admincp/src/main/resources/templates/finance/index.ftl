<#escape x as (x)!>
<#if inajax=="0">
	<#include "../header.ftl">
<#--laydate 时间控件-->
<#--配置文件 -->
<link rel="stylesheet"  href="static/public/plugins/layui/css/layui.css"/>
<script type="text/javascript"  src="static/public/plugins/layui/layui.js" charset="utf-8"></script>

    <!-- header start -->
    <section class="content-header">
        <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
                <li class="active"><a href="finance/index">财务明细</a></li>
            </ul>
        </div>
    </section>
    <!-- header end -->

    <section id="listDiv" class="content-body">
</#if>
        <!-- searchDiv start -->
        <div id="hideSearchDiv" class="row searchDiv" style="display:none">
            <div class="col-sm-12">
                <div class="page-header">
                    <h1>搜索列表</h1>
                </div>
                <form class="form-horizontal" id="actionForm" method="post" action="finance/index">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">商户ID号：</label>
                            <input type="text" name="partner_id" id="partner_id" class="form-control input-sm" value="${partner_id}">
                        </div>
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">平台单号：</label>
                            <input type="text" name="order_sn" id="order_sn" class="form-control input-sm" value="${order_sn}">
                        </div>
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">流水号：</label>
                            <input type="text" name="billing_sn" id="billing_sn" class="form-control input-sm" value="${billing_sn}">
                        </div>
                    </div>
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-6">
                            <label class="control-label">交易时间：</label>
                            <input type="text" name="startTime" readonly id="startTime" class="form-control input-sm" value="${startTime}">
                            <input type="text" name="endTime"   readonly id="endTime"   class="form-control input-sm" value="${endTime}">
                        </div>
                        <div class="col-xs-6 col-md-6">
                            <label class="control-label">交易金额：</label>
                            <input type="text" name="min_paymoney" id="min_paymoney" class="form-control input-sm" value="${min_paymoney}">
                            <input type="text" name="max_paymoney" id="max_paymoney" class="form-control input-sm" value="${max_paymoney}">
                        </div>
                    </div>
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">流水类型：</label>
                            <select class="form-control input-sm" name="bill_type" id="bill_type">
                                <option value="">==流水类型==</option>
                                <option value="1000" <#if bill_type?? && bill_type == "1000">selected</#if>>在线支付</option>
                                <option value="1001" <#if bill_type?? && bill_type == "1001">selected</#if>>在线充值</option>
                                <option value="2000" <#if bill_type?? && bill_type == "2000">selected</#if>>转账到户</option>
                                <option value="2001" <#if bill_type?? && bill_type == "2001">selected</#if>>对公提现</option>
                                <option value="2002" <#if bill_type?? && bill_type == "2002">selected</#if>>转账到卡</option>
                                <option value="2003" <#if bill_type?? && bill_type == "2003">selected</#if>>境外提现</option>
                                <option value="2005" <#if bill_type?? && bill_type == "2005">selected</#if>>分润加款</option>
                                <option value="3000" <#if bill_type?? && bill_type == "3000">selected</#if>>订单退款</option>
                                <option value="4000" <#if bill_type?? && bill_type == "4000">selected</#if>>提现退款</option>
                                <option value="4001" <#if bill_type?? && bill_type == "4001">selected</#if>>调账加款</option>
                                <option value="4002" <#if bill_type?? && bill_type == "4002">selected</#if>>调账扣款</option>
                                <option value="5001" <#if bill_type?? && bill_type == "5001">selected</#if>>冲正加款</option>
                                <option value="5002" <#if bill_type?? && bill_type == "5002">selected</#if>>冲正扣款</option>
                            </select>
                        </div>
					</div>
                    <button type="submit" class="btn btn-default">搜索</button>
                </form>
            </div>
        </div>
        <!-- search div end -->

    <!-- list-div start-->
    <div class="row list-toolbar">
        <div class="col-sm-6">
            <button id="searchHandle." onclick="toggleSearchDiv();"  type="button" class="btn btn-primary btn-sm">搜索列表</button>
        </div>
        <div class="col-sm-6">
            <nav class="pages pull-right">
                <ul id="testpage" class="pagination pagination-sm">${pages}</ul>
            </nav>
        </div>
    </div>
<div class="row">
    <table class="table table-hover">
        <thead>
        <tr>
            <th>业务类型</th>
            <th>订单号</th>
            <th>流水号</th>
            <th>商户ID</th>
            <th>交易金额</th>
            <th>交易手续费</th>
            <th>实付金额</th>
            <th>账户当前余额</th>
            <th>交易时间</th>
            <th>交易状态</th>
        </tr>
        </thead>
        <tbody>
			<#if lists?? && lists?size&gt;0>
				<#list lists as list>
                <tr id="userid-${list.bid}" title="${list.remark!"备注信息为空"}" overstyle="on">
                    <td>
						<#if list.billType==1000>在线支付
						<#elseif list.billType==1001>在线充值
						<#elseif list.billType==2000>转账到户
						<#elseif list.billType==2001>对公提现
						<#elseif list.billType==2002>转账到卡
						<#elseif list.billType==2003>境外提现
						<#elseif list.billType==2005>分润加款
						<#elseif list.billType==3000>订单退款
						<#elseif list.billType==4000>提现退款
						<#elseif list.billType==4001>调账加款
						<#elseif list.billType==4002>调账扣款
						<#elseif list.billType==5001>冲正加款
						<#elseif list.billType==5002>冲正扣款
						</#if>
                    </td>
                    <td>${list.orderSn}</td>
                    <td>${list.billingSn}</td>
                    <td title="${list.partnerName}">${list.partnerId} </td>
                    <td>
						<#if list.billType==1000 || list.billType==1001 || list.billType==4000 || list.billType==4001 || list.billType==5001>
                            <label class="text-success">+${list.tradeAmount?string("#,##0.00")}</label>
						<#else>
                            <label class="text-danger">-${list.tradeAmount?string("#,##0.00")}</label>
						</#if>
                    </td>
                    <td>
						<#if list.billType==4000 || list.billType==4001 || list.billType==5001>
                            <label class="text-success">+${list.tradeFee?string("#,##0.00")}</label>
						<#else>
                            <label class="text-danger">-${list.tradeFee?string("#,##0.00")}</label>
						</#if>
                    </td>
                    <td>
                        <#if list.billType==1000 || list.billType==1001 || list.billType==4000 || list.billType==4001 || list.billType==5001>
                            <label class="text-success">+${list.payment?string("#,##0.00")}</label>
                        <#else>
                            <label class="text-danger">-${list.payment?string("#,##0.00")}</label>
                        </#if>
                    </td>
					<td><label class="text-default">￥${list.accountAmount?string("##0.00")}</label></td>
                    <td>${list.tradeTime?string("yyyy-MM-dd HH:mm:ss")}</td>
                    <td>
						<#if list.payStatus==0><span class="label label-primary">交易中</span>
						<#elseif list.payStatus==1><span class="label label-success">交易成功</span>
						<#elseif list.payStatus==-1><span class="label label-danger">交易失败</span>
						</#if>
                    </td>
                </tr>
				</#list>
                <tr style="background:#C1FFC1">
                    <td colspan="10">
                        <button class="btn btn-primary btn-sm" onclick="getTotal()" type="button" id="totalButton">财务明细汇总</button>
                        <div id="totalDiv"  style="display: none;">
                            交易汇总结果：
                            <span class="text-success" id="transfer_success"></span>&nbsp;&nbsp;
                            <span class="text-primary" id="success_num"></span>&nbsp;&nbsp;
                            <span class="text-danger" id="transfer_failed"></span>&nbsp;&nbsp;
                        </div>
                    </td>
                </tr>
			<#else>
            <tr class="no-data">
                <td colspan="10"><em>很抱歉,未查询到任何数据信息!</em></td>
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
    <script>

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

        function getTotal(){
            var form =$("#actionForm");
            var param = form.serialize();
            $.ajax({
                url:"finance/getTotal",
                type:"POST",
                data:param,
                dataType:"json",
                success:function (data) {
                    var result = data.resultData;
                    $("#transfer_success").html("交易金额：<b style='color:red'>￥"+result.TRADE_SUCCESS+"</b>");
                    $("#success_num").html("笔数：<b style='color:red'>"+result.SUCCESS_NUM+"</b>");
                    $("#totalDiv").css("display","inline");
                },
                error:function () {
                    $.alert('系统异常', '系统提示');
                }
            });
        }
    </script>

</#escape>