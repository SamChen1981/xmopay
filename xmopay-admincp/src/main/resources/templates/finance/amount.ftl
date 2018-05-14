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
                <li class="active"><a href="finance/amount">商户余额汇总</a></li>
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
                <form action="finance/amount" name="pagefrm" method="post" class="form-horizontal">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <div class="col-xs-12 col-md-12">
                            <label class="control-label">时间：</label>
                            <input type="text" name="startTime" readonly id="startTime" class="form-control input-sm" value="${startTime}">&nbsp;至&nbsp;
                            <input type="text" name="endTime"  readonly id="endTime"   class="form-control input-sm" value="${endTime}">
                            <a href="javascript:" onclick="creTime('minute');"    class="btn btn-small">一分钟内</a>
                            <a href="javascript:" onclick="creTime('hour');"      class="btn btn-small">一个小时内</a>
                            <a href="javascript:" onclick="creTime('day')"        class="btn btn-small">一天内</a>
                            <a href="javascript:" onclick="creTime('week')"       class="btn btn-small">一周内</a>
                            <a href="javascript:" onclick="creTime('month')"      class="btn btn-small">一个月内</a>
                            <a href="javascript:" onclick="creTime('threeMonth')" class="btn btn-small">最近三个月内</a>
                        </div>
                    </div>
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">商户ID：</label>
                            <input type="text" name="partner_id" id="partner_id" class="form-control input-sm" value="${partner_id}">
                        </div>
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">数据：</label>
                            <select class="form-control input-sm" name="tbname" id="tbname">
                                <option value="" >实时数据</option>
                                <option value="history" <#if tbname?? && tbname == "history">selected</#if>>历史数据</option>
                            </select>
                        </div>
                    </div>
                    <input type="hidden" name="type" id="type" value="" />
                    <input type="hidden" name="pageIndex" id="pageIndex" value="${page.getPageIndex()}" />
                    <button type="submit" class="btn btn-default">搜索</button>
                </form>
            </div>
        </div>
        <!-- search div end -->


        <!-- list-div start-->
        <div class="row list-toolbar">
            <div class="col-sm-6">
                <button id="searchHandle." type="button" onclick="toggleSearchDiv();" class="btn btn-primary btn-sm">搜索列表</button>
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
                    <th>商户ID</th>
                    <th>帐户名称</th>
                    <th>余额</th>
                    <th>时间</th>
                </tr>
                </thead>
                <tbody title="12345">
                    <#if lists?? && lists?size&gt;0 >
                        <#list page.elements as list>
                            <tr id="userid-${list.BID}" overstyle="on">
                                <td>${list.PARTNER_ID}</td>
                                <td > ${list.PARTNER_NAME} </td>
                                <td><span class="red">￥${list.ACCOUNT_AMOUNT?string("#,##0.00")}</span></td>
                                <td>${list.TRADE_TIME?string('yyyy-MM-dd HH:mm:SS')}</td>
                            </tr>
                        </#list>
                    <#else>
                        <tr class="no-data">
                            <td colspan="4"><em>很抱歉,未查询到任何数据信息!</em></td>
                        </tr>
                    </#if>
                </tbody>
            </table>
<#if inajax=="0">
        </div>
        <!-- list-div end -->
    </section>
	<!-- list-toolbar end-->

    <#include "../footer.ftl">
</#if>
<script type="text/javascript">


	function creTime(type){
		var frm = document.pagefrm;
		frm.type.value = type;
		frm.submit();
	}

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
</script>

</#escape>