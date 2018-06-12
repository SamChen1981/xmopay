<#escape x as (x)!>
<!DOCTYPE html>
<html>
<head>
<base href="${base}">
<meta charset="UTF-8">
<title>XMOPAY系统管理中心</title>
<script type="text/javascript" src="static/js/jquery.min.js" charset="utf-8"></script>
<link type="text/css" rel="stylesheet" href="static/css/login.css" charset="utf-8" />
<style>
body{background-color:#223035}
</style>
</head>

<body class="land-bg">
	<div class="modal in" id="login-modal" >
		<p>登录：<span id="msgTips"></span></p>
		<div class="login-form clearfix">
			<input id="username" name="username" readonly type="text" placeholder="帐号：" value="admin" onblur="googlecheck(this.value);" />
			<input id="password" name="password" readonly type="password" placeholder="密码：" value="admin123" />
			<input id="googlecode" name="googlecode" type="text" placeholder="Google安全码：" style="display: none">
			<input type="button" name="type" class="button-blue login" value="登录" onclick="checkLogin()">
		</div>
	</div>
</body>
</html>
<script type="text/javascript">
if (self != top){
    /* 在框架内，则跳出框架 */
    top.location = self.location;
}
//键盘监听
$(document).bind("keydown", function(e){
	//键盘监听
    if(e.keyCode == 13){
	  checkLogin();
	}
});
	
function googlecheck(username){
	$.ajax({
		type: "post",
		url: "user/checkGoogleCode",
		data: {"username":username},
		dataType : "json",
		success: function(data, textStatus){
			if(data==null || data==""){
				$("#msgTips").html("<font color='red'>系统错误</font>").fadeIn();
				return;
			}
			if(data.resultCode=="RESP_SUCCESS"){
				$("#googlecode").show();
			}else{
				$("#googlecode").hide();
			}
		},
		error: function(){
			$("#msgTips").html("<font color='red'>系统异常响应</font>").fadeIn();

		}
	}); 
}

function checkLogin(){
	//$.setCookie('actionHistory', "");
	var _username = $("#username").val();
	var _password = $("#password").val();
	var _googlecode = $("#googlecode").val();

	var chking = true;
	if(_password == '' || _username == ''){
		$("#msgTips").html("<font color='red'>帐户或密码为空！</font>").fadeIn();
		$("#username").focus();
		chking = false;
	}

	if(chking){
		$("#msgTips").html("<font color='green'>正在登录中...</font>").fadeIn();
		$.ajax({
			type: "post",
			url: "user/dologin",
			data: {username:_username,password:hex_md5(_password),googlecode:_googlecode},
			dataType : "json",
			async:false,
			success: function(data){

				if(data.resultCode=="RESP_SUCCESS"){
			        $("#msgTips").html('<font color="green">'+data.resultMsg+'</font>').fadeIn();
			    	setTimeout("window.location.href = '${base}protal/index'", '1000');
			    }else{
			    	$("#msgTips").html('<font color="red">'+data.resultMsg+'</font>').fadeIn();
			        return false  
			    }
			},
			error: function(){
				$("#msgTips").html('<font color="red">系统异常响应！</font>').fadeIn();
				return false;
			}
		}); 		
		return true;
	}
}
</script>
</#escape>