<#escape x as (x)!>
<#include "../header.ftl">
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li><a href="user/roleList"> 角色列表 </a></li>
            <li class="active"><a href="javascript:"><#if mark=="to_add">添加</#if><#if mark=="to_edit">修改</#if>角色</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section class="content-body">
    <div class="row form">
        <div class="col-sm-6">
            <form id="partnersForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="user/addRole">
                <div class="form-group">
                    <label class="col-sm-3 control-label">角色名称：</label>
                    <div class="col-sm-9">
                        <input class="form-control" id="rolename" name="rolename" type="text" maxlength="45" value="${roleMap.roleName}" <#if roleMap??>readonly</#if>/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 control-label">角色描述：</label>
                    <div class="col-sm-9">
                        <textarea class="form-control" rows="3" cols="12" name="roledesc" id="roledesc">${roleMap.roleDesc}</textarea>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-12">
                        <input id="rid" name="rid" type="hidden"  maxlength="45" value="${roleMap.rid}">
                        <button type="submit" id="blackSubmit" class="btn btn-primary">确定</button>
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
                    rolename: {
                        row: '.col-sm-9',
                        validators: {
                            notEmpty: {
                                message: '请输入角色名称'
                            }
                        }
                    },
                    roledesc: {
                        row: '.col-sm-9',
                        validators: {
                            notEmpty: {
                                message: '请输入角色描述'
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
                                    window.location.href = '${base}user/roleList'
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