<#escape x as (x)!>
    <#include "../header.ftl">
<!-- header start -->
<!--suppress ALL -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li><a href="/partners/productList?partner_id=<#if partner_id??>${partner_id}<#else>0</#if>">【${partner.partnerId}-${partner.partnerName}】商户产品列表</a></li>
            <li class="active"><a href="/partners/openProduct?partner_id=<#if partner_id??>${partner_id}<#else>0</#if>">商户<#if tab_title??>${tab_title}</#if>产品</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section class="content-body">
    <div class="row form">
        <div class="col-sm-12">
            <form id="productEditForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="partners/doEditPartnerProduct">
                <input type="hidden" name="ppid" id="ppid" value="<#if ppid??>${ppid?c}</#if>">
                <div class="form-group">
                    <label class="col-sm-2 control-label">商户合作ID：</label>
                    <div class="col-sm-8 form-control-static">
                        <#if partner??>
                            <#if partner.partnerName??>${partner.partnerName}</#if>
                            <#if partner.partnerId??>[${partner.partnerId}]</#if>
                            <input type="hidden" name="partner_id" id="partner_id" value="${partner.partnerId}">
                            <input type="hidden" name="partner_name" id="partner_name" value="${partner.partnerName}">
                        </#if>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">支付类型：</label>
                    <div class="col-sm-8">
                        <select class="form-control" name="product_type" id="product_type"
                                data-fv-row=".col-sm-8"
                                data-fv-notempty="true"
                                required
                                data-fv-notempty-message="必须选择支付类型">
                            <option value="">---选择支付类型---</option>
                            <option value="BANK_PAY" <#if partnerProduct??&&partnerProduct.productType=="BANK_PAY">selected</#if>>网银</option>
                            <option value="SCAN_PAY" <#if partnerProduct??&&partnerProduct.productType=="SCAN_PAY">selected</#if>>扫码</option>
                            <option value="QUICK_PAY" <#if partnerProduct??&&partnerProduct.productType=="QUICK_PAY">selected</#if>>快捷</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">渠道名称：</label>
                    <div class="col-sm-8">
                        <select class="form-control" name="channel_code" id="channel_code" required data-fv-notempty-message="请选择选择渠道" data-fv-row=".col-sm-8">
                            <#if gatewayChannels??&&gatewayChannels?size&gt;0>
                                <option value="" data-id="">---选择渠道---</option>
                                <#list gatewayChannels as list>
                                    <option value="${list.channelCode}|${list.channelName}" data-id='${list.channelId}' <#if partnerProduct??&&partnerProduct.channelCode==list.channelCode>selected</#if>>${list.channelName}</option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">结算费率：</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" name="rate" id="rate"
                               placeholder="结算费率"
                               required
                               data-fv-notempty="true"
                               data-fv-notempty-message="请填写最结算费率"
                               value="<#if partnerProduct??&&partnerProduct.rate??>${partnerProduct.rate?string("#.####")}</#if>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">支付产品：</label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="bank_name" id="bank_name"
                               placeholder="产品名称"
                               required
                               data-fv-notempty="true"
                               data-fv-notempty-message="银行名称"
                               value="<#if partnerProduct??&&partnerProduct.bankName??>${partnerProduct.bankName}</#if>">
                    </div>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="bank_code" id="bank_code"
                               placeholder="产品编码"
                               required
                               data-fv-notempty="true"
                               data-fv-notempty-message="请填写银行编码"
                               value="<#if partnerProduct??&&partnerProduct.bankCode??>${partnerProduct.bankCode}</#if>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">状态：</label>
                    <div class="col-sm-8">
                        <label class="radio-inline">
                            <input type="radio" name="status" id="status1" value="1" <#if partnerProduct??&&partnerProduct.status??&&partnerProduct.status==1>checked<#else>checked</#if>>开通
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="status" id="status0" value="0" <#if partnerProduct??&&partnerProduct.status??&&partnerProduct.status==0>checked</#if>>关闭
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
    $(document).ready(function () {
        $('#productEditForm').formValidation().on('success.form.fv', function (e) {
            // Prevent form submission
            e.preventDefault();

            // Some instances you can use are
            var $form = $(e.target),        // The form instance
                    fv = $(e.target).data('formValidation'); // FormValidation instance

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
                            ok: function () {
                                if (data.resultCode == "RESP_SUCCESS") {
                                    window.location.href = "${base}partners/productList?partner_id=" + $("#partner_id").val();
                                }
                            }
                        }
                    });
                },
                error: function (data) {
                    $.alert('系统异常', '系统提示');
                }
            });
        });
    });
</script>
</#escape>