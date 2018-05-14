<#escape x as (x)!>
<#if inajax=="0">
	<#include "../header.ftl">
<#--laydate 时间控件-->
<#--配置文件 -->
<link rel="stylesheet"  href="static/plugins/layui/css/layui.css"/>
<script type="text/javascript"  src="static/plugins/layui/layui.js" charset="utf-8"></script>


    <!-- header start -->
    <section class="content-header">
        <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
                <li class="active"><a href="orders/refund">退款订单</a></li>
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
                    <h1>搜索退款订单</h1>
                </div>
                <form class="form-horizontal" method="post" action="orders/refund">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">商户ID号：</label>
                            <input type="text" name="partner_id" id="partner_id" class="form-control input-sm" value="${partner_id}">
                        </div>
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">退款订单号：</label>
                            <input type="text" name="order_sn" id="order_sn" class="form-control input-sm" value="${order_sn}">
                        </div>
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">商户订单号：</label>
                            <input type="text" name="out_trade_no" id="out_trade_no" class="form-control input-sm" value="${out_trade_no}">
                        </div>
                    </div>
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">确认时间：</label>
                            <input type="text" name="startTime" readonly id="startTime" class="form-control input-sm input-append date" value="${startTime}">
                            <input type="text" name="endTime"   readonly id="endTime"   class="form-control input-sm input-append date" value="${endTime}">
                        </div>
                        <div class="col-xs-6 col-md-6">
                            <label class="control-label">金额区间：</label>
                            <input type="text" name="min_paymoney" id="min_paymoney" class="form-control input-sm" value="${min_paymoney}">
                            <input type="text" name="max_paymoney" id="max_paymoney" class="form-control input-sm" value="${max_paymoney}">
                        </div>
                    </div>
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">流水号：</label>
                            <input type="text" name="billing_sn" id="billing_sn" class="form-control input-sm" value="${billing_sn}">
                        </div>
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">退款状态：</label>
                            <select class="form-control input-sm" name="refund_status" id="refund_status">
                                <option value="">==退款状态==</option>
                                <option value=-1 <#if refund_status?? && refund_status == "-1">selected</#if>>退款失败</option>
                                <option value=0  <#if refund_status?? && refund_status == "0"> selected</#if>>退款中</option>
                                <option value=1  <#if refund_status?? && refund_status == "1"> selected</#if>>退款成功</option>
                                <option value=3  <#if refund_status?? && refund_status == "3"> selected</#if>>退款拒绝</option>
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
            <button id="searchHandle." onclick="toggleSearchDiv();"  type="button" class="btn btn-primary btn-sm">搜索订单</button>
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
                <th>交易类型</th>
                <th>退款订单号</th>
                <th>商户订单号</th>
                <th>流水号</th>
                <th>商户ID</th>
                <th>商户名称</th>
                <th>金额(元)</th>
                <th>退款申请时间</th>
                <th>退款状态</th>
                <th>操作</th>
			</tr>
			</thead>
			<tbody>
				<#if lists?? && lists?size&gt;0>
					<#list lists as list>
                    <tr>
                        <td>
                            <#if list.refundType==3000>    订单退款
                            <#elseif list.refundType==4000>代付退款
                            </#if>
                        </td>
                        <td>${list.orderSn!'------'}</td>
                        <td>${list.outTradeNo!'--'}</td>
                        <td>${list.billingSn!'--'}</td>
                        <td title="${list.partnerId}">
                            <#if list.partnerId?? && list.partnerId?length lt 9>
                                ${list.partnerId}
                            <#else>
                                <span class="text-info">${list.partnerId?substring(8,list.partnerId?length)}</span>
                            </#if>
                        </td>
                        <td>${list.partnerName}</td>
                        <td title="备注信息: ${list.remark!''}">￥${list.orderAmount?string("#0.00")}</td>
                        <td>${list.dateline?string('yyyy-MM-dd HH:mm:ss')!''}</td>
                        <td>
                            <#if list.refundStatus==0>     <span class="label label-info">退款中</span>
                            <#elseif list.refundStatus==1> <span class="label label-success">退款成功</span>
							<#elseif list.refundStatus==3> <span class="label label-warning">退款拒绝</span>
							<#elseif list.refundStatus==-1><span class="label label-danger">退款失败</span>
							</#if>
                        </td>
                        <td>
                            <#if list.refundType==3000 >
                                <#if list.refundStatus==0>
                                    <a href="javascript:void(0);" onclick="confirmRefund('${list.refundType}','${list.partnerId}','${list.orderSn}','${list.outTradeNo}', 1)">批准退款</a> |
                                    <a href="javascript:void(0);" onclick="confirmRefund('${list.refundType}','${list.partnerId}','${list.orderSn}','${list.outTradeNo}',-1)">拒绝退款</a>
                                    <#if list.isChannelRefund == 1>
                                        <a href="javascript:void(0);" onclick="upRefund('${list.partnerId}','${list.orderSn}','${list.outTradeNo}')"> | 渠道退款</a>
                                    </#if>
                                <#else>
                                    ---
                                </#if>
                            <#else >
                                ---
							</#if>
                        </td>
                    </tr>
					</#list>
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





    function confirmRefund(refund_type, partner_id, order_sn, out_trade_no, refund_status){
        var statusMsg = "拒绝";
        if(refund_status==1){  statusMsg = "通过"; }
        if(refund_status==-1){ statusMsg = "失败"; }
        $.confirm({
            title: '系统提示',
            content: "<label class='text-danger' size='4'>是否确定"+ statusMsg +"该笔退款申请?</label>",
            buttons: {
                ok: function () {
                    var params={'refund_type':refund_type,'partner_id':partner_id,'order_sn':order_sn,'out_trade_no':out_trade_no,'refund_status':refund_status};
                    $.ajax({
                        type: "post",
                        async:false,
                        url: "orders/dorefund",
                        data: params,
                        dataType : "json",
                        success: function(data, textStatus){
                            if(data==null || data==""){
                                $.alert('系统错误！', '系统提示');
                                return false;
                            }
                            if(data.resultCode=="RESP_SUCCESS"){
                                $.confirm({
                                    title: "温馨提示",
                                    content: data.resultMsg,
                                    buttons: {
                                        ok: function(){
                                            document.location.reload();
                                        }
                                    }
                                })
                            }else{
                                $.alert(data.resultMsg, '温馨提示');
                            }
                        },
                        error: function(){
                            $.alert('系统出错', '温馨提示');
                        }
                    });
                },
                cancel: function () {
                    'btn-blue'
                }
            }
        });
    }

