<#escape x as (x)!>
    <#if inajax=="0">
<#include "../header.ftl">
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li><a href="channel/index">渠道列表</a></li>
            <li><a href="channel/edit">添加渠道</a></li>
            <li class="active"><a href="channel/agencyList">机构列表</a></li>
            <li><a href="channel/editAgency?gaid=0">添加机构</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

    <section id="listDiv" class="content-body">
        <!-- searchDiv start -->
        <div id="hideSearchDiv" class="row searchDiv" style="display:none">
            <div class="col-sm-12">
                <div class="page-header">
                    <h1>搜索机构</h1>
                </div>
                <form class="form-horizontal" method="post" action="channel/agencyList">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">机构名称：</label>
                            <input type="text" name="agencyName" id="agency_name" class="form-control input-sm" value="">
                        </div>
                    </div>
                    <button type="submit" class="btn btn-default">搜索</button>
                </form>
            </div>
        </div>
        <!-- search div end -->
    </#if>

    <!-- list-div start-->
    <div class="row list-toolbar">
        <div class="col-sm-6">
            <button id="searchHandle" type="button" class="btn btn-primary btn-sm">搜索机构</button>
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
                <th>机构名称</th>
                <th>机构代码（类名）</th>
                <th>其他参数</th>
                <th>状态</th>
                <th>更新时间</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
                <#if lists?? && lists?size&gt;0>
                    <#list lists as list>
                        <tr>
                            <td>${list.agencyName}</td>
                            <td>${list.agencyCode}</td>
                            <td><button type="button" class="btn btn-info btn-xs" data-agency-params='${list.agencyParams}' onclick='showExtendsParams(this)'> 详情 </button></td>
                            <td><#if list.agencyStatus==0><span class="label label-danger">关闭</span><#elseif list.agencyStatus==1><span class="label label-success">开启</span></#if></td>
                            <td><#if list.updateTime??>${list.updateTime?string('yyyy-MM-dd HH:mm:ss')}<#else>${list.dateline?string("yyyy-MM-dd HH:mm:ss")}</#if></td>
                            <td><a data-id="1" href="channel/editAgency?gaid=${list.gaid?c}">修改</a></td>
                        </tr>
                    </#list>
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
<#include "../footer.ftl">
    </#if>
<script type="text/javascript">
    function showExtendsParams(obj) {
        var ctr = "";
        var params = $(obj).attr("data-agency-params");
        if (params != null && params != "") {
            ctr = "<html><body><div style='height: 260px; overflow-y: auto;overflow-x: hidden;'><pre style='white-space:pre-wrap;tab-size:4;'>" + JSON.stringify(JSON.parse(params), null, 4) + "</pre></div></body></html>";
        } else {
            ctr = "暂无设置其他参数";
        }
        $.dialog({
            title: '其他参数详情信息',
            content: ctr,
            columnClass:'col-md-8 col-md-offset-2',
            type: 'blue',
            typeAnimated: true
        });
    }
</script>
</#escape>