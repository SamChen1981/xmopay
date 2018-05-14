<#escape x as (x)!>
    <#if inajax=="0">
    <#include "../header.ftl">
<#--laydate 时间控件-->
<#--配置文件 -->
<link rel="stylesheet"  href="static/plugins/layui/css/layui.css"/>
<script type="text/javascript"  src="static/plugins/layui/layui.js" charset="utf-8"></script>
<link rel="stylesheet" href="static/plugins/bootstrap-multiselect/bootstrap-multiselect.css" />
<script type="text/javascript" src="static/plugins/bootstrap-multiselect/bootstrap-multiselect.min.js" charset="UTF-8"></script>


    <!-- header start -->
    <section class="content-header">
        <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
                <li class="active"><a href="orders/index">交易订单</a></li>
            </ul>
        </div>
    </section>
    <!-- header end -->

    <section id="listDiv" class="content-body">
</#if>
        <!-- searchDiv start -->
        <div id="hideSearchDiv" class="row searchDiv" style="display:none">
            <div class="col-sm-12">
                <div class="page-header">
                    <h1>搜索交易订单</h1>
                </div>
                <form class="form-horizontal" id="actionForm" method="post" action="orders/index">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">商户ID号：</label>
                            <input type="text" name="partner_id" id="partner_id" class="form-control input-sm" value="${partner_id}">
                        </div>
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">平台单号：</label>
                            <input type="text" name="order_sn" id="order_sn" class="form-control input-sm" value="${order_sn}">
                        </div>
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">商户单号：</label>
                            <input type="text" name="out_trade_no" id="out_trade_no" class="form-control input-sm" value="${out_trade_no}">
                        </div>
                        <div class="col-xs-6 col-md-3"></div>
                    </div>

                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-6">
                            <label class="control-label">订单时间：</label>
                            <input type="text" name="startTime" readonly id="startTime" class="form-control input-sm input-append date" value="${startTime}">
                            <input type="text" name="endTime"   readonly id="endTime"   class="form-control input-sm input-append date" value="${endTime}">
                        </div>
                        <div class="col-xs-6 col-md-6">
                            <label class="control-label">交易金额：</label>
                            <input type="text" name="min_paymoney" id="min_paymoney" class="form-control input-sm" value="${min_paymoney}">
                            <input type="text" name="max_paymoney" id="max_paymoney" class="form-control input-sm" value="${max_paymoney}">
                        </div>
                    </div>

                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">订单状态：</label>
                            <select class="form-control input-sm" name="order_status" id="order_status"
                                    onchange="fac(this.id)">
                                <option value="">==订单状态==</option>
                                <option value=0  <#if order_status?? && order_status=="0">selected</#if> >未提交到银行</option>
                                <option value=1  <#if order_status?? && order_status=="1">selected</#if> >交易成功</option>
                                <option value=2  <#if order_status?? && order_status=="2">selected</#if> >已提交待处理</option>
                                <option value=-1 <#if order_status?? && order_status=="-1">selected</#if>>交易失败</option>
                                <option value=-2 <#if order_status?? && order_status=="-2">selected</#if>>交易自动关闭</option>
                                <option value=-3 <#if order_status?? && order_status=="-3">selected</#if>>订单已退款</option>
                                <option value=-9 <#if order_status?? && order_status=="-9">selected</#if>>异常订单</option>
                            </select>
                        </div>
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">通知状态：</label>
                            <select class="form-control input-sm" name="notice_status" id="notice_status">
                                <option value="">==通知状态==</option>
                                <option value=0  <#if notice_status?? && notice_status=="0">selected</#if> >等待通知</option>
                                <option value=-1 <#if notice_status?? && notice_status=="-1">selected</#if>>通知失败</option>
                                <option value=2  <#if notice_status?? && notice_status=="2">selected</#if>>已通知</option>
                                <option value=1  <#if notice_status?? && notice_status=="1">selected</#if> >通知成功</option>
                            </select>
                        </div>
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">对账状态：</label>
                            <select class="form-control input-sm" name="check_status" id="check_status">
                                <option value="">==对账状态==</option>
                                <option value=0  <#if check_status?? && check_status=="0">selected</#if> >未对账</option>
                                <option value=1  <#if check_status?? && check_status=="1">selected</#if> >已成功对账</option>
                                <option value=2  <#if check_status?? && check_status=="2">selected</#if> >已对账，待上游处理</option>
                                <option value=-9 <#if check_status?? && check_status=="-9">selected</#if>>异常</option>
                                <option value=-2 <#if check_status?? && check_status=="-2">selected</#if>>上游无此订单</option>
                                <option value=-1 <#if check_status?? && check_status=="-1">selected</#if>>交易失败</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">银行代码：</label>
                            <select class="form-control input-sm" name="bank_code" id="bank_code"
                                    data-fv-row=".col-sm-5"
                                    data-fv-notempty="true"
                                    data-fv-notempty-message="==请选择银行==">
                                <option value="">==请选择银行==</option>
                                <option value="QQPAY_QRCODE"  <#if bank_code?? && bank_code == "QQPAY_QRCODE">selected</#if>>QQ扫码支付</option>
                                <option value="ALIPAY_QRCODE" <#if bank_code?? && bank_code == "ALIPAY_QRCODE">selected</#if>>支付宝支付</option>
                                <option value="QPAY_UNIONPAY" <#if bank_code?? && bank_code == "QPAY_UNIONPAY">selected</#if>>银联快捷支付</option>
                                <option value="WEIXIN_QRCODE" <#if bank_code?? && bank_code == "WEIXIN_QRCODE">selected</#if>>微信支付</option>
                                <#if bankList?? && bankList?size&gt;0>
                                    <#list bankList as bankList>
                                        <option value=${bankList.respCode} <#if bank_code?? && bank_code == bankList.respCode>selected</#if>>   ${bankList.respName}  </option>
                                    </#list>
                                </#if>
                            </select>
                        </div>
                        <div class="col-xs-3">
                            <label class="control-label">支付方式：</label>
                            <select class="form-control input-sm" name="pay_type" id="pay_type">
                                <option value="">==选择支付方式==</option>
                                <option value="BANK_PAY"  <#if pay_type?? && pay_type == "BANK_PAY">selected</#if>>网银支付</option>
                                <option value="QUICK_PAY" <#if pay_type?? && pay_type == "QUICK_PAY">selected</#if>>快捷支付</option>
                                <option value="QQPAY"     <#if pay_type?? && pay_type == "QQPAY">selected</#if>>QQ扫码支付</option>
                                <option value="WXPAY"     <#if pay_type?? && pay_type == "WXPAY">selected</#if>>微信扫码支付</option>
                                <option value="ALIPAY"    <#if pay_type?? && pay_type == "ALIPAY">selected</#if>>支付宝扫码支付</option>
                            </select>
                        </div>
                        <div class="col-xs-6 col-md-3">
                            <label class="control-label">交易IP：</label>
                            <input type="text" name="trade_ip" id="trade_ip" class="form-control input-sm" value="${trade_ip}">
                        </div>
                    </div>
                    <div class="form-group form-inline">
                        <div class="col-xs-6">
                            <label class="control-label">交易接口：</label>
                            <select class="form-control input-sm" name="channel_code" id="channel_code">
                                <option value="">--选择支付渠道--</option>
                                <#if gatewaylist??>
                                    <#list gatewaylist as list>
                                        <option value="${list.channelCode}" <#if channel_code?? && channel_code == list.channelCode>selected</#if>>
                                        ${list.channelName} [${list.channelCode}]
                                        </option>
                                    </#list>
                                </#if>
                            </select>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-default">搜索</button>
                </form>
            </div>
        </div>
        <!-- searchDiv end -->


        <div class="row list-toolbar">
            <div class="col-sm-6">
                <button id="searchHandle." onclick="toggleSearchDiv();" type="button" class="btn btn-primary btn-sm">搜索订单</button>
                <#if sessionAdminUserInfo.roleId==1><button onclick="queryOrders()" type="button" class="btn btn-primary btn-sm">一键查单</button></#if>
            </div>
            <div class="col-sm-6">
                <nav class="pages pull-right">
                    <ul id="testpage" class="pagination pagination-sm">
                    ${pages}
                    </ul>
                </nav>
            </div>
        </div>
        <div class="row ">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>类型</th>
                        <th>商户号</th>
                        <th>平台/商户订单</th>
                        <th>银行代码</th>
                        <th>金额</th>
                        <th>订单时间</th>
                        <th>完成时间</th>
                        <th>交易状态</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                <#if lists?? && lists?size&gt;0>
                    <#list lists as list>
                    <tr id="toid-${list.toid}" overstyle="on">
                        <td  title="${list.tranType}">
                            <#if list.tranType == 1000>支付
                            <#elseif list.tranType == 1001>充值
                            <#elseif list.tranType == 2001>转账
                            </#if>
                        </td>
                        <td title="${list.partnerId}">
                            <#if list.partnerId?? && list.partnerId?length lt 13>
                                ${list.partnerId}
                            <#else>
                                <span class="text-info">${list.partnerId?substring(12,list.partnerId?length)}</span>
                            </#if>
                        </td>
                        <td class="blue" title="平台流水号：${list.billingSn}">${list.orderSn}<br><small><#if list.outTradeNo??>${list.outTradeNo}<#else>------</#if></small></td>
                        <td title="渠道代码：${list.channelCode} | 上游银行代码：${list.bankCode}"><#if list.bankCode??>${list.bankCode}<#else>------</#if></td>
                        <td>￥${list.orderAmount?string("#0.00")}</td>
                        <td>${list.orderTime?string('MM-dd HH:mm:ss')!'------'}</td>
                        <td>${list.finishTime?string('MM-dd HH:mm:ss')!'------'}</td>
                        <td>
                            <#if list.orderStatus==0>     <span class="label label-danger">交易中断</span>
                            <#elseif list.orderStatus==1> <span class="label label-success">成功</span>
                            <#elseif list.orderStatus==2> <span class="label label-default">待处理</span>
                            <#elseif list.orderStatus==-9><span class="label label-warning">风控</span>
                            <#elseif list.orderStatus==-3><span class="label label-info">已退款</span>
                            <#elseif list.orderStatus==-1><span class="label label-danger">失败</span>
                            <#elseif list.orderStatus==-2><span class="label label-danger">自动关闭</span>
                                <#--<span class="deepred">风控[<a href="javascript:void(0);" onclick="changeRiskBlack('${list.orderSn}', '&lt;#&ndash;${list.userId}&ndash;&gt;')">改</a>]</span>-->
                            </#if>
                        </td>
                        <td>
                            <#if list.tranType == 1000>
                                <#if list.orderStatus==1 && list.refundStatus==0>
                                    <a href="javascript:void(0);" onclick="supply('${list.orderSn}', this)">补</a> |
                                </#if>
                            </#if>
                            <a title="查看订单详情" class="toedit" href="orders/detail?order_sn=${list.orderSn}&partner_id=${list.partnerId}&tbname=<#if tbname??>${tbname}</#if>">详</a>
                            <#if list.orderStatus==1 || list.orderStatus==2>
                                <#if list.channelCode?? && list.channelCode?index_of("SQEPAY")!=-1>

                                <#else>
                                <#if list.tranType != 2001>
                                    | <a href="javascript:void(0);" onclick="orderQuery('${list.orderSn}', '${list.partnerId}','${list.toid}')">查</a>
                                </#if>
                                </#if>
                            </#if>
                            <#--<#if list.channelCode?? && list.channelCode?index_of('JYT') gt -1>-->
                            <#if list.orderStatus==1 && list.refundStatus==0 && list.billingSn??><#--交易成功，退款状态为默认状态，有流水号-->
                                | <a href="javascript:void(0);" onclick="orderRefund('${list.orderSn}', '${list.partnerId}')">退</a>
                            </#if>
                            <#--</#if>-->
                            <#if list.orderStatus==-9>
                                    | <a href="javascript:void(0);" onclick="orderRefund('${list.orderSn}', '${list.partnerId}')">退</a>
                            </#if>
                        </td>
                    </tr>
                    </#list>
                    <tr style="background:#C1FFC1">
                        <td colspan="11">
                            <button class="btn btn-primary btn-sm" onclick="getTotal()" type="button" id="totalButton">汇总统计</button>
                            <div id="totalDiv"  style="display: none;">
                                汇总结果：
                                <span class="text-success" id="amountTotal"></span>&nbsp;&nbsp;
                                <span class="text-success" id="sumAmount"></span>&nbsp;&nbsp;
                            </div>
                        </td>
                    </tr>
                    <#--<#if totalList??>-->
                    <#--<tr style="background:#C1FFC1"  overstyle="on">-->
                        <#--<td colspan="13">-->
                            <#--交易归总统计：-->
                            <#--<#list totalList as totalList>-->
                                <#--<#if totalList.ORDER_STATUS==1><span class="green">交易成功：${totalList.num}笔(${totalList.TOTAL?string("#,###.##")}元)</span>&nbsp;&nbsp;-->
                                <#--<#elseif totalList.ORDER_STATUS==-1><span class="deepred">交易失败：${totalList.num}笔(${totalList.TOTAL?string("#,###.##")}元)</span>&nbsp;&nbsp;-->
                                <#--<#elseif totalList.ORDER_STATUS==-2><span class="blue">交易中断：${totalList.num}笔(${totalList.TOTAL?string("#,###.##")}元)</span>&nbsp;&nbsp;-->
                                <#--<#elseif totalList.ORDER_STATUS==2><span class="blue">待处理：${totalList.num}笔(${totalList.TOTAL?string("#,###.##")}元)</span>-->
                                <#--</#if>-->
                            <#--</#list>-->
                        <#--</td>-->
                    <#--</tr>-->
                    <#--</#if>-->
                <#else>
                    <tr class="no-data">
                        <td colspan="11"><em>很抱歉,未查询到任何数据信息!</em></td>
                    </tr>
                </#if>
                </tbody>
            </table>
