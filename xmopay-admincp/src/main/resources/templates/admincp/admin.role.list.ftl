<#escape x as (x)!>
<#if inajax=="0">
<#include "../header.ftl">
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li class="active"><a href="user/roleList">角色列表</a></li>
            <li><a href="user/toEditRole">添加角色</a></li>
        </ul>
    </div>
</section>
<!-- header end -->

<section id="listDiv" class="content-body">
</#if>

    <div class="row list-toolbar">
        <div class="col-sm-6">
            <#if sessionAdminUserInfo.roleId==1>
                <button id="deleteHandle" type="button" class="btn btn-primary btn-sm">删除</button>
            </#if>
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
                <th style="width:30px;">
                    <input type="checkbox" name="checkb" id="checkbox_handle">
                </th>
                <th>角色ID</th>
                <th>角色名称</th>
                <th>角色描述</th>
                <th>记录时间</th>
                <#if sessionAdminUserInfo.roleId==1>
                    <th>操作</th>
                </#if>
            </tr>
            </thead>
            <tbody>
				<#if lists?? && lists?size&gt;0>
					<#list lists as list>
                    <tr id="userid-${list.rid}" overstyle="on">
                        <td>
                            <#if sessionAdminUserInfo.roleId!=list.rid>
                            <input type="checkbox" name="checkb" value="${list.rid}" class="checkbox" >
                            </#if>
                        </td>
                        <td>${list.rid}</td>
                        <td>${list.roleName}</td>
                        <td>${list.roleDesc}</td>
                        <td>${list.dateLine?string('yyyy-MM-dd HH:mm:ss')}</td>
                        <#if sessionAdminUserInfo.roleId==1>
                            <td>
                                <a title="编辑角色" class="toedit" href="user/toEditRole?rid=${list.rid}">编辑</a>&nbsp;|&nbsp;
                                <a class="toedit" href="user/toAuthMenu?rid=${list.rid}">菜单权限</a>
                            </td>
                        </#if>
                    </tr>
					</#list>
				<#else>
                <tr class="no-data">
                    <td colspan="15"><em>很抱歉,未查询到任何数据信息!</em></td>
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
	$(function(){

		$("#deleteHandle").click(function() {
			var hasChecked = $(".checkbox:checked");
			if (hasChecked.length >= 1) {

                $.confirm({
                    title: '系统提示',
                    content: "<p class='text-danger'>确定要删除所选中项吗？</p>",
                    buttons: {
                        ok: function () {
                            var ids = "";
                            hasChecked.each(function(){
                                ids += this.value +",";
                            });
                            ids = ids.substr(0, (ids.length - 1));
                            if(ids==""){
                                $.alert("<label class='red'>请勾选需要的订单!</label>", "系统提示");
                                return;
                            }

                                $.ajax({
                                    url: "user/deleteRole",
                                    data: {ids : ids},
                                    type: 'POST',
                                    dataType: 'json',
                                    cache: false,
                                    success: function(data, textStatus) {
                                        if(data.resultCode=="RESP_SUCCESS"){
                                            $.confirm({
                                                title: '温馨提示',
                                                content: "<p class='text-success'>"+data.resultMsg+"</p>",
                                                buttons: {
                                                    ok: function () {
                                                        window.location.href = '${base}user/roleList';
                                                    }
                                                }
                                            });
                                        }else {
                                            $.alert(data.resultMsg, '温馨提示');
                                        }
                                    },
                                    error: function(httpRequest, statusText, errorThrown) {
                                        $.alert('系统响应异常!', '温馨提示');
                                    }
                                });
                        },
                        cancel: function(){

                        }
                    }
                });
			}else {
                $.alert('至少选择一个对象!', '温馨提示');
			}
		});
	});
</script>

</#escape>