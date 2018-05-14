<#escape x as (x)!>
    <#include "../header.ftl">
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li><a href="product/index">产品列表</a></li>
            <li class="active"><a href="product/edit/1"><#if tab_title??>${tab_title}<#else>添加</#if>产品</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section class="content-body">
    <div class="row form">
        <div class="col-sm-12">
            <form id="productEditForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="product/${action}">
                <input type="hidden" name="pid" id="pid" value="<#if pid??>${pid}</#if>">
                <input type="hidden" id="authTradeType" value='<#if authTradeType??>${authTradeType}</#if>'>
                <input type="hidden" id="tmpProductCode" value="<#if gatewayProduct??&&gatewayProduct.productCode??>${gatewayProduct.productCode}</#if>">
                <div class="form-group">
                    <label class="col-sm-2 control-label">产品编码：</label>
                    <div class="col-sm-8">
                        <select class="form-control" name="productCode" id="product_code" onchange="selectProduct(this.value)"
                                required
                                data-fv-notempty-message="请选择产品服务接口名称" <#if gatewayProduct??>disabled</#if>>
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
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">产品名称：</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" name="productName" id="product_name" placeholder="产品名称"
                               data-fv-row=".col-sm-8"
                               data-fv-notempty="true"
                               required
                               data-fv-notempty-message="必须填写产品名称"
                               value="<#if gatewayProduct??&&gatewayProduct.productName??>${gatewayProduct.productName}</#if>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">约束：</label>
                    <div class="col-sm-8">
                        <select class="form-control" name="restrict" id="restrict"
                                data-fv-row=".col-sm-8"
                                data-fv-notempty="true"
                                required
                                data-fv-notempty-message="必须选择约束">
                            <option value="">请选择约束方案</option>
                            <#if restricts??&&restricts?size&gt;0>
                                <#list restricts as list>
                                    <option value="${list.restrictId?c}_${list.restrictName}" <#if gatewayProduct??&&gatewayProduct.restrictId??&&gatewayProduct.restrictId==list.restrictId>selected</#if>>${list.restrictName}</option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">费率方案：</label>
                    <div class="col-sm-8">
                        <select class="form-control" name="rateSchema" id="rateSchema"
                                data-fv-row=".col-sm-8"
                                data-fv-notempty="true"
                                required
                                data-fv-notempty-message="必须选择费率方案">
                            <option value="">请选择费率方案</option>
                            <#if rateSchemes??&&rateSchemes?size&gt;0>
                                <#list rateSchemes as list>
                                    <option value="${list.rateSchemeId?c}_${list.schemeName}" <#if gatewayProduct??&&gatewayProduct.rateSchemaId??&&gatewayProduct.rateSchemaId==list.rateSchemeId>selected</#if>>${list.schemeName}</option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">授权交易类型：</label>
                    <div class="col-sm-8 form-control-static">
                        <ul style="overflow:hidden; width:100%;" id="ul-service-product">
                        </ul>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">状态：</label>
                    <div class="col-sm-8">
                        <label class="radio-inline">
                            <input type="radio" name="productStatus" id="product_status" value="1" <#if gatewayProduct??&&gatewayProduct.productStatus??&&gatewayProduct.productStatus==1>checked</#if>>开通
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="productStatus" id="product_status" value="0" <#if gatewayProduct??&&gatewayProduct.productStatus??&&gatewayProduct.productStatus==0>checked</#if>>关闭
                        </label>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-8">
                        <button type="submit" id="btnSubmit" class="btn btn-primary"><#if tab_title??>${tab_title}</#if>产品</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</section>
    <#include "../footer.ftl">

<script type="text/javascript">
    $(document).ready(function() {
        $('#productEditForm').formValidation().on('success.form.fv', function(e) {
            // Prevent form submission
            e.preventDefault();

            // Some instances you can use are
            var $form = $(e.target),        // The form instance
                    fv    = $(e.target).data('formValidation'); // FormValidation instance

            // Use Ajax to submit form data
            $.ajax({
                url: $form.attr('action'),
                type: 'POST',
                data: $form.serialize(),
                dataType: "json",
                beforeSend: function () {
                    // 禁用按钮防止重复提交
                    $("#btnSubmit").attr({disabled: "disabled"});
                },
                success: function (data) {
                    if (data == null || data == "") {
                        $.alert('系统错误！', '系统提示');
                    }
                    $.confirm({
                        title: '系统提示!',
                        content: data.resultMsg,
                        buttons: {
                            confirm: function () {
                                if (data.resultCode == "RESP_SUCCESS") {
                                    window.location.href = "${base}product/index";
                                }
                            },
                            cancel: function () {
                            }
                        }
                    });
                },
                error: function (data) {
                    $("#btnSubmit").removeAttr("disabled");
                    $.alert('系统异常', '系统提示');
                }
            });
        });
    });

    (function(pid){
        if (pid > 0) {
            selectProduct($("#product_code").val());
        }
    }($("#pid").val()));

    function selectProduct(code) {
        if (isValue(code)) {
            return;
        }

        var pid = $("#pid").val();
        $.ajax({
            url: "${base}product/getServiceProduct",
            type: 'GET',
            data: {"service_code": code},
            dataType: "json",
            success: function(data) {
                if (data.resultCode == "RESP_SUCCESS") {
                    if (data.resultData != null && data.resultData.length > 0) {
                        var lists = data.resultData;
                        $("#ul-service-product").empty();
                        if (isValue(pid)) {
                            for (var i = 0; i < lists.length; i++) {
                                $("#ul-service-product").append('<li style="width:20%;float:left; padding: 6px 0px 0px 0px;"><input type="checkbox" name="serviceProduct" value="'+lists[i].productCode+'"> '+lists[i].productName+'</li>');
                            }
                        } else {
                            var authTradeType = $("#authTradeType").val();
                            var authJson = JSON.parse(authTradeType);
                            var tmpProductCode = $("#tmpProductCode").val();
                            var productCode = $("#product_code").val();
                            for (var i = 0; i < lists.length; i++) {
                                if (!isValue(authJson[lists[i].productCode]) && tmpProductCode === productCode) {
                                    $("#ul-service-product").append('<li style="width:20%;float:left; padding: 6px 0px 0px 0px;"><input type="checkbox" name="serviceProduct" value="'+lists[i].productCode+'" checked> '+lists[i].productName+'</li>');
                                } else {
                                    $("#ul-service-product").append('<li style="width:20%;float:left; padding: 6px 0px 0px 0px;"><input type="checkbox" name="serviceProduct" value="'+lists[i].productCode+'"> '+lists[i].productName+'</li>');
                                }
                            }
                        }
                    }
                }
            },
            error: function(data) {
            }
        });
    }
</script>
</#escape>