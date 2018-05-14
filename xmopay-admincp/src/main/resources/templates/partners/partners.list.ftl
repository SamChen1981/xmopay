<#escape x as (x)!>
<#if inajax?? && inajax=="0">
	<#include "../header.ftl">
	<!-- header start -->
	<section class="content-header">
    	<div class="nav-tabs-custom">
        	<ul class="nav nav-tabs">
				<li class="active"><a href="partners/index">商户列表</a></li>
				<li><a href="partners/signup">商户注册</a></li>
			</ul>
		</div>
	</section>
	<!-- header end -->

    <section id="listDiv" class="content-body">
</#if>
        <div id="hideSearchDiv" class="row searchDiv" style="display:none">
            <div class="col-sm-12">
                <div class="page-header">
                    <h1>搜索商户</h1>
                </div>
                <form class="form-horizontal" id="actionForm" method="post" action="partners/index">
                    <input id="isSearch" name="isSearch" type="hidden" value="1">
                    <div class="form-group form-inline">
                        <#if partner.partnerType?? && partner.partnerType != "PTYPE_AGENCY" && partner.partnerType!="PTYPE_OPERATE">
                            <div class="col-xs-6 col-md-4">
                                <label class="control-label">商户ID号：</label>
                                <input type="text" name="partner_id" id="partner_id" class="form-control input-sm" value="<#if partner?? >${partner.partnerId}</#if>">
                            </div>
                        </#if>
                        <#if partner.partnerType?? && partner.partnerType!="PTYPE_OPERATE">
                            <div class="col-xs-6 col-md-4">
                                <label class="control-label">代理商ID号：</label>
                                <input type="text" name="topPartnerId" id="topPartnerId" class="form-control input-sm" value="<#if partner?? >${partner.topPartnerId}</#if>">
                            </div>
                        </#if>
                    </div>
                    <div class="form-group form-inline">
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">商户名称：</label>
                            <input type="text" name="partner_name" id="partner_name" class="form-control input-sm" value="<#if partner?? >${partner.partnerName}</#if>">
                        </div>
                        <div class="col-xs-6 col-md-4">
                            <label class="control-label">商户状态：</label>
                            <select class="form-control input-sm" name="status" id="status">
                                <option value="">==商户状态==</option>
                                <option value="-1" <#if partner?? && partner.status?? && partner.status==-1>selected</#if>>关闭</option>
                                <option value="0" <#if partner?? && partner.status?? && partner.status==0>selected</#if>>待开通</option>
                                <option value="1" <#if partner?? && partner.status?? && partner.status==1>selected</#if>>开通</option>
                            </select>
                        </div>
                    </div>
                    <input type="hidden" name="partner_type" id="partner_type"  value="${partner_type}">
                    <button type="submit" class="btn btn-default">搜索</button>
                </form>
            </div>
        </div>

    <div id="hideExportDiv" class="row searchDiv" style="display:none">
        <div class="col-sm-12">
            <div class="page-header">
                <h1>导出商户</h1>
            </div>
            <form class="form-horizontal" id="actionForm" method="post" action="partners/exportPartners">
                <input id="isSearch" name="isSearch" type="hidden" value="1">
                <div class="form-group form-inline">
                    <div class="col-xs-6 col-md-4">
                        <label class="control-label">按类型：</label>
                        <select class="form-control input-sm" name="isTraded" id="isTraded">
                            <option value="0" <#if isTraded??&&isTraded=='0'>selected</#if>>未产生交易</option>
                            <option value="1" <#if isTraded??&&isTraded=='1'>selected</#if>>产生交易</option>
                        </select>
                    </div>
                    <div class="col-xs-5 col-md-5">
                        <label class="control-label">起止时间：</label>
                        <input type="text" name="startTime" readonly id="startTime" class="form-control input-sm" value="${startTime}">
                        <input type="text" name="endTime"   readonly id="endTime"   class="form-control input-sm" value="${endTime}">
                    </div>
                </div>
                <input type="hidden" name="partner_type" id="partner_type"  value="${partner_type}">
                <button type="submit" class="btn btn-default">导出</button>
            </form>
        </div>
    </div>

    <!-- list-div start-->
    <div class="row list-toolbar">
        <div class="col-sm-6">
            <button id="searchHandle." onclick="toggleSearchDiv();hideExportDiv();"  type="button" class="btn btn-primary btn-sm">搜索商户</button>
            <#if partner.partnerType?? && partner.partnerType=="PTYPE_PARTNER">
                <button id="exportHandle." onclick="toggleExportDiv();"  type="button" class="btn btn-primary btn-sm">导出商户</button>
            </#if>
        </div>
        <div class="col-sm-6">
            <nav class="pages pull-right">
                <ul id="testpage" class="pagination pagination-sm">${pages}</ul>
            </nav>
        </div>
    </div>
