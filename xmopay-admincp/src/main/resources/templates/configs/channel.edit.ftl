<#escape x as (x)!>
    <#include "../header.ftl">
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li><a href="channel/index">渠道列表</a></li>
            <li class="active"><a href="channel/edit?channelId=0"><#if channel??>修改<#else>添加</#if>渠道</a></li>
            <li><a href="channel/agencyList">机构列表</a></li>
            <li><a href="channel/editAgency">添加机构</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section class="content-body">
    <div class="row form">
        <div class="col-sm-12">
            <form id="productEditForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="channel/${action}">
                <input type="hidden" name="channelId" id="channel_id" value="<#if channel??&&channel.channelId??>${channel.channelId}</#if>">
                <div class="form-group">
                    <label class="col-sm-2 control-label">渠道类型：</label>
                    <div class="col-sm-6 form-inline">
                        <select class="form-control" name="channelType" id="channel_type">
                            <option value="0" <#if channel??&&channel.channelType??&&channel.channelType==0>selected</#if>>交易网关</option>
                            <option value="1" <#if channel??&&channel.channelType??&&channel.channelType==1>selected</#if>>下发网关</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">渠道机构：</label>
                    <div class="col-sm-6 form-inline">
                        <select class="form-control" name="channelAgency" id="channel_agency"
                                data-fv-row=".col-sm-6"
                                data-fv-notempty="true"
                                required
                                data-fv-notempty-message="必须选择渠道机构">
                            <option value="">请选择渠道机构</option>
                            <#if agencies??&&agencies?size&gt;0>
                                <#list agencies as list>
                                    <option value="${list.gaid}|${list.agencyCode}" <#if channel??&&channel.channelType??&&channel.agencyId==list.gaid>selected</#if>>${list.agencyName}</option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">渠道名称：</label>
                    <div class="col-sm-6">
                        <input type="text" class="form-control" name="channelName" id="channel_name" placeholder="渠道名称"
                               required
                               data-fv-notempty-message="请填写渠道名称"
                               value="<#if channel??&&channel.channelName??>${channel.channelName}</#if>">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">渠道KEY：</label>
                    <div class="col-sm-6">
                        <input type="text" class="form-control" name="channelKey" id="channel_key" placeholder="渠道KEY"
                               data-fv-row=".col-sm-6"
                               required
                               data-fv-notempty-message="请填写渠道KEY"
                               value="<#if channel??&&channel.channelKey??>${channel.channelKey}</#if>">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">渠道密钥：</label>
                    <div class="col-sm-6">
                        <textarea class="form-control" rows="3" name="channelSecret" id="channel_secret"
                                  data-fv-row=".col-sm-6"
                                  required
                                  data-fv-notempty-message="请填写渠道密钥"
                        ><#if channel??&&channel.channelSecret??>${channel.channelSecret}</#if></textarea>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label">其他参数：</label>
                    <div class="col-sm-6" id="extends-params">
                        <#if channelParamsList??&&channelParamsList?size&gt;0>
                            <#list channelParamsList?keys as k>
                                <div class="form-group">
                                    <div class="col-xs-4">
                                        <input id="ext-param-key" type="text" class="form-control" placeholder="key" value="${k}"/>
                                    </div>
                                    <div class="col-xs-4">
                                        <input id="ext-param-value" type="text" class="form-control" placeholder="value" value="${channelParamsList[k]}"/>
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
                    <label class="col-sm-2 control-label">渠道状态：</label>
                    <div class="col-sm-6">
                        <label class="radio-inline">
                            <input type="radio" name="status" id="status" value="1" <#if channel??&&channel.status??&&channel.status==1>checked<#else>checked</#if>>开通
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="status" id="status" value="0" <#if channel??&&channel.status??&&channel.status==0>checked</#if>>关闭
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

            var channelKey = $("#channel_key").val();
            var channelName = $("#channel_name").val();
            var channelSecret = $("#channel_secret").val();
            var status = $("input[name='status']:checked").val();
            var channelType = $("#channel_type").val();
            var channelAgency = $("#channel_agency").val();
            var orderNumberRule = $("#order_number_rule").val();

            var channelParams = resolveExtendsParams();

            // Use Ajax to submit form data
            $.ajax({
                url: $form.attr('action'),
                type: 'POST',
                data: {
                        "status": status,
                        "channelKey": channelKey,
                        "channelType": channelType,
                        "channelName": channelName,
                        "channelAgency": channelAgency,
                        "channelParams": channelParams,
                        "channelSecret": channelSecret,
                        "orderNumberRule": orderNumberRule,
                        "channelId": $("#channel_id").val()
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
                                    window.location.href = "${base}channel/index";
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
        $(obj).parent('div').parent().remove();
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