<#escape x as (x)!>
<#include "../header.ftl">
<link rel="stylesheet" href="sysadmin/public/plugins/bootstrap-multiselect/bootstrap-multiselect.css" />
<script type="text/javascript" src="sysadmin/public/plugins/bootstrap-multiselect/bootstrap-multiselect.min.js" charset="UTF-8"></script>

    <!-- header start -->
    <section class="content-header">
        <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
                <li class="active"><a href="partners/change">商户调账</a></li>
            </ul>
        </div>
    </section>
    <!-- header end -->

    <section id="listDiv" class="content-body">
        <div id="hideSearchDiv" class="row searchDiv" >
            <div class="col-sm-12">
                <#--<div class="page-header">-->
                    <#--<h1>搜索商户</h1>-->
                <#--</div>-->
                <form class="form form-horizontal" method="post" action="partners/dochange" id="actionForm">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group">
                        <label class="control-label col-xs-3 col-md-2">商户名称：</label>
                        <div class="col-xs-3 col-md-4">
                            <select name="partnerName" id="partnerName" onchange="print(this.value)" class="form-control">
                                <option value="">==请选择商户==</option>
                                <#if lists?? && lists?size&gt;0>
                                    <#list lists as list>
                                        <#if list.partnerId??>
                                            <option value="${list.partnerId}">${list.partnerId}_${list.partnerName}</option>
                                        </#if>
                                    </#list>
                                </#if>
                            </select>
                        </div>
                    </div>
                    <div class="form-group ">
                        <label class="control-label  col-xs-3 col-md-2">当前账户余额：</label>
                        <div class="col-xs-3 col-md-3">
                            <input type="hidden" name="partner_id" id="partner_id" class="form-control input-sm" />
                            <input type="text" readonly="readonly" id="account_amount" style="border:0;" class="red"/>
                        </div>
                    </div>
                    <div class="form-group ">
                        <label class="control-label col-xs-3 col-md-2">调账金额：</label>
                        <div class="col-xs-6 col-md-4">
                            <input type="text" id="pay_money" name="pay_money" class="form-control"/>
                        </div>
                    </div>
                    <div class="form-group ">
                        <label class="control-label col-xs-3 col-md-2">调账类型：</label>
                        <div class="col-xs-3 col-md-4">
                            <label class="radio-inline">
                                <input type="radio" name="bill_type" id="btype" value="4001" checked > 差额加款
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="bill_type" id="btype" value="4002" > 差额扣款
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="bill_type" id="btype" value="5001" > 冲正加款
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="bill_type" id="btype" value="5002" > 冲正扣款
                            </label>
                        </div>
                    </div>
                    <div class="form-group " id="remark">
                        <label class="control-label col-xs-3 col-md-2">调账备注：</label>
                        <div class="col-xs-6 col-md-4">
                            <input id="remark" name="remark" rows="5" class="form-control"/>
                        </div>
                    </div>
                    <input type="hidden" name="partner_type" id="partner_type"  value="${partner_type}">
                    <div class="form-group ">
                        <label class="control-label col-xs-3 col-md-2"></label>
                        <div class="col-xs-6 col-md-4">
                            <button type="submit" class="btn btn-default"  id="btnSubmit">确定</button>
                        </div>
                    </div>

                </form>
            </div>
        </div>
    </section>


    <#include "../footer.ftl">
    <script type="text/javascript">

        $(document).ready(function() {
            $("#actionForm")
                    .formValidation({
                        fields: {
                            partnerName: {
                                row: '.col-md-4',
                                validators: {
                                    notEmpty: {
                                        message: '请选择商户名称'
                                    }
                                }
                            },
                            pay_money: {
                                row: '.col-md-4',
                                validators: {
                                    notEmpty: {
                                        message: '请输入调账金额'
                                    },
                                    regexp: {
                                        regexp: /^[0-9]+(.[0-9]+)?$/,
                                        message: '金额为数字'
                                    }
                                }
                            }
                        }
                    })
                    .on('success.form.fv', function(e) {
                        e.preventDefault();  // Prevent form submission
                        // Some instances you can use are
                        var $form = $(e.target),        // The form instance
                                fv    = $(e.target).data('formValidation'); // FormValidation instance

                        var bill_type = $('input[name="bill_type"]:checked').val();
                        if(bill_type=="4002" || bill_type=="5002"){
                            var account_amount = $("#account_amount").val();
                            if(parseFloat(account_amount)<=0){
                                $.alert("<p class='text-danger'>账户余额不足不允许进行调账减款!</p>",  {title: "系统提示"});
                                return;
                            }
                            if(parseFloat(account_amount) <= parseFloat($("#pay_money").val())){
                                $.alert("<p class='text-danger'>账户余额不足不允许进行调账减款!</p>",  {title: "系统提示"});
                                return;
                            }
                        }
                        var $btn = $("#btnSubmit").button('loading');
                        // Use Ajax to submit form data
                        $.confirm({
                            title:  "系统提示",
                            content: "<p class='green'>确定进行调账吗？</p>",
                            buttons: {
                                ok: function(){
                                    $.ajax({
                                        url: $form.attr('action'),
                                        type: 'POST',
                                        data: $form.serialize(),
                                        dataType:"json",
                                        success: function(data) {
                                            $btn.button('reset');
                                            if(data.resultCode == "RESP_SUCCESS"){
                                                $.confirm({
                                                    title:  "系统提示",
                                                    content: "<p class='text-success'>"+data.resultMsg+"</p>",
                                                    buttons: {
                                                        ok: function () {
                                                            setTimeout("location.reload()", '1000');
                                                        }
                                                    }
                                                });

                                            }else{
                                                $.alert('<p class="text-danger">'+data.resultMsg+'</p>', '系统提示');
                                            }
                                        },
                                        error : function(data) {
                                            //console.log(data);
                                        }
                                    });
                                },
                                cancel: function () {
                                    $btn.button('reset');
                                    return ;
                                }
                            }
                        });

                    });
            $("#partnerName").multiselect({
                enableFiltering: true,
                buttonWidth: '160px',
                maxHeight: 250,
                onChange: function(option, select) {
                    adjustByScrollHeight();
                },
                onDropdownShown: function(e) {
                    adjustByScrollHeight();
                },
                onDropdownHidden: function(e) {
                    adjustByHeight();
                }
            });
            function adjustByHeight() {
                var $body   = $('body'),
                        $iframe = $body.data('iframe.fv');
                if ($iframe) {
                    // Adjust the height of iframe when hiding the picker
                    $iframe.height($body.height());
                }
            }

            function adjustByScrollHeight() {
                var $body   = $('body'),
                        $iframe = $body.data('iframe.fv');
                if ($iframe) {
                    // Adjust the height of iframe when showing the picker
                    $iframe.height($body.get(0).scrollHeight/2);
                }
            }
        });


    function print(partner_id){
        if(partner_id==""){
            return;
        }
        $("#partner_id").val(partner_id);
        thisAmount(partner_id);
    }

    function thisAmount(partner_id){
        var params={'partner_id':partner_id};
        $.post('partners/getAmountPartnerById',params,function(data){
            var result = data.resultCode;
            if(result=="RESP_SUCCESS"){
                $("#account_amount").val(data.resultData);
            }
        },'json');
    }

    </script>
</#escape>