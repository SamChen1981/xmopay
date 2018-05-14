<#escape x as (x)!>
<#if inajax=="0">
        <#include "../header.ftl">
        <!-- header start -->
        <section class="content-header">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li><a href="partners/index?partner_type=${partner_type}">返回</a></li>
                    <li class="active"><a href="/partners/productList?partner_id=<#if partner_id??>${partner_id}<#else>0</#if>">【${partner_id}-${partner_name}】产品列表</a></li>
                    <li><a href="/partners/openProduct?partner_id=<#if partner_id??>${partner_id}<#else>0</#if>">商户开通产品</a></li>
                </ul>
            </div>
        </section>
        <!-- header end -->
    <section id="listDiv" class="content-body">
</#if>

    <!-- list-div start-->
    <div class="row list-toolbar">
        <#--<div class="col-sm-6">-->
            <#--<button id="searchHandle" type="button" class="btn btn-primary btn-sm">搜索产品</button>-->
        <#--</div>-->
        <div class="col-sm-12">
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
                <th style="width:30px;"><input type="checkbox" name="checkb" id="checkbox_handle"><label for="checkbox"></label></th>
                <th>支付类型</th>
                <th>渠道名称</th>
                <th>支付产品</th>
                <th>结算费率</th>
                <th>更新时间</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <#if lists?? && lists?size&gt;0>
                <#list lists as list>
                    <tr>
                        <td>
                            <input type="checkbox" name="checkb" value="${list.ppid?c}" class="checkbox" >
                        </td>
                        <td>
                            <#if list.productType == "BANK_PAY"><span class="label label-primary">网银
                            <#elseif list.productType == "QUICK_PAY"><span class="label label-success">快捷
                            <#elseif list.productType == "SCAN_PAY"><span class="label label-warning">扫码
                            <#else><span class="label label-danger">其他
                            </#if></span>
                        </td>
                        <td title="${list.channelCode}">${list.channelName}</td>
                        <td title="${list.bankCode}">${list.bankName}-${list.bankCode}</td>
                        <td><span class="text-danger">${list.rate}</span></td>
                        <td>${list.updateTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                        <td><#if list.status==0><span class="text-danger">关闭</span> <#elseif list.status==1><span class="text-success">开通</span> </#if></td>
                        <td><a href="partners/openProduct?partner_id=${list.partnerId}&ppid=${list.ppid?c}">修改</a></td>
                    </tr>
                </#list>
            <#else>
            <tr class="no-data">
                <td colspan="8"><em>很抱歉,未查询到任何数据信息!</em></td>
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
     $(document).ready(function() {
        $("#editProduct").click(function () {
            var pid = $(this).attr("data-id");
            $.dialog({
                title: '修改产品信息',
                content: 'url:product/edit?pid='+pid,
                columnClass:'col-md-8 col-md-offset-2',
                type: 'blue',
                typeAnimated: true
            });
        });
    });
</script>
</#escape>