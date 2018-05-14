<#escape x as (x)!>

	<#include "../header.ftl">
    <#if inajax=="0">
<#--laydate 时间控件-->
<#--配置文件 -->
<link rel="stylesheet"  href="static/plugins/layui/css/layui.css"/>
<script type="text/javascript"  src="static/plugins/layui/layui.js" charset="utf-8"></script>

    <!-- header start -->
    <section class="content-header">
        <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
                <li class="active"><a href="orders/queue">消息队列</a></li>
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
                    <h1>搜索列表</h1>
                </div>
                <form class="form-horizontal" method="post" action="orders/queue">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">订单号：</label>
                            <input type="text" name="order_sn" id="order_sn" class="form-control input-sm" value="${order_sn}" />
                        </div>
                        <div class="col-xs-3 col-md-3">
                            <label class="control-label">线程状态：</label>
                            <select name="thread_status" id="thread_status" class="form-control input-sm">
                                <option value="">--请选择状态--</option>
                                <option value="1" <#if thread_status?? && thread_status=="1">selected</#if>>处理中</option>
                                <option value="0" <#if thread_status?? && thread_status=="0">selected</#if>>未处理</option>
                            </select>
                        </div>
                        <div class="col-xs-3 col-md-3">
                            <label class="control-label">通知状态：</label>
                            <select name="consumer_status" id="consumer_status" class="form-control input-sm">
                                <option value="">--请选择状态--</option>
                                <option value="0" <#if consumer_status?? && consumer_status=="0">selected</#if>>未被消费</option>
                                <option value="1" <#if consumer_status?? && consumer_status=="1">selected</#if>>消费成功</option>
                                <option value="2" <#if consumer_status?? && consumer_status=="2">selected</#if>>消费处理中</option>
                                <option value="-1" <#if consumer_status?? && consumer_status=="-1">selected</#if>>消费失败</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-6">
                            <label class="control-label">通知时间：</label>
                            <input type="text" name="startTime" id="startTime" readonly class="form-control input-sm" value="${startTime}">&nbsp;至&nbsp;
                            <input type="text" name="endTime"   id="endTime"  readonly class="form-control input-sm" value="${endTime}">
                        </div>
                    </div>
                    <input type="hidden" name="pageIndex" id="pageIndex" value="${page.getPageIndex()}" />
                    <button type="submit" class="btn btn-default">确定</button>
		        </form>
            </div>
	    </div>
        <!-- searchDiv end -->

        <div class="row list-toolbar">
            <div class="col-sm-6">
                <button id="searchHandle." onclick="toggleSearchDiv();"  type="button" class="btn btn-primary btn-sm">搜索列表</button>
                <button onclick="BatchHANDLE_STATUS()"  class="btn btn-primary btn-sm">批量修改为待处理</button>
                <#if sessionAdminUserInfo.roleId==1>
                    <button onclick="BatchHANDLE_DELETE()"  class="btn btn-primary btn-sm">批量删除</button>
                </#if>

            </div>
            <div class="col-sm-6">
                <nav class="pages pull-right">
                    <ul id="testpage" class="pagination pagination-sm">${pages}</ul>
                </nav>
            </div>
        </div>
        <!-- list-div start -->
        <div class="row">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th style="width:30px;"><input type="checkbox" name="checkb" id="checkbox_handle"><label for="checkbox"></label></th>
                    <th>商户号</th>
                    <th>订单号</th>
                    <th>消息topic</th>
                    <th>分布主机</th>
                    <th>产生时间</th>
                    <th>线程状态</th>
                    <th>消费状态</th>
                    <th>通知次数</th>
                    <#if sessionAdminUserInfo.roleId==1 ><!-- 超级管理员 -->
                    <th>操作</th>
                    </#if>
                </tr>
                </thead>
                <tbody>
                    <#if lists?? && lists?size&gt;0>
                        <#list lists as list>
                        <tr overstyle="on">
                            <td>
                                <#if list.threadStatus==1> <input type="checkbox" name="checkb" value="${list.mqid?c}" class="checkbox" >
                                <#elseif list.threadStatus==0> &nbsp;
                                </#if>
                            </td>
                            <td> ${list.partnerId}</td>
                            <td>${list.orderSn}</td>
                            <td title='消息TOPIC：${list.messageTopic}; 消息内容: ${list.messageBody}'>
                                <#if list.messageTopic=="TOPIC_TRADE_PAY_WAIT_SETTLE">交易加款
                                <#elseif list.messageTopic=="TOPIC_TRADE_PAY_WAIT_NOTIFY">交易通知
                                <#elseif list.messageTopic=="TOPIC_TRANSFER_PAY_WAIT_REFUND">代付失败退款
                                <#elseif list.messageTopic=="TOPIC_TRANSFER_PAY_WAIT_DEBIT">代付等待扣款
                                <#elseif list.messageTopic=="TOPIC_TRANSFER_PAY_WAIT_TO_BANK">代付提交至银行
                                <#elseif list.messageTopic=="TOPIC_TRANSFER_PAY_WAIT_NOTIFY">代付通知
                                <#elseif list.messageTopic=="TOPIC_TRADE_PAY_REFUND">交易退款
                                </#if>
                            </td>
                            <td>
                                ${list.messageHost}
                            </td>
                            <td>${list.dateline?string('yyyy-MM-dd HH:mm:ss')}</td>
                            <td>
                                <#if list.threadStatus==0>     <span class="label label-default">待处理</span>
                                <#elseif list.threadStatus==1> <span class="label label-success">处理中</span>
                                </#if>
                            </td>
                            <td>
                                <#if list.consumerStatus==0><span class="label label-default">未被消费</span>
                                <#elseif list.consumerStatus==1><span class="label label-success">消费成功</span>
                                <#elseif list.consumerStatus==2><span class="label label-primary">消费处理中</span>
                                <#elseif list.consumerStatus==-1><span class="label label-danger">消费失败</span>
                                </#if>
                            </td>
                            <td>${list.notifyCount}</td>
                            <#if sessionAdminUserInfo.roleId==1 >
                            <td>
                                <#if list.consumerStatus != 0>
                                    <a href="javascript:deleteQueue('${list.mqid}')">删除</a>
                                <#else>
                                    ---
                                </#if>
                            </td>
                            </#if>
                        </tr>
                        </#list>
                    <#else>
                        <tr class="no-data">
                            <td colspan="10"><em>很抱歉,未查询到任何数据信息!</em></td>
                        </tr>
                    </#if>
                </tbody>
            </table>
