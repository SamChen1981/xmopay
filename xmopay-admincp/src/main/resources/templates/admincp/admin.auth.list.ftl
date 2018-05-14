<#--该文件未被使用-->
<#escape x as (x)!>
	<#include "../header.ftl">
<div id="search_div" style="display:none">
	<div class="title">搜索会员 </div>
	<div class="formDiv2">
		<form action="user/authList" name="pagefrm" method="post">
			<div class="formBtm">
				<input type="hidden" name="pageIndex" id="pageIndex" value="${page.getPageIndex()}" />
			  	<input type="submit" value="确定" class="formbtn">
			</div>
		</form>
	</div>
</div>
<div class="title-tab">
	<ul>
		<li><span class="on"><a href="user/authList">权限列表</a></span></li>
		<li><a href="user/to_auth_role">添加权限</a></li><!-- authAction!to_addAuth.jhtml -->
	</ul>
</div>
<div id="listDiv">
	<!-- list-toolbar start-->
	<div class="list-toolbar">
		<div id="toppage" class="page right">正在载入分页...</div>
		<!-- <a id="delid" class="btn-a" href="javascript:void(0);" onclick="ui.confirm(this, '你确定删除吗？')" callback="drop('delid')" dropurl="authAction!deleteAuth.jhtml" ajaxcallurl="authAction!index.jhtml"  title="删除权限"><span>删除权限</span></a> -->
	</div>
	<!-- list-toolbar end--> 
	<!-- list-div start-->
	<div class="list">
		<table cellspacing="0"  border="0" width="100%">
			<tbody>
				<tr>
					<th style="width:30px;">&nbsp;</th>
					<th>角色id | 名称</th>
					<th>菜单id | 名称</th>
					<th>操作</th>
				</tr>
				<#if list??>
				<#list page.elements as list>
				<tr id="userid-0" overstyle="on">
					<td>&nbsp;</td>
					<td>${list.ROLE_ID} | ${list.ROLENAME}</td>
					<td>${list.MENU_ID} | ${list.MENU_NAME}</td>
					<td>
						<a title="删除" class="toedit" href="user/delete?role_id=${list.ROLE_ID}&menu_id=${list.MENU_ID}">删除</a>
					</td>
				</tr>
				</#list>
				<#else>
			    <tr class="no-data">
			        <td colspan="15"><em>很抱歉,未查询到任何数据信息!</em></td>
			    </tr>
			    </#if>
			</tbody>
		</table>
	</div>
	<!-- list-div end -->
	<!-- list-toolbar start-->
	<div class="list-toolbar">
		<div id="btmpage" class="page right">正在载入分页...</div>
		<!-- <a class="btn-a" href="javascript:void(0);" onclick="ui.confirm(this, '你确定删除吗？')" callback="drop('delid')" title="删除角色"><span>删除角色</span></a> -->
	</div>
	<!-- list-toolbar end--> 
</div>
<#include "../footer.ftl">

<script type="text/javascript">
	<!-- 分页处理;  start;-->
	<#if list?? && list?size&gt;0>
	<#if page.elements??>
		$("#toppage").html(sync_Page(${page.getPageIndex()},${page.getPageNumber()}));
		$("#btmpage").html(sync_Page(${page.getPageIndex()},${page.getPageNumber()}));
	</#if>
	<#else>
		$("#toppage").html("");
		$("#btmpage").html("");
	</#if>
	<!-- 分页处理 end-->
</script>
</#escape>