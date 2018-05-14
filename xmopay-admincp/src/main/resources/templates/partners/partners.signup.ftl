<#escape x as (x)!>
<#include "../header.ftl">

<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li><a href="partners/index?partner_type=PTYPE_PARTNER">商户列表</a></li>
            <li class="active"><a href="partners/signup">商户注册</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section class="content-body">
	<div class="row form">
		<div class="col-sm-6">
            <form id="partnersForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="partners/register">
                <div class="form-group">
                    <label class="col-sm-3 control-label">商户名称</label>
                    <div class="col-sm-9">
                        <input name="partner_name" type="text" class="form-control" id="partner_name" placeholder="商户名称"
                               data-fv-row=".col-sm-9"
                               data-fv-notempty="true"
                               required
                               data-fv-notempty-message="必须填写商户名称">
                    </div>
                </div>
                <span id="emailInfo">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">邮箱</label>
                        <div class="col-sm-9">
                            <input name="email" type="text" class="form-control" id="email" placeholder="邮箱"
                                   data-fv-row=".col-sm-9"
                                   required
                                   data-fv-notempty-message="必须填写登录邮箱">
                        </div>
                    </div>
                </span>
                <div class="form-group">
                    <label class="col-sm-3 control-label">平台名称</label>
                    <div class="col-sm-9">
                        <input name="app_title" type="text" class="form-control" id="app_title" placeholder="平台名称"
                               data-fv-row=".col-sm-9"
                               data-fv-notempty="true"
                               required
                               data-fv-notempty-message="必须填写平台名称">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">平台域名</label>
                    <div class="col-sm-9">
                        <input name="app_domain" type="text" class="form-control" id="app_domain" placeholder="平台域名"
                               data-fv-row=".col-sm-9"
                               data-fv-notempty="true"
                               required
                               data-fv-notempty-message="必须填写平台域名">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">网关地址</label>
                    <div class="col-sm-9">
                        <input name="app_gateway_url" type="text" class="form-control" id="app_gateway_url" placeholder="网关地址"
                               data-fv-row=".col-sm-9"
                               data-fv-notempty="true"
                               required
                               data-fv-notempty-message="必须填写网关地址">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">状态</label>
                    <div class="col-sm-9 form-inline">
                        <select class="form-control" name="status" id="status">
                            <option value="0">待开通</option>
                            <option value="1">已开通</option>
                            <option value="-1">冻结</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-12">
                        <button type="submit" data-loading-text="Login Loading..." id="blackSubmit" class="btn btn-primary">注册商户</button>
                    </div>
                </div>
            </form>
		</div>
	</div>
</section>


<#include "../footer.ftl">
<script type="text/javascript">

    $(document).ready(function() {
        $('#partnersForm').formValidation().on('success.form.fv', function(e) {
            var partner_type = $("#partner_type").val();
            // Prevent form submission
            e.preventDefault();

            // Some instances you can use are
            var $form = $(e.target),        // The form instance
                    fv    = $(e.target).data('formValidation'); // FormValidation instance

            var $btn = $("#blackSubmit").button('loading');
//            $("#userpwd").val(hex_md5($("#userpwd").val()));
//            $("#userpwd_").val(hex_md5($("#userpwd_").val()));

            // Use Ajax to submit form data
            $.ajax({
                url: $form.attr('action'),
                type: 'POST',
                data: $form.serialize(),
                dataType:"json",
                success: function(data) {
                    if(data==null || data==""){
                        $btn.button('reset'); //重置按钮状态
                        $.alert('系统错误！', '系统提示');
                    }
                    if(data.resultCode=="RESP_SUCCESS"){
                        $.confirm({
                            title: '系统提示',
                            content: "<p class='text-success'>注册成功！</p>",
                            buttons: {
                                ok:function () {
                                    window.location.href="${base}partners/index";
                                }
                            }
                        });
                    }else{
                        $btn.button('reset'); //重置按钮状态
                        $.alert(data.resultMsg, '系统提示');
                    }
                },
                error : function(data) {
                    $btn.button('reset'); //重置按钮状态
                    $.alert('系统异常', '系统提示');
                }
            });
        });

    });

</script>
</#escape>