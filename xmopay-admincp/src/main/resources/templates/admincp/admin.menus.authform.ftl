
<#--该文件未被使用-->
<#include "../header.ftl">
<div class="title-tab">
	<ul>
		<li><span class="on">菜单赋权</span></li>
		<li><a href="user/menuList">菜单列表</a></li>
		<li><a href="user/ztree">菜单设置</a></li>
	</ul>
</div>
<div class="formDiv">
		<form action="user/auth_role" onsubmit="return addmenu();">
		<dl id="parent_e">
		  <dt>选择用户角色：</dt>
		  <dd>
		  	<select id="role_select" multiple="multiple" size="10" style="width:200px;height:150px;">
		  		<#list roleList as role>
		  			<option value="${role.RID}">${role.ROLENAME}</option>
		  		</#list>
		  	</select>
			 
		  	<span class="notice">
		  		<input type="button" onclick="move_role('add');" value="&gt;&gt;">
		  		<input type="button" onclick="move_role('delete');" value="&lt;&lt;">
		  	</span>
		  	
		  	<select id="role_sure" multiple="multiple" size="10" style="width:200px;height:150px;"></select>
		  	
		  	<label class="notice">*</label>
			<span id="rolespan" style="display:none;color:red;"></span>
			
		  </dd>
		</dl>
		
		<dl>
		  <dt>选择菜单栏：</dt>
		  <dd>
	      	<select id="menu_select" multiple="multiple" size="10" style="width:200px;height:200px;">
		  		<#list parentMenu as pm>
		  			<option value="${pm.MENU_ID}">${pm.MENU_NAME}</option>
		  		</#list>
		  	</select>
			
			<span class="notice">
				<input type="button" onclick="move_menu('add');" value="&gt;&gt;">
		  		<input type="button" onclick="move_menu('delete');" value="&lt;&lt;">
			</span>
			
			<select id="menu_sure" multiple="multiple" size="10" style="width:200px;height:200px;">
		  		
		  	</select>
		  	
		  	<label class="notice">*</label>
			<span id="menuspan" style="display:none;color:red;"></span>
		  </dd>
		</dl>
		
		<div class="formBtm">
		  	<input class="formbtn" type="submit" id="btnadd"  value="确定" />
		  	<input class="formbtn" type="reset" value="重置" name="Reset" />
		</div>
		<input type="hidden" id="role" name="role">
		<input type="hidden" id="menu" name="menu">
		</form>
</div>	
</div>
<script type="text/javascript">
	var $j = jQuery.noConflict();
	$j(function(){
		
	});
	function move_role(obj){
		if(obj==''){
			return;
		}
		var oList1=document.getElementById("role_select");
		var oList2=document.getElementById("role_sure");
		var arrOptions=[];
		if(obj=="add"){
			if(oList1.options.length<=0){
				$j("#rolespan").show();
				$j("#rolespan").html("左边已经没有了！");
				//alert("左边已经没有了");
				return;
			}
			if(oList1.selectedIndex<0){
				$j("#rolespan").show();
				$j("#rolespan").html("请选择要添加的项！");
				//alert("请选择要添加的项");
				return;
			}
			for(var i=0;i<oList1.options.length;i++){
				if(oList1.options[i].selected){
					//将当前选中的添加到数组中
					arrOptions.push(oList1.options[i]);
				}
			}
			for(var i=0;i<arrOptions.length;i++){
				//循环把数组中的值添加的oList2
				oList2.appendChild(arrOptions[i]);
			}
		}else if(obj=="delete"){
			if(oList2.options.length<=0){
				$j("#userspan").show();
				$j("#userspan").html("右边已经没有了！");
				//alert("右边已经没有了");
				return;
			}
			if(oList2.selectedIndex<0){
				$j("#userspan").show();
				$j("#userspan").html("请选择要删除的项！");
				//alert("请选择要删除的项");
				return;
			}
			for(var i=0;i<oList2.options.length;i++){
				if(oList2.options[i].selected){
					arrOptions.push(oList2.options[i]); 
				}
			}
			for(var i=0;i<arrOptions.length;i++){
				oList1.appendChild(arrOptions[i]);
			}
		}
	}
	function move_menu(obj){
		if(obj==''){
			return;
		}
		var oList1=document.getElementById("menu_select");
		var oList2=document.getElementById("menu_sure");
		var arrOptions=[];
		if(obj=="add"){
			if(oList1.options.length<=0){
				alert("左边已经没有了");
				return;
			}
			if(oList1.selectedIndex<0){
				alert("请选择要添加的项");
				return;
			}
			for(var i=0;i<oList1.options.length;i++){
				if(oList1.options[i].selected){
					arrOptions.push(oList1.options[i]);
				}
			}
			for(var i=0;i<arrOptions.length;i++){
				oList2.appendChild(arrOptions[i]);
			}
		}else if(obj=="delete"){
			if(oList2.options.length<=0){
				alert("右边已经没有了");
				return;
			}
			if(oList2.selectedIndex<0){
				alert("请选择要删除的项");
				return;
			}
			for(var i=0;i<oList2.options.length;i++){
				if(oList2.options[i].selected){
					arrOptions.push(oList2.options[i]); 
				}
			}
			for(var i=0;i<arrOptions.length;i++){
				oList1.appendChild(arrOptions[i]);
			}
		}
	}
	function addmenu(){
		var role="";
		var role_sure=document.getElementById("role_sure");
		if(role_sure.options.length<=0){
			$j("#rolespan").show();
			$j("#rolespan").html("你没有选择用户角色！");
			return false;
		}
		for(var i=0;i<role_sure.options.length;i++){
			role+=role_sure.options[i].value+",";
		}
		$j("#role").val(role);
		var menu="";
		var menu_sure=document.getElementById("menu_sure");
		if(menu_sure.options.length<=0){
			$j("#menuspan").show();
			$j("#menuspan").html("你没有选择菜单！");
			return false;
		}
		for(var i=0;i<menu_sure.options.length;i++){
			menu+=menu_sure.options[i].value+",";
		}
		$j("#menu").val(menu);
		return true;
	}
</script>
<#include "../footer.ftl">