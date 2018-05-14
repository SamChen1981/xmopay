<#escape x as (x)!>
<#if inajax=="0">
	<#include "../header.ftl">
<#--laydate 时间控件-->
<#--配置文件 -->
<link rel="stylesheet"  href="static/public/plugins/layui/css/layui.css"/>
<script type="text/javascript"  src="static/public/plugins/layui/layui.js" charset="utf-8"></script>
<link rel="stylesheet" href="static/public/plugins/bootstrap-multiselect/bootstrap-multiselect.css" />
<script type="text/javascript" src="static/public/plugins/bootstrap-multiselect/bootstrap-multiselect.min.js" charset="UTF-8"></script>
    <!-- header start -->
    <section class="content-header">
        <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
                <li class="active"><a href="orders/statistic">交易统计</a></li>
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
                <h1>搜索交易</h1>
            </div>
            <form class="form-horizontal" method="post" name="pagefrm" id="pagefrm" action="orders/statistic">
                <input id="isSearch" name="isSearch" type="hidden" value="1">
                <div class="form-group form-inline">
                    <div class="col-xs-6 col-md-6">
                        <label class="control-label">交易日期：</label>
                        <input type="text" name="startTime" readonly id="startTime" class="form-control input-sm" value="${startTime}">
                        <input type="text" name="endTime" readonly id="endTime" class="form-control input-sm" value="${endTime}">
                    </div>
                    <div class="col-xs-6 col-md-6">
                        <label class="control-label">交易金额：</label>
                        <input type="text" name="minPayMoney" id="minPayMoney" class="form-control input-sm" value="${minPayMoney}">
                        <input type="text" name="maxPayMoney" id="maxPayMoney" class="form-control input-sm" value="${maxPayMoney}">
                    </div>
                </div>

                <div class="form-group form-inline">
                    <div class="col-xs-6 col-md-3">
                        <label class="control-label">支付网关：</label>
                        <select class="form-control input-sm" name="channel_code" id="channel_code" onchange="fac(this.id)">
                            <option value="">==支付网关==</option>
                            <#if gatewayList??>
                                <#list gatewayList as list>
                                    <option value="${list.channelCode}" <#if channel_code?? && list.channelCode == channel_code>selected</#if>>${list.channelName}</option>
                                </#list>
                            </#if>
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
            <button id="searchHandle." onclick="toggleSearchDiv();"  type="button" class="btn btn-primary btn-sm">搜索交易</button>
            <a class="btn-a" href="javascript:exportTradeStatics();"><span>导出统计</span></a>
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
            <th>交易日期</th>
            <th>渠道</th>
            <th>在线支付总额</th>
            <th>笔数</th>
        </tr>
        </thead>

        <tbody>
			<#if lists?? && lists?size&gt;0>
				<#list page.elements as list>
                <tr overstyle="on">
                    <td>${list.FINISH_TIME!'------'}</td>
                    <td>[${list.CHANNEL_CODE}] ${list.CHANNEL_NAME!'------'}</td>
                    <td><strong class="text-danger">￥${list.ORDER_AMOUNT?string("#,##0.00")}</strong></td>
                    <td>${list.NUM}</td>
                </tr>
				</#list>
                <tr overstyle="on">
                    <td colspan="4">
                        <#if daymap?? && daymap?size&gt;0>
                            <label>交易金额：<span class="text-success">${daymap.order_amount?string("#,##0.00")}</span></label>
                            <label>交易笔数：<span class="text-success">${daymap.order_count}</span></label>
                        </#if>
                    </td>
                </tr>
			<#else>
            <tr class="no-data">
                <td colspan="5"><em>很抱歉,未查询到任何数据信息!</em></td>
            </tr>
			</#if>
        </tbody>
    </table>
	<#if inajax=="0">
    </div>
        <!-- list-div end -->
    </section>
</div>
<#include "../footer.ftl">
    </#if>
<script type="text/javascript">
    $(document).ready(function () {
        $("#channel_code").multiselect({
            enableFiltering: true,
            buttonWidth: '160px',
            maxHeight: 400,
            onChange: function(option, select) {
                adjustByScrollHeight();
            },
            onDropdownShown: function(e) {
                adjustByScrollHeight();
            },
            onDropdownHidden: function(e) {
                adjustByHeight();
            }
        });
        function adjustByHeight() {
            var $body   = $('body'),
                    $iframe = $body.data('iframe.fv');
            if ($iframe) {
                // Adjust the height of iframe when hiding the picker
                $iframe.height($body.height());
            }
        }

        function adjustByScrollHeight() {
            var $body   = $('body'),
                    $iframe = $body.data('iframe.fv');
            if ($iframe) {
                // Adjust the height of iframe when showing the picker
                $iframe.height($body.get(0).scrollHeight/2);
            }
        }
    });
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
    function exportTradeStatics(){
        var frm = document.pagefrm;
        frm.action="orders/exportTradeStatics";
        frm.submit();
        frm.action="orders/statistic";
    }
</script>

</#escape>