<#if inajax=="0">
        </div>
    </section>
        <#include "../footer.ftl">
</#if>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#channel_code").multiselect({
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
        layui.use('laydate', function(){
            var laydate = layui.laydate;

            laydate.render({
                elem: '#startTime' //指定元素
                ,type: 'datetime' //日期时间选择器
                ,format: 'yyyy-MM-dd HH:mm:ss' //可任意组合
                ,max: 1 //1天后
                ,istime: true
            });

            //执行一个laydate实例
            laydate.render({
                elem: '#endTime' //指定元素
                ,type: 'datetime' //日期时间选择器
                ,format: 'yyyy-MM-dd HH:mm:ss' //可任意组合
                ,max: 1 //1天后
                ,istime: true
            });
        });



        //导出交易订单
        function exportTrade() {
            var f = document.forms['pagefrm'];
            f.action = f.action + "?format=excel";
            f.submit();
            f.action = "orders/index";
            f.method = "post";
        }

        //补单通知
        function supply(order_sn, obj) {
            var $this = $(obj);
            var params = {"order_sn": order_sn, "tbname": '<#if tbname??>${tbname}</#if>'};
            $.ajax({
                type: "post",
                url: "orders/supply",
                data: params,
                dataType: "json",
                success: function (data, textStatus) {
                    if (data == null || data == "") {
                        $.alert("<p class='red'>结果异常</p>", "温馨提示");
                        return false;
                    }
                    if (data.resultCode == "RESP_SUCCESS") {
                        $.alert("<p class='green'>" + data.resultMsg + "</p>", "温馨提示");
                        return true;
                    } else {
                        $.alert("<p class='red'>" + data.resultMsg + "</p>", "温馨提示");
                        return false;
                    }
                },
                error: function () {
                    $.alert("<p class='red'>结果异常2</p>", "温馨提示");
                    return false;
                }
            });
            return true;
        }

        // 商户订单查询  --> 只查实时表
        function orderQuery(order_sn, partner_id, toid) {
            $.ajax({
                url: "orders/check",
                data: {"order_sn": order_sn, "partner_id": partner_id},
                type: 'POST',
                dataType: 'json',
                cache: false,
                success: function (data, textStatus) {
                    var result = data.resultCode;
                    var msg = data.resultMsg;
                    if (result == "RESP_SUCCESS") {
                        $.confirm({
                            columnClass: 'col-md-10',
                            title: "系统提示",
                            content: msg,
                            buttons: {
                                ok: function(){
                                   $("#toid-"+toid).hidden();
                                }
                            }
                        })
                    } else {
                        $.alert("<p class='red'>" + msg + "</p>", null, {title: "系统提示"});
                    }
                },
                error: function (httpRequest, statusText, errorThrown) {
                    $.alert("<p class='red'>系统响应异常!</p>", null, {title: "系统提示"});
                }
            });
        }

        function orderRefund(orderSn,partnerId) {
            $.dialog({
                title: "退款理由",
                content: 'url:orders/refundReason?order_sn=' + orderSn+'&partner_id='+partnerId + '&tbname=<#if tbname??>${tbname}</#if>',
                columnClass: 'col-md-8 col-md-offset-2',
                type: 'blue',
                typeAnimated: true
            });
        }

        function getTotal(){
            var form =$("#actionForm");
            var param = form.serialize();
            $.ajax({
                url:"orders/getTotal?tbname="+'<#if tbname??>${tbname}</#if>',
                type:"POST",
                data:param,
                dataType:"json",
                success:function (data) {
                    var result = data.resultData;
                    $("#amountTotal").html("总金额：<b style='color:red'>￥"+result.sumAmount+"</b>");
                    $("#sumAmount").html("笔数：<b style='color:red'>"+result.countNum+" </b>");
                    $("#totalDiv").css("display","inline");
                },
                error:function () {
                    $.alert('系统异常', '系统提示');
                }
            });
        }
    function queryOrders() {
        $.confirm({
            title:"系统提示",
            content:"确定进行一键查单吗?",
            buttons:{
                confirm:function () {
                    $.ajax({
                       url:"orders/queryOrders",
                       type:"POST",
                        dataType:"json",
                        success:function (data) {
                            if (data == null || data == "") {
                                $.alert('系统错误！', '系统提示');
                            }
                            $.confirm({
                                title: '系统提示!',
                                content: data.resultMsg,
                                buttons: {
                                    ok: function () {
                                        if (data.resultCode == "RESP_SUCCESS") {
                                            window.location.href = "${base}orders/index";
                                        }
                                    }
                                }
                            });
                        },
                        error:function () {
                            $.alert("系统异常","系统提示");
                        }
                    });
                },
                cancel:function () {

                }
            }
        });
    }
</script>

</#escape>