<#escape x as (x)!>
<#include "../header.ftl">

<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li><a href="orders/index?startTime=${pagefrm.startTime}&endTime=${pagefrom.endTime}&order_status=${pagefrm.order_status}&order_sn=${pagefrm.order_sn}&gateway_code=${pagefrm.gateway_code}&notice_status=${pagefrm.notice_status}&sources=${pagefrm.sources}&min_paymoney=${pagefrm.min_paymoney}&max_paymoney=${pagefrm.max_paymoney}&pageIndex=${pagefrm.pageindex}">返回</a></li>
            <li class="active"><a>编辑交易订单</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section class="content-body">
    <div class="row form">
        <div class="col-sm-6">
            <form id="submitForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="orders/editOrder">
                <div class="form-group">
                    <label class="col-sm-3 control-label">商户ID</label>
                    <div class="col-sm-9">
                        <label class="form-control">${tradeInfo.partnerId}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">商户名</label>
                    <div class="col-sm-9">
                        <label class="form-control">${tradeInfo.partnerName}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">支付金额</label>
                    <div class="col-sm-9">
                        <label class="form-control">${tradeInfo.orderAmount}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">平台订单号</label>
                    <div class="col-sm-9">
                        <label class="form-control" id="order_sn">${tradeInfo.orderSn}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">商户订单号</label>
                    <div class="col-sm-9">
                        <label class="form-control" >${tradeInfo.outTradeNo}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">创建时间</label>
                    <div class="col-sm-9">
                        <label class="form-control">${tradeInfo.orderTime?string("yyyy-MM-dd HH:mm:ss")}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">付款时间</label>
                    <div class="col-sm-9">
                        <label class="form-control">${tradeInfo.tradeTime?string("yyyy-MM-dd HH:mm:ss")}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">支付完成时间</label>
                    <div class="col-sm-9">
                        <label class="form-control">${tradeInfo.finishTime?string("yyyy-MM-dd HH:mm:ss")}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">交易IP</label>
                    <div class="col-sm-9">
                        <label class="form-control">${tradeInfo.tradeIp}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">TRADE_HASH</label>
                    <div class="col-sm-9">
                        <label class="form-control">${tradeInfo.tradeHash}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">异步地址</label>
                    <div class="col-sm-9">
                        <label class="form-control">${tradeInfo.notifyUrl}</label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">当前状态</label>
                    <div class="col-sm-9 form-inline">
                        <#if tradeInfo.orderStatus==-9>    <label class="text-danger">风控订单</label>
                        <#elseif tradeInfo.orderStatus==-3><label class="text-warning">订单已退款</label>
                        <#elseif tradeInfo.orderStatus==-2><label class="text-muted">订单自动关闭</label>
                        <#elseif tradeInfo.orderStatus==-1><label class="text-danger">交易失败</label>
                        <#elseif tradeInfo.orderStatus==2> <label class="text-primary">已提交待处理</label>
                        <#elseif tradeInfo.orderStatus==1> <label class="text-success">交易成功</label>
                        <#elseif tradeInfo.orderStatus==0> <label class="text-info">未提交到银行</label>
                        </#if>
                        <#-- '订单状态: WAIT_TO_BANK-未提交到银行  WAIT_BANK_NOTIFY-已提交待处理  TRADE_SUCCESS-交易成功
                        TRADE_FAILURE-交易失败  TRADE_EXCEPTION - 异常订单 TRADE_CLOSE - 订单自动关闭'  TRADE_REFUND-订单已退款, -->
                        <#if tradeInfo.orderStatus==2>
                            ===> 可调整为：
                            <select class="form-control" name="modify_order_status" id="modify_order_status">
                                <option value="">可调整状态</option>
                                <option value=-1>失败</option>
                                <option value=1>成功</option>
                            </select>
                        <#elseif tradeInfo.orderStatus==0>
                            ===> 可调整为：
                            <select class="form-control" name="modify_order_status" id="modify_order_status">
                                <option value="">可调整状态</option>
                                <option value=-2>订单自动关闭</option>
                            </select>
                        <#elseif tradeInfo.orderStatus==-9>
                            ===> 可调整为：
                            <select class="form-control" name="modify_order_status" id="modify_order_status">
                                <option value="">可调整状态</option>
                                <option value=1>成功</option>
                            </select>
                        </#if>

                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-12">
                        <label class="text-danger">（允许：已提交待处理-->成功[需要超级权限]；已提交待处理-->失败[需要超级权限]；未提交到银行-->订单自动关闭；）</label>
                    </div>
                </div>
				<#if tradeInfo.orderStatus=2 || tradeInfo.orderStatus==0 || tradeInfo.orderStatus==-9>
                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-12">
                        <input type="hidden" name="partner_id" id="partner_id" value="<#if tradeInfo??>${tradeInfo.partnerId}</#if>">
                        <input type="hidden" name="toid" id="toid" value="<#if tradeInfo??>${tradeInfo.toid}</#if>">
                        <button type="submit" data-loading-text="Loading..." id="blackSubmit" class="btn btn-primary">确定提交</button>
                    </div>
                </div>
				</#if>
            </form>
        </div>
    </div>
</section>

</div>
<#include "../footer.ftl">
<script type="text/javascript">
$(function(){
	<#if tradeInfo??>
        $(document).ready(function() {
            $('#submitForm').formValidation()
                .on('success.form.fv', function(e) {
                    // Prevent form submission
                    e.preventDefault();

                    // Some instances you can use are
                    var $form = $(e.target),        // The form instance
                            fv    = $(e.target).data('formValidation'); // FormValidation instance
                    var $btn = $("#loginSubmit").button('loading');
                    // Use Ajax to submit form data

                    var order_sn = $("#order_sn").text();
                    var modify_order_status = $("#modify_order_status").val();
                    $.ajax({
                        url: $form.attr('action'),
                        type: 'POST',
                        data: {"order_sn" : order_sn, "modify_order_status": modify_order_status },
                        dataType:"json",
                        success: function(data) {
                            if(data==null || data==""){
                                $btn.button('reset'); //重置按钮状态
                                $.alert('系统错误！', '系统提示');
                            }
                            if(data.resultCode=="RESP_SUCCESS"){
                                $.confirm({
                                    title: '系统提示',
                                    content: "<p class='text-success'>操作成功！</p>",
                                    buttons: {
                                        ok: function () {
                                            setTimeout("window.location.href = '${base}orders/index'", '1000');
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
	</#if>
	
});
</script>
</#escape>