<#escape x as (x)!>
    <#if inajax=="0">
        <#include "../header.ftl">
        <!-- header start -->
        <section class="content-header">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li class="active"><a href="product/index">产品列表</a></li>
                    <li class=""><a href="product/edit/1"><#if tab_title??>${tab_title}<#else>添加</#if>产品</a></li>
                </ul>
            </div>
        </section>
        <!-- header end -->
    <section id="listDiv" class="content-body">
        <!-- searchDiv start -->
        <div id="hideSearchDiv" class="row searchDiv" style="display:none">
            <div class="col-sm-12">
                <div class="page-header">
                    <h1>搜索产品</h1>
                </div>
                <form class="form-horizontal" method="post" action="product/index">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">产品编号：</label>
                            <select class="form-control" name="product_code" id="product_code" >
                                <option value="">---请选择产品服务接口名称---</option>
                                <option value="PTY_ONLINE_PAY_BANK_PAY" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_ONLINE_PAY_BANK_PAY">selected</#if>>单笔网银支付接口</option>
                                <option value="PTY_ONLINE_PAY_QUICK_PAY" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_ONLINE_PAY_QUICK_PAY">selected</#if>>单笔快捷支付接口</option>
                                <option value="PTY_ONLINE_PAY_WXPAY" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_ONLINE_PAY_WXPAY">selected</#if>>单笔微信支付接口</option>
                                <option value="PTY_ONLINE_PAY_QQPAY" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_ONLINE_PAY_QQPAY">selected</#if>>单笔QQ钱包支付接口</option>
                                <option value="PTY_ONLINE_PAY_ALIPAY" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_ONLINE_PAY_ALIPAY">selected</#if>>单笔支付宝支付接口</option>
                                <option value="PTY_ONLINE_RECHARGE_BANK_PAY" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_ONLINE_RECHARGE_BANK_PAY">selected</#if>>单笔网银在线充值</option>
                                <option value="PTY_TRADE_NOTIFY" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_TRADE_NOTIFY">selected</#if>>交易通知接口</option>
                                <option value="PTY_TRADE_QUERY" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_TRADE_QUERY">selected</#if>>交易查询接口</option>
                                <option value="PTY_TRADE_REFUND" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_TRADE_REFUND">selected</#if>>交易退款接口</option>
                                <option value="PTY_TRANSFER_PAY_CARD" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_TRANSFER_PAY_CARD">selected</#if>>单笔代付接口</option>
                                <option value="PTY_TRANSFER_PAY_NOTIFY" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_TRANSFER_PAY_NOTIFY">selected</#if>>代付通知接口</option>
                                <option value="PTY_TRANSFER_PAY_QUERY" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_TRANSFER_PAY_QUERY">selected</#if>>代付查询接口</option>
                                <option value="PTY_ACCOUNT_BALANCE" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_ACCOUNT_BALANCE">selected</#if>>账户余额查询接口</option>
                                <option value="PTY_TRANS_TO_ACCOUNT" <#if gatewayProduct??&&gatewayProduct.productCode??&&gatewayProduct.productCode=="PTY_TRANS_TO_ACCOUNT">selected</#if>>转账到户</option>
                            </select>
                        </div>
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">产品名称：</label>
                            <input type="text" name="product_name" id="product_name" class="form-control input-sm" value="${gatewayProduct.productName}">
                        </div>
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">开通状态：</label>
                            <select class="form-control" name="product_status" id="product_status" >
                                <option value="">---开通状态---</option>
                                <option value="1" <#if gatewayProduct??&&gatewayProduct.productSatus??&&gatewayProduct.productSatus==1>selected</#if>>开通</option>
                                <option value="0" <#if gatewayProduct??&&gatewayProduct.productSatus??&&gatewayProduct.productSatus==0>selected</#if>>关闭</option>
                            </select>
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
            <button id="searchHandle" type="button" class="btn btn-primary btn-sm">搜索产品</button>
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
                <th>产品代码</th>
                <th>产品名称</th>
                <th>费率方案</th>
                <th>渠道约束</th>
                <th>开通状态</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <#if lists?? && lists?size&gt;0>
                <#list lists as list>
                    <tr>
                        <td>${list.productCode}</td>
                        <td>${list.productName}</td>
                        <td><#if list.rateSchemaName??>${list.rateSchemaName}</#if></td>
                        <td>${list.restrictId}</td>
                        <td><#if list.productStatus==0><span class="label label-danger">关闭</span><#elseif list.productStatus==1><span class="label label-success">开通</span></#if></td>
                        <td><a href="product/edit/2?pid=${list.gproductId?c}">修改</a></td>
                    </tr>
                </#list>
            <#else>
            <tr class="no-data">
                <td colspan="7"><em>很抱歉,未查询到任何数据信息!</em></td>
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