//    渠道退款
    function upRefund(partnerId, order_sn, outTradeNo) {
        $.confirm({
            title: "系统提醒",
            content:"是否确定往上游提交该笔退款申请?",
            buttons: {
                ok: function(){
                    var params={'partner_id':partnerId,'order_sn':order_sn,'out_trade_no':outTradeNo};
                    $.ajax({
                        type: "post",
                        url: "orders/subChannelRefund",
                        data: params,
                        dataType : "json",
                        cache: false,
                        success: function(data, textStatus){
                            if(data==null || data==""){
                                $.alert("<label class='red' size='4'>系统错误</label>", {title: "系统提示"});
                            }
                            var result = data.resultCode;
                            if(result=="RESP_SUCCESS"){
                                $.confirm({
                                    title: "系统提示",
                                    content: "<label class='text-success' size='4'>"+ data.resultMsg +"</label>",
                                    buttons: {
                                        ok: function(){
                                            location.reload();
                                        }
                                    }
                                });
                            }else{
                                $.alert("<label class='text-danger' size='4'>"+ data.resultMsg +"</label>",  {title: "系统提示"});
                            }
                        },
                        error: function(){
                            $.alert("<p class='text-danger'>系统响应异常!</p>", null, {title: "系统提示"});
                        }
                    });
                }
            }
        });


    }

</script>

</#escape>