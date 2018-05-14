<#escape x as (x)!>
    <#if inajax=="0">
	<#include "../header.ftl">

    <!-- header start -->
    <section class="content-header">
        <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
                <li class="active"><a href="channel/balanceList">渠道余额</a></li>
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
                <form action="/channel/balanceList" name="pagefrm" id="pagefrm" method="post" class="form-horizontal">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">批次号：</label>
                            <input type="text" name="batch_id" id="batch_id" class="form-control input-sm" value="${batch_id}">
                        </div>
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">网关code：</label>
                            <input type="text" name="gateway_code" id="gateway_code" value="${gateway_code}" class="form-control input-sm" >
                        </div>
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">网关名字：</label>
                            <input type="text" name="gateway_name" id="gateway_name" value="${gateway_name}" class="form-control input-sm" >
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
                <button id="taskBalanceHandle" data-loading-text="Data Loading..." type="button" class="btn btn-primary btn-sm">一键同步渠道余额</button>
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
                    <th>批次号</th>
                    <th>网关ID</th>
                    <th>网关名称</th>
                    <th>虚拟账号</th>
                    <th>账户余额</th>
                    <th>最后交易时间</th>
                    <th>查询时间</th>
                </tr>
                </thead>
                <tbody>
                    <#if lists?? && lists?size&gt;0>
                        <#list lists as list>
                        <tr>
                            <td>${list.batchId}</td>
                            <td>${list.gatewayCode}</td>
                            <td>${list.gatewayName}</td>
                            <td>${list.merViralAcct}</td>
                            <td><span class="text-danger">￥${list.balance}</span></td>
                            <td>${list.lastTradeTime?string("yyyy-MM-dd HH:mm:ss")}</td>
                            <td>${list.dateline?string("yyyy-MM-dd HH:mm:ss")}</td>
                        </tr>
                        </#list>
                    <#else>
                    <tr class="no-data">
                        <td colspan="7"><em>很抱歉,未查询到任何数据信息!</em></td>
                    </tr>
                    </#if>
                    <#if balance_total??>
                    <tr style="background:#C1FFC1">
                        <td colspan="7">
                            余额归总：
                        ￥<strong class="text-danger">${balance_total?string("#,##0.00")}</strong>
                        </td>
                    </tr>
                    </#if>
                </tbody>
            </table>
        </div>
    <#if inajax=="0">
        <!-- list-div end -->
    </section>
	<!-- list-toolbar end-->

<#include "../footer.ftl">
    </#if>
<script type="text/javascript">
    $("#taskBalanceHandle").click(function () {
        var $btn = $("#taskBalanceHandle").button('loading');
        $.ajax({
            url: "channel/synchBalance",
            type: 'POST',
            data: null,
            dataType:"json",
            success: function(data) {
                $btn.button('reset'); //重置按钮状态
                if(data==null || data==""){
                    $.alert('系统错误！', '系统提示');
                }
                if(data.resultCode=="RESP_SUCCESS"){
                    $.confirm({
                        title: '系统提示',
                        content: "<p class='text-success'>操作成功！</p>",
                        buttons: {
                            ok: function () {
                                window.location.reload();
                            }
                        }
                    });
                }else{
                    $.alert(data.resultMsg, '系统提示');
                }
            },
            error : function(data) {
                $btn.button('reset'); //重置按钮状态
                $.alert('系统异常', '系统提示');
            }
        });
    });
    function exportChannelBalance() {
            var frm = document.pagefrm;
            frm.action="channel/exportChannelBalance";
            frm.submit();
            frm.action="channel/balanceList";
    }
</script>

</#escape>