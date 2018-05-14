<#escape x as (x)!>

		<#include "header.ftl">
    <!-- header start -->
    <section class="content-header">
        <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
                <li class="active"><a href="/protal/welcome">概况信息</a></li>
            </ul>
        </div>
    </section>
    <!-- header end -->

    <section id="listDiv" class="content-body">
    <div class="row">
        <div class="col-sm-12">
            <div class="page-header">
                <h1>最新消息:</h1>
            </div>
            <div class="alert alert-success" role="alert">
                温馨提示：你好，<#if sessionAdminUserInfo??>${sessionAdminUserInfo.userName}</#if>！欢迎您登录后台管理中心，不建议使用360浏览器。你当前使用的浏览器是：<label id="onlog" style="color:red"></label></span>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-12">
            <div class="page-header">
                <h1>系统信息:</h1>
            </div>
            <div class="form-group form-inline" role="alert">
                浏览器是: <span id="inlog" class="text-danger"></span>
            </div>
            <div class="form-group form-inline" role="alert">
                操作系统: <span class="text-danger" id="std" ></span>
            </div>
        </div>
    </div>

	<#include "footer.ftl">


</#escape>

<script>

	function initMenus(){
        $.confirm({
            title: "系统提示",
            content:"请初始化菜单栏",
            buttons:{
                ok: function(){
                    $.post('protal/initMenus',{"userName":"${sessionAdminUserInfo.userName}"},
                        function(data){
                            if(data.resultCode == "RESP_SUCCESS"){
                                $.confirm({
                                    title: "系统提示",
                                    content:"成功初始化菜单栏",
                                    buttons: {
                                        ok: function () {
                                            $.setCookie("initMenus","1");
                                            setTimeout("window.reload()",'1000');
                                        }
                                    }
                                })
                            }else{
                                $.alert("初始化失败！");
                            }
                        },'JSON');
                    }
            }
        })
    }

	onlogding();
	function onlogding(){
		var userbrowser=navigator.userAgent;
		$("#onlog").html("("+version_type(userbrowser)+")");
        $("#inlog").html("("+version_type(userbrowser)+")");
        $("#btd").html(version_type(userbrowser));
		// alert(isMozilla=navigator.userAgent.indexOf("Gecko/")>0);

		var usersystem=navigator.platform;
		$("#std").html(system_type(usersystem));
	}
	function version_type(userbrowser){
		var version="未识别";
		if(userbrowser.indexOf("MSIE")>0){
			version="IE浏览器";
		}
		if(userbrowser.indexOf("Firefox")>0){
			version="火狐浏览器";
		}
		if(userbrowser.indexOf("Chrome")>0){
			version="Google Chrome浏览器";
		}
		if(userbrowser.indexOf("Safari")>0  && userbrowser.indexOf("Chrome")<0){
			version="Apple Safari浏览器";
		}
		if(userbrowser.indexOf("Opera")>0){
			version="Opera浏览器";
		}
		if(userbrowser.indexOf("360SE")>0){
			version="360浏览器";
		}
		return version;
	}
	function system_type(usersystem){
		var version="未识别";
		if((usersystem == "Win32") || (usersystem == "Windows")){
			version = "Windows";
		}
		if((usersystem == "Mac68K") || (usersystem=="MacIntel") || (usersystem == "MacPPC") || (usersystem == "Macintosh")){
			version = "Mac";
		}
		if((usersystem == "X11") && !isWin && !isMac){
			version = "Unix";
		}
		return version;
	}
</script>
<#--</#escape>-->