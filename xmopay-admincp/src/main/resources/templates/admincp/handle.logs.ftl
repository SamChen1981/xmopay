<#escape x as (x)!>
    <#if inajax=="0">
        <#include "../header.ftl">

<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li class="active"><a href="hlogs/index">日志列表</a></li>

        </ul>
    </div>
</section>
<!-- header end -->

<section id="listDiv" class="content-body">
    </#if>


    <div id="hideSearchDiv" class="row" style="display:none">
        <div class="col-sm-12 searchDiv">
            <div class="page-header">
                <h1>搜索日志</h1>
            </div>
            <form class="form-horizontal" method="post" action="hlogs/index">
                <div class="form-group form-inline">
                    <div class="col-xs-6 col-md-4">
                        <label class="control-label">操作员ID：</label>
                        <input type="text" name="puserid" id="puserid"  class="form-control input-sm" value="${puserid}">&nbsp;&nbsp;&nbsp;&nbsp;
                    </div>
                    <div class="col-xs-6 col-md-4">
                        <label class="control-label">商户ID：</label>
                        <input type="text"  class="form-control input-sm" name="partner_id" id="partner_id" value="${partner_id}">&nbsp;&nbsp;&nbsp;&nbsp;
                    </div>
                    <div class="col-xs-6 col-md-4">
                        <label class="control-label">订单号：</label>
                        <input type="text"  class="form-control input-sm" name="order_sn" id="order_sn" value="${order_sn}">&nbsp;&nbsp;&nbsp;&nbsp;
                    </div>
                </div>
                <div class="form-group form-inline">
                    <div class="col-xs-4 col-md-4">
                        <label class="control-label">类型：</label>
                        <select class="form-control input-sm"  name="handle_type" id="handle_type">
                            <option value="">==类型==</option>
                            <option value="TYPE_PARTNER_HANDLE"  <#if handle_type?? && handle_type == "TYPE_PARTNER_HANDLE">selected</#if>>商户系统</option>
                            <option value="TYPE_SYSADMIN_HANDLE" <#if handle_type?? && handle_type == "TYPE_SYSADMIN_HANDLE">selected</#if>>后台系统</option>
                            <option value="TYPE_GATEWAY_HANDLE"  <#if handle_type?? && handle_type == "TYPE_GATEWAY_HANDLE">selected</#if>>网关系统</option>
                            <option value="TYPE_TASK_HANDLE"     <#if handle_type?? && handle_type == "TYPE_TASK_HANDLE">selected</#if>>计划任务</option>
                        </select>
                    </div>
                    <div class="col-xs-4 col-md-4">
                        <label class="control-label">事件描述：</label>
                        <input type="text" class="form-control input-sm"  name="handle_params" id="handle_params" value="${handle_params}">&nbsp;&nbsp;&nbsp;&nbsp;
                    </div>
                    <div class="col-xs-4 col-md-4">
                        <label class="control-label">编码：</label>
                        <select class="form-control input-sm"  name="handle_code" id="handle_code">
                            <option value="">==编码==</option>
                            <option value="PARTNER_LOGIN"  <#if handle_code?? && handle_code == "PARTNER_LOGIN">selected</#if>>商户登陆</option>
                            <option value="PARTNER_PAYOUT" <#if handle_code?? && handle_code == "PARTNER_PAYOUT">selected</#if>>商户提现</option>
                            <option value="PARTNER_PAYIN"  <#if handle_code?? && handle_code == "PARTNER_PAYIN">selected</#if>>商户充值</option>
                            <option value="PARTNER_SAFETY"     <#if handle_code?? && handle_code == "PARTNER_SAFETY">selected</#if>>商户提现安全设置</option>
                            <option value="SYSADMIN_SAFETY"     <#if handle_code?? && handle_code == "SYSADMIN_SAFETY">selected</#if>>后台设置商户安全</option>
                            <option value="SYSADMIN_LOG"     <#if handle_code?? && handle_code == "SYSADMIN_LOG">selected</#if>>后台设置日志</option>
                            <option value="SYSADMIN_LOGIN"     <#if handle_code?? && handle_code == "SYSADMIN_LOGIN">selected</#if>>后台登陆日志</option>
                        </select>

                    </div>
                </div>
                <button type="submit" class="btn btn-default">搜索</button>
            </form>
        </div>
    </div>

    <!-- list-div start-->
    <div class="row list-toolbar">
        <div class="col-sm-6">
            <button id="searchHandle." onclick="toggleSearchDiv();" type="button" class="btn btn-primary btn-sm">搜索日志</button>
        </div>
        <div class="col-sm-6">
            <nav class="pages pull-right">
                <ul id="testpage" class="pagination pagination-sm">${pages}</ul>
            </nav>
        </div>
    </div>

    <!-- list-div start-->
    <div class="row">
        <table class="table table-hover" >
            <thead>
            <tr>
                <th style="width:10%;align-content: center">类型</th>
                <th style="width:10%; align-content: center">编码</th>
                <th style="width:6%;align-content: center">操作员</th>
                <th style="width:9%;align-content: center">IP</th>
                <th style="width:15%;align-content: center">记录时间</th>
                <th style="width:45%;align-content: center">事件描述</th>
            </tr>
            </thead>

            <tbody>
				<#if lists?? && lists?size&gt;0>
					<#list lists as list>
                    <tr id="logs-${list.hlid}" overstyle="on">
                        <td >
							<#if list.handleType=="TYPE_PARTNER_HANDLE">商户系统
                            <#elseif list.handleType=="TYPE_SYSADMIN_HANDLE">后台系统
                            <#elseif list.handleType=="TYPE_GATEWAY_HANDLE">网关系统
                            <#elseif list.handleType=="TYPE_TASK_HANDLE">计划任务
                            </#if>
                        </td>
                        <td>
                            <#if list.handleCode=="PARTNER_LOGIN">商户登陆
                            <#elseif list.handleCode=="PARTNER_PAYOUT">商户提现
                            <#elseif list.handleCode=="PARTNER_PAYIN">商户充值
                            <#elseif list.handleCode=="PARTNER_SAFETY">商户提现安全设置
                            <#elseif list.handleCode=="SYSADMIN_SAFETY">后台设置商户安全
                            <#elseif list.handleCode=="SYSADMIN_LOG">后台设置日志
                            <#elseif list.handleCode=="SYSADMIN_LOGIN">后台登陆日志
                            <#else>其他操作
                            </#if>
                        </td>
                        <td title="${list.partnerId}"><span class="green"> ${list.puserid} </span></td>
                        <td>${list.handleIp}</td>
                        <td><span>${list.dateline?string('yyyy-MM-dd HH:mm:ss')}</span></td>
                        <td title="${list.handleParams}">
							<#if list.handleParams?? && list.handleParams?length lt 50>
                                ${list.handleParams}
                            <#else>
                                ${list.handleParams[0..50]}...
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
    <script>

    </script>

</#escape>