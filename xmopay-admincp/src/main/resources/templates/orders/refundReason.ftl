<#escape x as (x)!>
<#--action="${detail.FRONT_MER_URL}"-->
<form id="actionForm" class="form-horizontal" method="post" novalidate="novalidate" onSubmit="return false">
    <#if tradeOrderInfo??>
        <div class="form-group">
            <label class="col-sm-4 control-label">订单号：</label>
            <div class="col-sm-8">
                <p class="form-control-static">${tradeOrderInfo.orderSn}</p>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-4 control-label">支付金额：</label>
            <div class="col-sm-8">￥${tradeOrderInfo.orderAmount!'--'}</div>
        </div>
        <div class="form-group">
                <label class="col-sm-4 control-label">退款理由：</label>
                <div class="col-sm-8"><input required type="text" class="form-control" id="remark" name="remark" placeholder="退款理由100字以内"></div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-4 col-sm-8">
                    <input type="button" class="btn btn-default" value="确认" onclick="confirmRefund('${tradeOrderInfo.orderSn}','${tradeOrderInfo.orderAmount}','${tradeOrderInfo.orderStatus}','${tradeOrderInfo.channelCode}');" /> <#--申请退款-->
            </div>
        </div>
    <#else>
        <tr class="no-data">
            <td colspan="10"><em>很抱歉,未获取到任何订单信息!</em></td>
        </tr>
    </#if>
</form>
</#escape>
<script>
    function confirmRefund(order_sn,account,order_status,channel_code){
        var remark = $("#remark").val();
        if(remark==null || remark==""){
            $.alert("退款理由为空", "系统提示");
            return false;
        }

        var params={'order_sn':$.trim(order_sn),'remark':$.trim(remark),'account':$.trim(account),'order_status':$.trim(order_status),'channel_code':$.trim(channel_code), 'tbname':'<#if tbname??>${tbname}</#if>'};
        $.ajax({
            type: "post",
            async:false,
            url: "orders/doTradeRefund",
            data: params,
            dataType : "json",
            success: function(data, textStatus){
                if(data==null || data==""){
                    $.alert("系统错误", "系统提示");
                    return false;
                }
                var result = data.resultCode;
                if(result=="RESP_SUCCESS"){
//                    $.alert("<p class='text-success'>"+data.info.msg+"</p>", "温馨提示");
                    $.confirm({
                        title: '系统提示',
                        content: "<p class='text-success'>"+data.resultMsg+"</p>",
                        buttons: {
                            ok: function () {
                                window.location.href = "orders/index";
                            }
                        }
                    });
                    return true;
                }else{
                    $.alert(data.resultMsg, "系统提示");
                    return false;
                }
            },
            error: function(){
                $.alert('系统异常', '系统提示');
                return false;
            }
        });
        return true;
    }
</script>