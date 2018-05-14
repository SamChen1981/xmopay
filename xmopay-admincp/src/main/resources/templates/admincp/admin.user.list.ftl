<#escape x as (x)!>
<#if inajax=="0">
	<#include "../header.ftl">
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li class="active"><a href="user/adminUserList">用户列表</a></li>
            <li><a href="user/toEditUser">添加用户</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section id="listDiv" class="content-body">
</#if>
    <!-- search div start -->
    <div id="hideSearchDiv" class="row searchDiv" style="display:none">
        <div class="col-sm-12">
			<div class="page-header">搜索会员</div>
			<form class="form-horizontal" method="post" action="user/adminUserList">
				<input id="isSearch" name="isSearch" type="hidden" value="1">
				<div class="form-group form-inline">
                    <div class="col-sm-6 col-md-3">
                    <label class="control-label">用户姓名：</label>
						<input type="text" name="username" id="username" class="form-control input-sm" value="${username}">
                    </div>
                    <div class="col-sm-6 col-md-3">
                    <label class="control-label">用户编号：</label>
						<input type="text" name="muid" id="muid" class="form-control input-sm" value="${muid}">
					</div>
                </div>
                <div class="form-group form-inline">
                    <div class="col-sm-2 col-md-3">
                        <button type="submit" class="btn btn-default">搜索</button>
                    </div>
                </div>

			</form>
		</div>
    </div>

    <!-- search div end -->
    <div class="row list-toolbar">
        <div class="col-sm-6">
            <button id="searchHandle." onclick="toggleSearchDiv();"  type="button" class="btn btn-primary btn-sm">搜索会员</button>
        </div>
        <div class="col-sm-6">
            <nav class="pages pull-right">
                <ul id="testpage" class="pagination pagination-sm">
				${pages}
                </ul>
            </nav>
        </div>
    </div>

    <div class="row">
        <table class="table table-hover">
            <thead>
				<tr>
                    <th>用户编号</th>
					<th>用户角色</th>
					<th>登录名</th>
					<th>最后一次登录IP</th>
					<th>最后一次登录时间</th>
					<th>用户状态</th>
					<th>操作</th>
				</tr>
			</thead>
            <tbody>
				<#if lists?? && lists?size&gt;0>
					<#list lists as list>
						<#if list.userName != 'paycloud'>
                        <tr id="userid-${list.muId}" overstyle="on">
                            <td>${list.muId}</td>
                            <td title="角色ID:${list.roleId}">${list.adminRoleDto.roleName}</td>
                            <td>${list.userName}</td>
                            <td>${list.lastIp}</td>
                            <td>${list.lastLogin?string('yyyy-MM-dd HH:mm:ss')}</td>
                            <td>
								<#if list.status==0><span class="red">锁定</span>
								<#elseif list.status==1><span class="green">正常</span>
								</#if>
                            </td>
                            <td>
								<#if sessionAdminUserInfo.roleId==1>
                                    <a title="编辑用户信息" class="toedit" href="user/toEditUser?muid=${list.muId?c}">编辑</a>
								<#elseif sessionAdminUserInfo.muId == list.muId>
                                    <a title="编辑用户信息" class="toedit" href="user/toEditUser?muid=${list.muId?c}">编辑</a>
								<#else>
                                    -----
								</#if>
                            </td>
                        </tr>
						</#if>
					</#list>
				<#else>
                <tr class="no-data">
                    <td colspan="9"><em>很抱歉,未查询到任何数据信息!</em></td>
                </tr>
				</#if>
			</tbody>
        </table>
<#if inajax=="0">
    </div>
</section>
<#include "../footer.ftl">
</#if>
<script></script>
</#escape>