<#if inajax=="0">
        </div>
        <!-- list-div end -->

    </section>








<#include "../footer.ftl">
</#if>
<script type="text/javascript">

    layui.use("laydate",function () {
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



    //删除消息 start
    function deleteQueue(mqid){
        $.confirm({
            title: "删除确认",
            content: "<p class='text-warning'>确定要删除该项吗？</p>",
            buttons: {
                ok: function(){

                        if (mqid == "") {
                            $.alert("<p class='red'>参数错误!</p>",  "系统提示" );
                            return;
                        }
                        $.ajax({
                            url: "orders/deleteQueue",
                            data: {"mqid": mqid},
                            type: 'POST',
                            dataType: 'json',
                            cache: false,
                            success: function (data, textStatus) {

                                if (data == null || data == "") {
                                    $.alert("<p class='red'>操作失败!</p>", "系统提示");
                                    return false;
                                }

                                if (data.resultCode == "RESP_SUCCESS") {
                                    $.alert("<label class='green'>删除成功!</label>", function () {
                                        window.location.href = "${base}orders/queue";
                                    }, {title: "系统提示"});
                                } else {
                                    $.alert("<label class='red'>删除失败! 原因[" + data.resultMsg + "]</label>", "系统提示");
                                }
                            },
                            error: function (httpRequest, statusText, errorThrown) {
                                $.alert("<label class='red'>系统响应异常!</label>", null, {title: "系统提示"});
                            }
                        });
                },
                cancel: function(){

                }
            }
        })


    }    //删除消息 end



    //批量删除 start
    function BatchHANDLE_DELETE(){
        $.confirm({
            title: "批量操作确认",
            content: "<p class='text-warning'>确定要删除选中项吗？</p>",
            buttons: {
                ok: function(){
                    var hasChecked = $(".checkbox:checked");
                    if (hasChecked.length >= 1) {
                        var mqids = "";

                        hasChecked.each(function () {
                            mqids += this.value + ",";
                        });
                        mqids = mqids.substr(0, (mqids.length - 1));
                        if (mqids == "") {
                            $.alert("<font class='green'>请勾选需要通知的队列!</font>", null, {title: "系统提示"});
                            return;
                        }
                        $.ajax({
                            url: "orders/deleteQueue",
                            data: {"mqids": mqids},
                            type: 'POST',
                            dataType: 'json',
                            cache: false,
                            success: function (data, textStatus) {

                                if (data == null || data == "") {
                                    $.alert("<p class='red'>操作失败!</p>", "系统提示");
                                    return false;
                                }

                                if (data.resultCode == "RESP_SUCCESS") {
                                    $.alert("<label class='green'>删除成功!</label>", function () {
                                        window.location.href = "${base}orders/queue";
                                    }, {title: "系统提示"});
                                } else {
                                    $.alert("<label class='red'>删除失败! 原因[" + data.resultMsg + "]</label>", "系统提示");
                                }
                            },
                            error: function (httpRequest, statusText, errorThrown) {
                                $.alert("<label class='red'>系统响应异常!</label>", null, {title: "系统提示"});
                            }
                        });
                    }else{
                        $.alert("<p class='red'>至少选择一个对象！</p>", null, {title:'系统提示'});
                    }
                },
                cancel: function(){

                }
            }
        })


    }    //批量删除 end

    //批量修改线程状态为待处理
    function BatchHANDLE_STATUS(){

        $.confirm({
            title: "批量操作确认",
            content: "<p class='text-warning'>确定要批量修改选中项为待处理吗？</p>",
            buttons: {
                ok: function(){
                    var hasChecked = $(".checkbox:checked");
                    if (hasChecked.length >= 1) {
                        var mqids = "";

                        hasChecked.each(function () {
                            mqids += this.value + ",";
                        });
                        mqids = mqids.substr(0, (mqids.length - 1));
                        if (mqids == "") {
                            $.alert("<p class='green'>请勾选需要通知的队列!</p>", null, {title: "系统提示"});
                            return;
                        }
                        $.ajax({
                            url: "orders/updateQueue",
                            data: {"mqids": mqids},
                            type: 'POST',
                            dataType: 'json',
                            cache: false,
                            success: function (data, textStatus) {

                                if (data == null || data == "") {
                                    $.alert("<p class='red'>操作失败!</p>", "系统提示");
                                    return false;
                                }

                                if (data.resultCode == "RESP_SUCCESS") {
                                    $.alert("<p class='red'>批量修改成功!</p>", function(){location.reload();}, {title: "系统提示"})
                                } else {
                                    $.alert("<label class='red'>修改失败! 原因[" + data.resultMsg + "]</label>", "系统提示");
                                }
                            },
                            error: function (httpRequest, statusText, errorThrown) {
                                $.alert("<label class='red'>系统响应异常!</label>", null, {title: "系统提示"});
                            }
                        });
                    }else{
                        $.alert("<p class='red'>至少选择一个对象！</p>", null, {title:'系统提示'});
                    }
                },
                cancel: function(){

                }
            }
        })
    }


    function showips(ip_white_list){
        if(ip_white_list==""){
            Boxy.alert("<font style='color:red;' size='4'>还未添加白名单请及时添加白名单</font>", null, {title: "IP白名单"});
        }else{
            Boxy.alert("<font size='4'>"+ip_white_list+"</font>", null, {title: "IP白名单"});
        }
    }

    //批量通知
    function BatchToNotice(){
        Boxy.confirm("<font class='red'>确定要批量通知选中项吗？</font>", function(){
            var hasChecked = $(".checkbox:checked");
            if (hasChecked.length >= 1) {
                var mqids = "";
                hasChecked.each(function(){
                    mqids += this.value +",";
                });
                mqids = mqids.substr(0, (mqids.length - 1));
                if(mqids==""){
                    Boxy.alert("<font class='green'>请勾选需要通知的队列!</font>", null, {title: "系统提示"});
                    return;
                }
                $.ajax({
                    url:"orders/batchQuene",
                    data: {"mqids":mqids},
                    type: 'POST',
                    dataType: 'json',
                    cache: false,
                    success: function(data, textStatus) {
                        var result = data.result;
                        if (result == "1") {
                            Boxy.alert("<font class='red'>批量通知成功!</font>", function(){location.reload();}, {title: "系统提示"});
                        }else{
                            Boxy.alert("<font class='red'>"+data.msg+"</font>", null, {title: "系统提示"});
                        }
                    },
                    error: function(httpRequest, statusText, errorThrown) {
                        Boxy.alert("<font class='red'>系统响应异常!</font>", null, {title: "系统提示"});
                    }
                });
            } else {
                Boxy.alert("<font class='red'>至少选择一个对象！</font>", null, {title:'系统提示'});
            }
        }, {title: "系统提示"});
    }

</script>

</#escape>