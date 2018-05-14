<#escape x as (x)!>
    <#include "../header.ftl">
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li><a href="channel/index">渠道列表</a></li>
            <li><a href="channel/edit">添加渠道</a></li>
            <li><a href="channel/agencyList">机构列表</a></li>
            <li class="active"><a href="channel/editAgency?gaid=0"><#if agency??>修改<#else>添加</#if>机构</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section class="content-body">
    <div class="row form">
        <div class="col-sm-12">
            <form id="productEditForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="channel/${action}">
                <input type="hidden" value="<#if agency??&&agency.gaid??>${agency.gaid}</#if>" name="gaid" id="gaid">
                <div class="form-group">
                    <label class="col-sm-2 control-label">机构名称：</label>
                    <div class="col-sm-6">
                        <input type="text" class="form-control" name="agencyName" id="agency_name" placeholder="机构名称" value="<#if agency??&&agency.agencyName??>${agency.agencyName}</#if>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">机构类型：</label>
                    <div class="col-sm-6 form-inline">
                        <select class="form-control" name="agency_type" id="agency_type">
                            <option value="ALLPAY">默认机构类型（多合一）</option>
                            <option value="QPAY" <#if agencyType??&&agencyType=="QPAY">selected</#if>>[QPAY]快捷支付</option>
                            <option value="QQPAY" <#if agencyType??&&agencyType=="QQPAY">selected</#if>>[QQPAY]QQ扫码支付</option>
                            <option value="WXPAY" <#if agencyType??&&agencyType=="WXPAY">selected</#if>>[WXPAY]微信扫码支付</option>
                            <option value="ALIPAY" <#if agencyType??&&agencyType=="ALIPAY">selected</#if>>[ALIPAY]支付宝扫码</option>
                            <option value="BANKPAY" <#if agencyType??&&agencyType=="BANKPAY">selected</#if>>[BANKPAY]网银支付</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">机构代码：</label>
                    <div class="col-sm-6">
                        <input type="text" class="form-control" name="agencyCode" id="agency_code" placeholder="机构代码" value="<#if agencyCode??>${agencyCode}</#if>">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">其他参数：</label>
                    <div class="col-sm-6" id="extends-params">
                        <#if agencyParamsList??&&agencyParamsList?size&gt;0>
                            <#list agencyParamsList?keys as k>
                                <div class="form-group">
                                    <div class="col-xs-4">
                                        <input id="ext-param-key" type="text" class="form-control" placeholder="key" value="${k}"/>
                                    </div>
                                    <div class="col-xs-4">
                                        <input id="ext-param-value" type="text" class="form-control" placeholder="value" value="${agencyParamsList[k]}"/>
                                    </div>
                                    <div class="col-xs-1">
                                        <#if k_index &gt; 0>
                                            <button type="button" class="btn btn-default addButton" onclick="removeExtendsParams(this)"><i class="fa fa-minus"></i></button>
                                            <#else>
                                                <button type="button" class="btn btn-default addButton" onclick="addExtendsParams()"><i class="fa fa-plus"></i></button>
                                        </#if>
                                    </div>
                                </div>
                            </#list>
                        <#else>
                            <div class="form-group">
                                <div class="col-xs-4">
                                    <input id="ext-param-key" type="text" class="form-control" name="key" placeholder="key"/>
                                </div>
                                <div class="col-xs-4">
                                    <input id="ext-param-value" type="text" class="form-control" name="value" placeholder="value"/>
                                </div>
                                <div class="col-xs-1">
                                    <button type="button" class="btn btn-default addButton" onclick="addExtendsParams()"><i class="fa fa-plus"></i></button>
                                </div>
                            </div>
                        </#if>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">机构状态：</label>
                    <div class="col-sm-6">
                        <label class="radio-inline">
                            <input type="radio" name="agencyStatus" id="agency_status" value="1" <#if agency??&&agency.agencyStatus??&&agency.agencyStatus==1>checked<#else>checked</#if>>开通
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="agencyStatus" id="agency_status" value="0" <#if agency??&&agency.agencyStatus??&&agency.agencyStatus==0>checked</#if>>关闭
                        </label>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-6">
                        <button type="submit" id="btnSubmit" class="btn btn-primary">保存</button>
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

            var gaid = $("#gaid").val();
            var agencyName = $("#agency_name").val();
            var agencyCode = $("#agency_code").val();
            var agencyType = $("#agency_type option:selected").val();
            var agencyStatus = $("input[name='agencyStatus']:checked").val();
            var agencyParams = resolveExtendsParams();

            // Use Ajax to submit form data
            $.ajax({
                url: $form.attr('action'),
                type: 'POST',
                data: {
                        "gaid": gaid,
                        "agencyName": agencyName,
                        "agencyType": agencyType,
                        "agencyCode": agencyCode,
                        "agencyParams": agencyParams,
                        "agencyStatus": agencyStatus
                }, // $form.serialize()
                dataType: "json",
                beforeSend: function () {
                    // 禁用按钮防止重复提交
                    $("#btnSubmit").attr({disabled: "disabled"});
                },
                success: function (data) {
                    $("#btnSubmit").removeAttr("disabled");
                    if (data == null || data == "") {
                        $.alert('系统错误！', '系统提示');
                    }
                    $.confirm({
                        title: '系统提示!',
                        content: data.resultMsg,
                        buttons: {
                            confirm: function () {
                                if (data.resultCode == "RESP_SUCCESS") {
                                    window.location.href = "${base}channel/agencyList";
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
    
    function addExtendsParams() {
        var html = '<div class="form-group"><div class="col-xs-4"><input id="ext-param-key" type="text" class="form-control" placeholder="key" value=""/></div><div class="col-xs-4"><input id="ext-param-value" type="text" class="form-control" placeholder="value" value=""/></div><div class="col-xs-1"><button type="button" class="btn btn-default addButton" onclick="removeExtendsParams(this)"><i class="fa fa-minus"></i></button></div></div>';
        $("#extends-params").append(html);
    }

    function removeExtendsParams(obj) {
        $(obj).parent().parent('div').remove();
    }

    function resolveExtendsParams() {
        var ctr = "";
        var keys = $("input#ext-param-key");
        var values = $("input#ext-param-value");
        var ctr = "{";
        $(keys).each(function(e) {
            var k = $(this).val();
            var v = $(values[e]).val();
            if (checkParams(k) || checkParams(v)) {
                return true;
            }

            if (values.length - 1 > e) {
                ctr += "\"" + k + "\"" + ":" + "\"" + v + "\"" + ",";
            } else {
                ctr += "\"" + k + "\"" + ":" + "\"" + v + "\"";
            }
        });
        return ctr + "}";
    }

    function checkParams(p) {
        return (null == p || "" == p || "undefined" == p || p.length == 0)
    }
</script>
</#escape>