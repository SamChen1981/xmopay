<#escape x as (x)!>
<#include "../header.ftl">
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li class="active"><a href="settings/basic"> 基本设置 </a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section class="content-body">
    <div class="row form">
        <div class="col-sm-6">
            <form id="partnersForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="settings/editBasic">
                <input id="type" name="type" type="hidden" value="1" />
                <div class="form-group">
                    <label class="col-sm-3 control-label">站点名称：</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="sitename" value="${map.sitename!''}">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">系统公告：</label>
                    <div class="col-sm-9">
                        <textarea name="system_notice" class="form-control" rows="3">${map.system_notice!''}</textarea>
                    </div>
                </div>


                <div class="form-group">
                    <label class="col-sm-3 control-label">应用名字：</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="appname" value="${map.appname!''}">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 control-label">应用域名：</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="appdomain" value="${map.appdomain!''}">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">接口地址：</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="api_url" value="${map.api_url!''}">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">上游IP白名单临时库：</label>
                    <div class="col-sm-9">
                        <textarea name="ip_white_list" class="form-control" rows="3" <#if sessionAdminUserInfo?? && sessionAdminUserInfo.roleId!=1>readonly</#if>>${map.ip_white_list!''}</textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">接口测试参数：</label>
                    <div class="col-sm-9">
                        <textarea name="api_test_params" class="form-control" rows="3" <#if sessionAdminUserInfo?? && sessionAdminUserInfo.roleId!=1>readonly</#if>>${map.api_test_params!''}</textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">国内短信参数：</label>
                    <div class="col-sm-9">
                        <textarea name="api_sms_params_zh" class="form-control" rows="3" <#if sessionAdminUserInfo?? && sessionAdminUserInfo.roleId!=1>readonly</#if>>${map.api_sms_params_zh!''}</textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">国外短信参数：</label>
                    <div class="col-sm-9">
                        <textarea name="api_sms_params" class="form-control" rows="3" <#if sessionAdminUserInfo?? && sessionAdminUserInfo.roleId!=1>readonly</#if>>${map.api_sms_params!''}</textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">邮箱接口参数：</label>
                    <div class="col-sm-9">
                        <textarea name="api_email_params" class="form-control" rows="3" <#if sessionAdminUserInfo?? && sessionAdminUserInfo.roleId!=1>readonly</#if>>${map.api_email_params!''}</textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">版权所有：</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="copyright" value="${map.copyright!''}">
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-12">
                        <button type="submit" id="blackSubmit" class="btn btn-primary">保存配置</button>
                    </div>
                </div>
			</form>
		</div>
	</div>
</section>

<#include "../footer.ftl">
<script type="text/javascript">
    $(document).ready(function() {
        $('#partnersForm')
                .formValidation(
                        {
                            fields: {
                                sitename: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入站点名称'
                                        }
                                    }
                                },
                                system_notice: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入系统公告'
                                        }
                                    }
                                },
                                appname: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入应用名字'
                                        }
                                    }
                                },
                                appdomain: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入应用域名'
                                        }
                                    }
                                },
                                api_url: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入接口地址'
                                        }
                                    }
                                },
                                ip_white_list: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入上游IP白名单临时库'
                                        }
                                    }
                                },
                                api_test_params: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入接口测试参数'
                                        }
                                    }
                                },
                                api_sms_params: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入短信接口参数'
                                        }
                                    }
                                },
                                api_email_params: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入邮箱接口参数'
                                        }
                                    }
                                },
                                copyright: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入版权所有'
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
                            if(data==null || data==""){
                                $.alert('系统错误！', '系统提示');
                            }
                            if(data.resultCode=="RESP_SUCCESS"){
                                $.confirm({
                                    title: '系统提示',
                                    content: "<p class='text-success'>操作成功!</p>",
                                    buttons: {
                                        ok: function () {
                                            window.location.href = '${base}settings/basic';
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