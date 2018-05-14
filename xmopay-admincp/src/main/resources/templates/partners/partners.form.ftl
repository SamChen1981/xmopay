<#escape x as (x)!>
<#include "../header.ftl">
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li><a href="partners/index?partner_type=${partner_type}">返回</a></li>
            <li class="active"><a href="partners/toEdit?ptid=${puserMap.ptid}">编辑商户</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section class="content-body">
    <div class="row form">
        <div class="col-sm-6">
            <form id="partnersForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="partners/doEditPartners">
                <div class="form-group">
                    <label class="col-sm-3 control-label">商户合作ID</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" name="partner_id" value="<#if puserMap??>${puserMap.partnerId}</#if>" readonly>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">商户名称</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control" name="partner_name" value="<#if puserMap??>${puserMap.partnerName}</#if>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 control-label">交易IP白名单</label>
                    <div class="col-sm-8">
                        <textarea name="api_white_ip" id="api_white_ip" class="form-control" rows="3"><#if puserMap??>${puserMap.apiWhiteIp}</#if></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">平台名称</label>
                    <div class="col-sm-9">
                        <input name="app_title" type="text" class="form-control" value="<#if partnerInfo??>${partnerInfo.app_title}</#if>" id="app_title" placeholder="平台名称"
                               data-fv-row=".col-sm-9"
                               data-fv-notempty="true"
                               required
                               data-fv-notempty-message="必须填写平台名称">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">平台域名</label>
                    <div class="col-sm-9">
                        <input name="app_domain" type="text" class="form-control" value="<#if partnerInfo??>${partnerInfo.app_domain}</#if>" id="app_domain" placeholder="平台域名"
                               data-fv-row=".col-sm-9"
                               data-fv-notempty="true"
                               required
                               data-fv-notempty-message="必须填写平台域名">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">网关地址</label>
                    <div class="col-sm-9">
                        <input name="app_gateway_url" type="text" class="form-control" value="<#if partnerInfo??>${partnerInfo.app_gateway_url}</#if>" id="app_gateway_url" placeholder="网关地址"
                               data-fv-row=".col-sm-9"
                               data-fv-notempty="true"
                               required
                               data-fv-notempty-message="必须填写网关地址">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">API权限状态</label>
                    <div class="col-sm-9 form-inline">
                        <select class="form-control" name="apiStatus" id="apiStatus">
                            <option value="1" <#if puserMap.apiStatus == 1>selected</#if>>已开通</option>
                            <option value="0" <#if puserMap.apiStatus == 0>selected</#if>>待开通</option>
                            <option value="-1" <#if puserMap.apiStatus == -1>selected</#if>>关闭</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">商户状态</label>
                    <div class="col-sm-9 form-inline">
                        <select class="form-control" name="status" id="status">
                            <option value="1" <#if puserMap.status == 1>selected</#if>>已开通</option>
                            <option value="0" <#if puserMap.status == 0>selected</#if>>待开通</option>
                            <option value="-1" <#if puserMap.status == -1>selected</#if>>关闭</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-12">
                        <input type="hidden" name="ptid" value="<#if puserMap??>${puserMap.ptid}</#if>">
                        <button type="submit" id="blackSubmit" class="btn btn-primary">确定提交</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</section>

<#include "../footer.ftl">
<script>
    $(document).ready(function() {
        $('#partnersForm')
                .formValidation(
                    {
//                        framework: 'bootstrap',
                        fields: {
                            operate: {
                                row: '.col-sm-9',
                                validators: {
                                    notEmpty: {
                                        message: '请输入上级类型'
                                    }
                                }
                            },
                            agency: {
                                row: '.col-sm-9',
                                validators: {
                                    notEmpty: {
                                        message: '请输入上级类型'
                                    }
                                }
                            },
                            partner_name: {
                                row: '.col-sm-9',
                                validators: {
                                    notEmpty: {
                                        message: '请输入商户名称'
                                    }
                                }
                            }
                        }
                    }
                )
                .on('success.form.fv', function(e) {
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
                        dataType:"json",
                        beforeSend: function () {
                            // 禁用按钮防止重复提交
                            $("#blackSubmit").attr({ disabled: "disabled" });
                        },
                        success: function(data) {
                            $("#blackSubmit").removeAttr("disabled");
                            if(data==null || data==""){
                                $.alert('系统错误！', '系统提示');
                            }
                            if(data.resultCode=="RESP_SUCCESS"){
                                $.confirm({
                                    title: '系统提示',
                                    content: "<p class='text-success'>操作成功！</p>",
                                    buttons: {
                                        ok: function () {
                                            setTimeout("window.location.href = '${base}partners/index'", '500');
                                        }
                                    }
                                });
                            }else{
                                $.alert(data.resultMsg, '系统提示');
                            }
                        },
                        error : function(data) {
                            $.alert('系统异常', '系统提示');
                        }
                    });
                });
    });

</script>
</#escape>