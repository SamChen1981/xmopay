<#escape x as (x)!>
<#include "../header.ftl">
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li><a href="user/adminUserList"> 用户列表 </a></li>
            <li class="active"><a href="user/toEditUser"><#if muid??>编辑用户<#else>添加用户</#if></a></li>

		</ul>
    </div>
</section>
<!-- header end -->

<section class="content-body">
    <div class="row form">
        <div class="col-sm-6">
            <form id="partnersForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="user/addUser">
                <div class="form-group">
                    <label class="col-sm-3 control-label">角色选择：</label>
                    <div class="col-sm-9">
                        <select class="form-control" id="roleid" name="roleid">
                            <option value="">--请选择--</option>
							<#if rolelist??>
								<#list rolelist as rolelist>
                                    <option value="${rolelist.rid}" <#if userMap??><#if userMap.roleId==rolelist.rid>selected</#if></#if>>${rolelist.roleName}</option>
								</#list>
							</#if>
                        </select>
                    </div>
                </div>
				<#if mark == "to_edit">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">用户名称：</label>
                        <div class="col-sm-9">
                            <input class="form-control" id="username" name="username" type="text" maxlength="45" value="${userMap.userName}" readonly>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">原始密码：</label>
                        <div class="col-sm-9">
                            <input class="form-control" id="oldpassword" name="oldpassword" type="password" maxlength="45">
                        </div>
                    </div>
				<#else>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">用户名称：</label>
                        <div class="col-sm-9">
                            <input class="form-control" id="username" name="username" type="text" maxlength="45" >
                        </div>
                    </div>
				</#if>

                <div class="form-group">
                    <#if mark == "to_edit">
                        <label class="col-sm-3 control-label">新密码：</label>
                    <#else >
                        <label class="col-sm-3 control-label">登录密码：</label>
                    </#if>
                    <div class="col-sm-9">
                        <input class="form-control" id="repassword" name="repassword" type="password" maxlength="45">
					</div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 control-label">绑定IP：</label>
                    <div class="col-sm-9">
                        <input class="form-control" id="bindIp" name="bindIp" type="text" maxlength="45">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 control-label">用户状态：</label>
                    <div class="col-sm-9">
                        <select class="form-control" id="status" name="status">
                            <option value="">--请选择--</option>
                            <option value="0" <#if userMap??><#if userMap.status==0>selected</#if></#if>>锁定</option>
                            <option value="1" <#if userMap??><#if userMap.status==1>selected</#if></#if>>正常</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-12">
                        <input type="hidden" id="muid" name="muid" value="${userMap.muId}" />
                        <input type="hidden" id="mark" name="mark" value="${mark}" />
                        <button type="submit" id="blackSubmit" class="btn btn-primary">确定</button>
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
                            fields: {
                                roleid: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请选择角色'
                                        }
                                    }
                                },
                                username: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请输入用户名称'
                                        }
                                    }
                                },
                                status: {
                                    row: '.col-sm-9',
                                    validators: {
                                        notEmpty: {
                                            message: '请选择用户状态'
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

                    var _password = $("#repassword").val();
                    if(_password !=''){
                        $("#repassword").val(hex_md5(_password));
                    }
                    <#if mark == "to_edit" >
                        var oldPassword = $("#oldpassword").val();
                        if(oldPassword !=''){
                            $("#oldpassword").val(hex_md5(oldPassword));
                        }

                    </#if>


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
                            if(data==null || data=="") {
                                $.alert('系统错误！', '系统提示');
                                return false;
                            }
                            if(data.resultCode=="RESP_SUCCESS"){
                                $.confirm({
                                    title: '系统提示',
                                    content: "<p class='text-success'>"+data.resultMsg+"</p>",
                                    buttons: {
                                        ok: function () {
                                            window.location.href = '${base}user/adminUserList'
                                        }
                                    }
                                });
                            }else{
                                $.alert(data.resultMsg, '系统提示');
                                return false;
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