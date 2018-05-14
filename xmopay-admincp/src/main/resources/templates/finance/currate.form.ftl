<#escape x as (x)!>
<#include "../header.ftl">
<script type="text/javascript" src="sysadmin/public/js/jquery.validate.min.js" charset="utf-8"></script>
<script type="text/javascript">
$(function(){
	//绑定操作事件
	$('#btnSubmit').bind("click", fromSubmit);

	validator = $('#actionForm').validate({
		errorPlacement: function(error, element){
			$(element).next('.notice').hide();
			$(element).after(error);
		},
		success : function(label){
			label.addClass('right');
		},
		//onfocusout : false,
		onkeyup    : false,
		rules : {
            currency_name : {
				required : true
			},
            currency_code  :{
				required : true
			},
            currency_price  :{
                required : true
            }
		},
		messages : {
            currency_name : {
				required : '货币名称不能为空'
			},
            currency_code  :{
				required : '货币代码不能为空'
			},
            currency_price  :{
                required : '货币价格不能为空'
            }
		}
	});
});

//AJAX FROM SUBMIT
function fromSubmit(){
	if(validator.form()){
		var form = $('#actionForm');
   		var data = form.serialize().replace(/%2C/g, "");//过滤所有的英文逗号
   		Boxy.loading("<font class='red'>数据处理中，请稍候...</font>",{title:'系统提示'});
   		var ajax = {
	       	url: 'finRate/saveEdit',
	        data: data, 
	        type: 'POST', 
	        dataType: 'json', 
	        cache: false,
	        success: function(json, statusText){
	        	Boxy.hideLoading();
	            if (json.result == "-1"){ 
	            	Boxy.alert("<font class='red'>操作失败!</font>", function(){location.reload();}, {title: "系统提示"});
	            } 
	            else{ //成功
	            	Boxy.alert("<font class='red'>操作成功!</font>", null, {title: "系统提示"});
	            	setTimeout("window.location.href = '${base}finRate/currate'", '1000');
	            }
	        },
	        error: function(httpRequest, statusText, errorThrown){ //错误处理
	        	Boxy.hideLoading();
	        	Boxy.alert("<font class='red'>系统异常!</font>", function(){location.reload();}, {title: "系统提示"});
	        }   			
   		};	
   		//AJAX数据提交
   		$.ajax(ajax);    
	}
}
</script>
<div class="title-tab">
	<ul>
        <li><span><a href="finRate/currate">货币汇率（单位：人民币/100外币）</a></span></li>
        <li><span class="on"><a href="finRate/edit"><#if mark=="to_add">添加</#if><#if mark=="to_edit">修改</#if>货币</a></span></li>
	</ul>
</div>
<div class="formDiv">
	<form id="actionForm" method="post">
		<dl>
		  <dt>货币名称：</dt>
		  <dd>
		  	<input id="frid" name="frid" type="hidden"  maxlength="45" value="${currateMap.FRID}">
		  	<input id="last_currency_price" name="last_currency_price" type="hidden"  maxlength="45" value="${currateMap.CURRENCY_PRICE}">
	      	<input id="currency_name" name="currency_name" type="text" maxlength="45" value="${currateMap.CURRENCY_NAME}" />
			<label class="notice">*</label>
		  </dd>
		</dl>
		
		<dl>
		  <dt>货币代码：</dt>
		  <dd>
		    <input id="currency_code" name="currency_code" type="text" maxlength="45" value="${currateMap.CURRENCY_CODE}" />
			<label class="notice">*</label>
		  </dd>
		</dl>
        <dl>
            <dt>货币价格：</dt>
            <dd>
                <input id="currency_price" name="currency_price" type="text" maxlength="45" value="${currateMap.CURRENCY_PRICE}" />
                <label class="notice">*</label>
            </dd>
        </dl>
        <dl>
            <dt>状态：</dt>
            <dd>
                <input id="status" name="status" type="radio" value="1" <#if mark=="to_edit" && currateMap.STATUS == 1>checked<#else>checked</#if>/>启用
                <input id="status" name="status" type="radio" value="-1" <#if mark=="to_edit" && currateMap.STATUS == -1>checked</#if>/>停用
            </dd>
        </dl>
		<div class="formBtm">
		  	<input class="formbtn" type="button" id="btnSubmit" value="确定" />
		  	<input class="formbtn" type="reset" value="重置" name="Reset" />
		</div>	
	</form>
</div>	
</div>
<#include "../footer.ftl">
</#escape>