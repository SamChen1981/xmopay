
<#--该文件未被使用-->
<#escape x as (x)!>
<div>
	<div class="table-box">
	    <table cellpadding="0" cellspacing="0" border="0" width="100%" >
	    <tr>
	    	<td width="80"><h3>用户：</h3></td>
	       	<td ><h3>${username}</h3></td>
	    </tr>
	       <tr>
	       	<td >原密码：</td>
	       	<td ><input type="password" id="password" name="password"></td>
	       </tr>
	       <tr>
	       	<td >新密码：</td>
	       	<td  ><input type="password" id="newpassword" name="newpassword"></td>
	       </tr>
	       <tr>
	       	<td width="80">确认密码：</td>
	       	<td  ><input type="password" id="cnewpassword" name="cnewpassword"></td>
	       </tr>
	       <input type="hidden" id="muid" name="muid" value="${muid}">
	       <input type="hidden" id="username" name="username" value="${username}">
	    </table>
	    <style type="text/css">
	        .ui-button{height:auto;padding:3px 12px;border:1px solid #A8ADB6;cursor:pointer;display:inline-block;font-size:12px;border-radius:2px;overflow:visible;line-height:16px;_line-height:15px;*line-height:14px!important;}
			.ui-button{background-color:#fafafa;background:-moz-linear-gradient(#fff,#f4f4f4);background:-ms-linear-gradient(#fff,#f4f4f4);background:-webkit-gradient(linear,left top,left bottom,color-stop(0%,#fff),color-stop(100%,#f4f4f4));background:-webkit-linear-gradient(#fff,#f4f4f4);background:-o-linear-gradient(#fff,#f4f4f4);filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffff',endColorstr='#f4f4f4');-ms-filter:"progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffff',endColorstr='#f4f4f4')";background:linear-gradient(#fff,#f4f4f4);}
			.ui-button:hover{background:-moz-linear-gradient(#fff,#ddd);background:-ms-linear-gradient(#fff,#ddd);background:-webkit-gradient(linear,left top,left bottom,color-stop(0%,#fff),color-stop(100%,#ddd));background:-webkit-linear-gradient(#fff,#ddd);background:-o-linear-gradient(#fff,#ddd);filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffff',endColorstr='#dddddd');-ms-filter:"progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffff',endColorstr='#dddddd')";background:linear-gradient(#fff,#ddd);}
			.ui-button:active{background:-moz-linear-gradient(#e2e2e2,#f7f7f7);background:-ms-linear-gradient(#e2e2e2,#f7f7f7);background:-webkit-gradient(linear,left top,left bottom,color-stop(0%,#e2e2e2),color-stop(100%,#f7f7f7));background:-webkit-linear-gradient(#e2e2e2,#f7f7f7);background:-o-linear-gradient(#e2e2e2,#f7f7f7);filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#e2e2e2',endColorstr='#f7f7f7');-ms-filter:"progid:DXImageTransform.Microsoft.gradient(startColorstr='#e2e2e2',endColorstr='#f7f7f7')";background:linear-gradient(#e2e2e2,#f7f7f7);}
			.table-box table{ border-collapse:collapse}
			.table-box table td {padding:3px; border-bottom:1px solid #c1c8d2; height:28px; line-height:28px;}
		</style>
    </div>
    <div class="navtitle">
			<input type="button" class="ui-button" value="确定" onclick="determine(${muid});" />
			<input type="button" class="ui-button" value="返回" onclick="Boxy.get(this).hide(); return false" />
	</div>
</div>
<!--endprint-->

<!-- form -->
<script>
	function determine(muid){
	
		var password=$("#password").val();
		var newpassword=$("#newpassword").val();
		var cnewpassword=$("#cnewpassword").val();
		var username=$("#username").val();
		
		if(password==""){
			alert("原密码不能为空");
			return false;
		}
		if(newpassword==""){
			alert("新密码不能为空");
			return false;
		}
		if(cnewpassword==""){
			alert("确认密码不能为空");
			return false;
		}
		
		if(newpassword!=cnewpassword){
			alert("两次输入不一致");
			return false;
		}
		var params={'username':username,'muid':muid,'newpassword':newpassword,'cnewpassword':cnewpassword,'password':password};
		$.ajax({
			type: "post",
			async:false,
			url: "member/newpassword",
			data: params,
			dataType : "json",
			success: function(data, textStatus){
				if(data==null || data==""){
					return false;
				}
				var result = data.result;
				if(result=="1"){
					Boxy.alert("<font class='green' size='4'>"+data.msg+"</font>", function onload(){
						setTimeout("window.location.href = '${base}user/logout'", '100');
					}, {title: "温馨提示"});
					return true;
				}else if(result=="-1"){
					Boxy.alert("<font class='red' size='4'>"+data.msg+"</font>", "温馨提示");
					return false;
				}else{
					alert("系统错误");
					return false;
				}
			},error: function(){
				alert("异步返回结果异常");
				return false;
			}
		});
		return true;
	}
</script>

</#escape>