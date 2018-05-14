<#escape x as (x)!>
    <#if inajax=="0">
        <#include "../header.ftl">
    <!-- header start -->
    <section class="content-header">
        <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
                <li class="active"><a href="channel/index">渠道列表</a></li>
                <li><a href="channel/edit?channelId=0">添加渠道</a></li>
                <li><a href="channel/agencyList">机构列表</a></li>
                <li><a href="channel/editAgency">添加机构</a></li>
            </ul>
        </div>
    </section>
    <!-- header end -->
    <section id="listDiv" class="content-body">
        <!-- searchDiv start -->
        <div id="hideSearchDiv" class="row searchDiv" style="display:none">
            <div class="col-sm-12">
                <div class="page-header">
                    <h1>搜索渠道</h1>
                </div>
                <form class="form-horizontal" method="post" action="channel/index">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">渠道类型：</label>
                            <select class="form-control" name="channel_type" id="channel_type">
                                <option value="">---请选择渠道类型---</option>
                                <option value="0" <#if gatewayChannel??&&gatewayChannel.channelType??&&gatewayChannel.channelType==0>selected</#if>>支付网关</option>
                                <option value="1" <#if gatewayChannel??&&gatewayChannel.channelType??&&gatewayChannel.channelType==1>selected</#if>>下发网关</option>
                            </select>
                        </div>

                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">渠道名称：</label>
                            <input type="text" name="channel_name" id="channel_name" class="form-control input-sm" value="<#if gatewayChannel??&&gatewayChannel.channelName??>${gatewayChannel.channelName}</#if>">
                        </div>

                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">渠道代码：</label>
                            <input type="text" name="channel_code" id="channel_code" class="form-control input-sm" value="<#if gatewayChannel??&&gatewayChannel.channelCode??>${gatewayChannel.channelCode}</#if>">
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-12"><button type="submit" class="btn btn-default">搜索</button></div>
                    </div>
                </form>
            </div>
        </div>
        <!-- search div end -->
    </#if>
    <div class="row list-toolbar">
        <div class="col-sm-6">
            <button id="searchHandle" type="button" class="btn btn-primary btn-sm">搜索渠道</button>
        </div>
        <div class="col-sm-6">
            <nav class="pages pull-right">
                <ul id="testpage" class="pagination pagination-sm">${pages}</ul>
            </nav>
        </div>
    </div>

    <!-- list-div start-->
<div class="row">
    <table class="table table-hover">
        <thead>
        <tr>
            <th>渠道类型</th>
            <th>渠道名称</th>
            <th>渠道代码</th>
            <th>渠道余额</th>
            <th>更新时间</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>

        <tbody>
            <#if lists?? && lists?size&gt;0>
                <#list lists as list>
                <tr>
                    <td><#if list.channelType==0><span class="label label-success">支付网关</span><#elseif list.channelType==1><span class="label label-info">下发网关</span></#if></td>
                    <td>${list.channelName}</td>
                    <td>${list.channelCode}</td>
                    <td><#if list.channelBalance??&&list.channelBalance!="0"><strong class="text-success">${list.channelBalance}</strong><#else><strong class="text-danger">${list.channelBalance}</strong></#if></td>
                    <td><#if list.updateTime??>${list.updateTime?string('yyyy-MM-dd HH:mm:ss')}<#else>${list.dateline?string("yyyy-MM-dd HH:mm:ss")}</#if></td>
                    <td><#if list.status==0><span class="label label-danger">关闭</span><#elseif list.status==1><span class="label label-success">开启</span></#if></td>
                    <td>
                        <a data-id="1" href="channel/edit?channelId=${list.channelId?c}">修改</a>
                        <#if list.status==0> |
                            <a href="javascript:void(0);" data-id="${list.channelId?c}" onclick='deleteGatewayChannel("${list.channelId?c}", "${list.channelName}")'>删除</a>
                        </#if>
                    </td>
                </tr>
                </#list>
            <#else>
            <tr class="no-data">
                <td colspan="9"><em>很抱歉,未查询到任何数据信息!</em></td>
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
    $(document).ready(function () {
        $(".editRoute").click(function () {
            var rate_scheme_id = $(this).attr("data-id");
            $.dialog({
                title: '添加/编辑路由信息',
                content: 'url:route/edit',
                columnClass: 'col-md-8 col-md-offset-2',
                type: 'blue',
                typeAnimated: true
            });
        });
    })

    function deleteGatewayChannel(channelId, channelName) {
        $.confirm({
            title: "系统提示",
            content: '确定要删除 <span class="text-primary">'+channelName+'</span> 吗?',
            buttons: {
                ok: function() {
                    $.ajax({
                        url:'channel/deleteGatewayChannel',
                        type: 'POST',
                        data:{'channel_id': channelId},
                        dataType: 'JSON',
                        success: function(data) {
                            if (data.resultCode == "RESP_SUCCESS") {
                                window.location.href="${base}channel/index";
                            }
                        },
                        error: function(data) {
                        }
                    });
                },
                cancel: function () {
                }
            }
        });
    }
</script>
</#escape>