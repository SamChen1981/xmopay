<#escape x as (x)!>
	<#include "../header.ftl">
<script type="text/javascript" src="sysadmin/public/js/form.list.js" charset="utf-8"></script>
<form action="finance/currate" name="pagefrm" method="post">
    <input type="hidden" name="pageIndex" id="pageIndex" value="${page.getPageIndex()}" />
</form>
<div class="title-tab">
	<ul>
		<li><span class="on"><a href="finRate/currate">货币汇率（单位：人民币/100外币）</a></span></li>
        <li><span><a href="finRate/edit">添加货币</a></span></li>
	</ul>
</div>
<div id="listDiv">
	<!-- list-toolbar start-->
	<div class="list-toolbar">
		<div id="toppage" class="page right">正在载入分页...</div>
        <a class="red2" href="http://www.boc.cn/sourcedb/whpj/index.html" target="_blank">今日最新中国银行汇率 中行外汇牌价查询</a>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<a href="javascript:void(0);" onclick="synBankExchange()">[同步中国银行实时汇率]</a>
	</div>
	<!-- list-toolbar end--> 
	<!-- list-div start-->
	<div class="list">
		<table cellspacing="0"  border="0" width="100%">
			<tbody>
				<tr>
					<th>货币代码</th>
					<th>货币名称</th>
					<th>当前价格</th>
					<th>上次价格</th>
					<th>更新时间</th>
					<th>状态</th>
                    <th>操作</th>
				</tr>
				<#if financeRateList??>
				<#list page.elements as list>
					<tr overstyle="on">
						<td>${list.CURRENCY_CODE}</td>
						<td>${list.CURRENCY_NAME}</td>
						<td>${list.CURRENCY_PRICE?string("#.####")} </td>
						<td>${list.LAST_CURRENCY_PRICE?string("#.####")} </td>
						<td>${list.UPDATE_TIME}</td>
						<td>
							<#if list.STATUS==-1><span class="blue">未启用</span>
							<#elseif list.STATUS==1><span class="green">启用</span>
							</#if>
						</td>
                        <td><a href="finRate/edit?frid=${list.FRID}">更新</a></td>
					</tr>
				</#list>
				<#else>
			    <tr class="no-data">
			        <td colspan="7"><em>很抱歉,未查询到任何数据信息!</em></td>
			    </tr>
			    </#if>
			</tbody>
		</table>
	</div>
	<!-- list-div end -->
	<!-- list-toolbar start-->
	<div class="list-toolbar">
		<div id="btmpage" class="page right">正在载入分页...</div>
	</div>
	<!-- list-toolbar end--> 
</div>
<#include "../footer.ftl">

<script type="text/javascript">
	<!-- 分页处理;  start;-->
	<#if financeRateList?? && financeRateList?size&gt;0>
		<#if page.elements??>
        $("#toppage").html(sync_Page(${page.getPageIndex()},${page.getPageNumber()}) + "共" + ${page.getTotal()}+"条");
        $("#btmpage").html(sync_Page(${page.getPageIndex()},${page.getPageNumber()}) + "共" + ${page.getTotal()}+"条");
        </#if>
	<#else>
		$("#toppage").html("");
		$("#btmpage").html("");
	</#if>
	<!-- 分页处理 end-->

	function synBankExchange(){
        Boxy.loading("<font class='red'>数据同步中，请稍候...</font>", {title:'系统提示'});
        $.ajax({
            type: "post",
            url: "finRate/synBankExchange",
            data: null,
            dataType : "json",
            async:false,
            success: function(data){
                Boxy.hideLoading();
                if(data.result=='1'){
                    Boxy.alert("<font class='green'>同步成功!</font>", function(){location.reload();}, {title: "系统提示"});
                }else{
                    Boxy.alert("<font class='red'>同步失败!</font>", null, {title: "系统提示"});
                }
            },
            error: function(){
                Boxy.hideLoading();
                Boxy.alert("<font class='red'>系统错误!</font>", null, {title: "系统提示"});
            }
        });
	}
</script>
</#escape>