<div class="row">
    <table class="table table-hover">
        <thead>
        <tr>
            <th>商户ID</th>
            <th>商户名称</th>
            <th>账户余额（元）</th>
            <th>冻结金额（元）</th>
            <th>最后交易时间</th>
            <th>商户状态</th>
            <th>API权限状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
            <#if lists?? && lists?size&gt;0>
                <#list lists as list>
                <tr id="userid-${list.PTID}" overstyle="on">
                    <td title="${list.partnerId}">
                        <#if list.partnerId?? && list.partnerId?length lt 13>
                            ${list.partnerId}
                        <#else>
                            <span class="text-info">${list.partnerId?substring(12,list.partnerId?length)}</span>
                        </#if>
                    </td>
                    <td>
                        <#if list.partnerInfo??&&list.partnerInfo?starts_with("{")>
                            <#assign j="${list.partnerInfo}"?eval/>
                            <#if j.skype_name??>
                                <a href="skype:${j.skype_name}?chat" target="_blank">${list.partnerName}<i class="fa fa-skype" aria-hidden="true"></i></a>
                            <#else>
                                ${list.partnerName}
                            </#if>
                        <#else>
                            ${list.partnerName}
                        </#if>
                    </td>
                    <td class="text-danger"><#if list.balance??>￥${list.balance?string(",##0.00")}<#else>￥0</#if></td>
                    <td>￥${list.freezeBalance?string(",##0.00")}</td>
                    <td><#if list.lastTrade??>${list.lastTrade?string('yyyy-MM-dd HH:mm:ss')}<#else>---</#if></td>
                    <td><#-- 待开通 已开通 黑名单用户 -->
                        <#if list.status==0 || list.status==-1 || list.status==-2> <span class="label label-danger">已冻结</span>
                        <#elseif list.status==1><span class="label label-success">已开通</span>
                        <#else><span class="label label-primary">其他</span>
                        </#if>
                    </td>
                    <td ><#-- 待开通 已开通 黑名单用户 -->
                        <#if list.apiStatus==0 || list.apiStatus==-1 || list.apiStatus==-2> <span class="label label-default">已关闭</span>
                        <#elseif list.apiStatus==1><span class="label label-success">已开通</span>
                        <#else><span class="label label-primary">其他</span>
                        </#if>
                    </td>
                    <td>
                        <a title="开通产品" class="toedit" href="partners/productList?partner_id=${list.partnerId}">开通产品</a> |
                        <a title="编辑会员信息" class="toedit" href="partners/toEdit?ptid=${list.ptid}">编辑</a>
                    </td>
                </tr>
                </#list>
            <tr style="background:#C1FFC1">
                <td colspan="8">

                        <button class="btn btn-primary btn-sm" onclick="getTotal()" type="button" id="totalButton">商户余额汇总</button>
                        <div id="totalDiv"  style="display: none;">
                            余额汇总结果：
                        <span class="text-success" id="open_total"></span>&nbsp;&nbsp;
                        <span class="text-primary" id="unopen_total"></span>&nbsp;&nbsp;
                        <span class="text-danger" id="closed_total"></span>&nbsp;&nbsp;
                    </div>
                </td>
            </tr>
            <#else>
            <tr class="no-data">
                <td colspan="10"><em>很抱歉,未查询到任何数据信息!</em></td>
            </tr>
            </#if>
        </tbody>
    </table>
<#if inajax?? &&  inajax=="0">
    </div>
	</section>
<#include "../footer.ftl">
</#if>
<script type="text/javascript">
    function getTotal(){
        var form =$("#actionForm");
        var param = form.serialize();
       $.ajax({
            url:"partners/getTotal",
            type:"POST",
            data:param,
            dataType:"json",
           success:function (data) {
                var result = data.resultData;
               $("#open_total").html("已开通商户："+parseFloat(result.open_total).toFixed(2)+" 元");
               $("#unopen_total").html("未开通商户："+parseFloat(result.unopen_total).toFixed(2)+" 元");
               $("#closed_total").html("已关闭商户: "+parseFloat(result.closed_total).toFixed(2)+" 元");
               $("#totalDiv").css("display","inline");
           },
           error:function () {
               $.alert('系统异常', '系统提示');
           }
        });
    }
    function hideExportDiv(){
        $("#hideExportDiv").hide();
    }
    function toggleExportDiv(){
        $("#hideExportDiv").toggle();
        $("#hideSearchDiv").hide();
    }
</script>
</#escape>