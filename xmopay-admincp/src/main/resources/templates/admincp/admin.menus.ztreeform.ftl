<#escape x as (x)!>
	<#include "../header.ftl">
<link rel="stylesheet" href="static/plugins/jquery-ztree/css/metroStyle/metroStyle.css" type="text/css">
<script type="text/javascript" src="static/plugins/jquery-ztree/js/jquery.ztree.core.min.js"></script>

<!-- 右键树 -->
<div id="rMenu" style="display: none; z-index:1000; position:absolute; ">
    <button id="m_del"  class="btn-default btn-sm btn">删除</button>
</div>

<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li class="active"><a href="user/ztreeMenus"> 菜单设置 </a></li>
            <li><a href="javascript:" onclick="window.parent.top.location.reload();">重新载入菜单</a></li>
        </ul>
    </div>
</section>
<!-- header end -->
<section class="content-body">
    <div class="row form">
		<div class="col-sm-2"
             style="width:250px;height:500px;float:left;overflow-x:hidden;border:1px solid #27ae60;">
			<span><b>模块菜单：</b>[<a onclick="return false;" title="全部展开" href="javascript:" id="expandAllBtn">展开</a>] [<a onclick="return false;" title="全部关闭" href="javascript:" id="collapseAllBtn">关闭</a>]</span>
			<ul id="tree" class="ztree"></ul>
		</div>
		<div class="col-sm-4">
            <form id="partnersForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="user/updateMenus">
				<div class="form-group">
					<label class="col-sm-4 control-label">菜单代码：</label>
					<div class="col-sm-8">
						<input class="form-control" id="menucode" name="menu_code" type="text" value=""/>
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-4 control-label">菜单名称：</label>
					<div class="col-sm-8">
						<input class="form-control" id="menuname" name="menu_name" type="text" value=""/>
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-4 control-label">菜单标志：</label>
					<div class="col-sm-8">
						<select class="form-control" id="menuflag" name="menu_flag" onchange="parent_e(this.value);">
							<option value="0">节点</option>
							<option value="1">叶子</option>
						</select>
					</div>
				</div>

				<div id="parent_e" class="form-group" style="display:none">
					<label class="col-sm-4 control-label">上级菜单：</label>
					<div class="col-sm-8">
						<select class="form-control" id="menuparent" name="menu_parent">
							<option value="">请选择上级菜单</option>
							<#if parentMenu??>
								<#list parentMenu as pm>
									<option value="${pm.menuId}">${pm.menuName}</option>
								</#list>
							</#if>
						</select>
					</div>
				</div>
				<div id="parent_u" class="form-group" style="display:none">
					<label class="col-sm-4 control-label">菜单URL：</label>
					<div class="col-sm-8">
						<input class="form-control" id="menuurl" name="menu_url" type="text" size="60" value="" />
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-4 control-label">设置常用菜单：</label>
					<div class="col-sm-8">
						<select class="form-control" id="status" name="status">
							<option value="1">是</option>
							<option value="0">否</option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label">菜单显示顺序：</label>
					<div class="col-sm-8">
						<input class="form-control" id="displayorder" name="displayorder" type="text"  maxlength="45" value="" />
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-4 control-label">菜单说明：</label>
					<div class="col-sm-8">
						<textarea class="form-control" cols="50" rows="5" id="menudesc" name="menu_desc">${am.menuDesc}</textarea>
					</div>
				</div>

				<div class="form-group">
					<div class="col-sm-offset-4 col-sm-12">
						<input type="hidden" id="menuid" name="menu_id" value="">
						<button type="submit" id="blackSubmit" class="btn btn-primary">确定</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</section>
<#include "../footer.ftl">
<script type="text/javascript">
    function parent_e(type){
        if(type==1){
            $("#parent_e").show();
            $("#parent_u").show();
        }else{
            $("#parent_e").hide();
            $("#parent_u").hide();
        }
    }

    var setting = {
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
            onClick: zTreeOnClick,
            onRightClick: OnRightClick
        }
    };

    var zNodes =[
		<#if seedMenu??>
			<#list seedMenu as pm>
                { id:${pm.menuId}, pId:${pm.menuParent}, name:"${pm.menuName}", open:true, click:true},
			</#list>
		</#if>
    ];

    $(document).ready(function(){
        $.fn.zTree.init($("#tree"), setting, zNodes);
        var treeObj = $.fn.zTree.getZTreeObj("tree");

        $("#expandAllBtn").bind("click", {type:"expandAll"}, expandNode);
        $("#collapseAllBtn").bind("click", {type:"collapseAll"}, expandNode);
    });

    function zTreeOnClick(event, treeId, treeNode) {
        var mid = treeNode.id;
        $.get("user/getZtreeById", {mid:mid},function(data){
            if(data==null || data=="") {
                $.alert('系统错误！', '系统提示');
                return false;
            }else{
                if(data.resultCode=="RESP_SUCCESS"){
                    for(i in data.resultData){
                        if(i=="menuFlag"){
                            if(eval('data.resultData.'+i)==1){
                                $("#parent_e").show();
                                $("#parent_u").show();
                            }else{
                                $("#parent_e").hide();
                                $("#parent_u").hide();
                            }
                        }
                        if(i=="menuFlag" || i=="status" || i=="menuParent"){
                            $("#"+i.toLowerCase()+" option").removeAttr("selected"); //移除属性selected
                            var mVal = eval('data.resultData.'+i);
                            $("#"+i.toLowerCase()+" option[value='"+mVal+"']").attr("selected", true);
                        }else {
                            console.log('#' + i.toLowerCase());
                            $('#' + i.toLowerCase()).val(eval('data.resultData.' + i));
                        }
                      /*  else {
                            console.log('#' + i.toLowerCase());
                            $('#' + i.toLowerCase()).attr("value", eval('data.resultData.' + i));
                        }*/
                    }
                }else{
                    ui.error('数据加载失败');
                }
            }
        }, 'json');
    }
    //展开树形
    function expandNode(e) {
        var zTree = $.fn.zTree.getZTreeObj("tree"), type = e.data.type;
        if (type == "expandAll") {
            zTree.expandAll(true);
        } else if (type == "collapseAll") {
            zTree.expandAll(false);
        }
    }

    /* ----------------- 右键删除菜单 ---------------------   */
    //右键方法
    function OnRightClick(event, treeId, treeNode) {
        if(treeNode.id == 0){  return; }
        console.log(event.clientX);
        console.log(event.clientY);
        $("#rMenu").show();
        $("#rMenu").css("left",document.body.scrollLeft+event.clientX+1);
        $("#rMenu").css("top", document.body.scrollLeft+event.clientY+10);
        var mid = treeNode.id;

        $("#rMenu").off("click");//不加此解绑操作，可能会出现点击一次按钮触发多次事件
        $("#rMenu").on('click',function(){
            delZtreeById(mid);
        });

    }

    function delZtreeById(mid){
        $("#rMenu").hide();
        $.confirm({
            title: '系统提示',
            content: "<p class='text-success'>菜单正在使用，请谨慎操作！</p>",
            buttons: {
                ok: function () {
                    $.post("user/delZtreeById", {mid: mid}, function(data){
                        if(data==null || data=="") {
                            $.alert('系统错误！', '系统提示');
                            return false;
                        }
                        if(data.resultCode=="RESP_SUCCESS"){
                            $.confirm({
                                title: '系统提示',
                                content: "<p class='text-success'>操作成功！</p>",
                                buttons: {
                                    ok: function () {
                                        setTimeout("window.location.href='${base}user/ztreeMenus'","1000");
                                    }
                                }
                            })
                        }else{
                            $.alert(data.resultMsg, '系统提示');
                        }
                    }, 'json');
                },
                cancel: function () {

                }
            }
        });

    }


    function onBodyMouseDown(event) {
        if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length > 0)) {
            rMenu.css({ "visibility": "hidden" });
        }
        if (!(event.target.id == "rMenu2" || $(event.target).parents("#rMenu2").length > 0)) {
            $("#rMenu2").css({ "visibility": "hidden" });
        }
    }


    /* ----------------- 右键删除菜单 ---------------------   */
</script>
<script type="text/javascript">
    $(document).ready(function() {
        //鼠标点击事件不在节点上时隐藏右键菜单
        $("body").on('click',function(){
            $("#rMenu").hide();
        });

        $('#partnersForm')
            .formValidation(
                {
                    fields: {
                        menu_code: {
                            row: '.col-sm-8',
                            validators: {
                                notEmpty: {
                                    message: '请输入菜单代码'
                                }
                            }
                        },
                        menu_name: {
                            row: '.col-sm-8',
                            validators: {
                                notEmpty: {
                                    message: '请输入菜单名称'
                                }
                            }
                        },
                        displayorder: {
                            row: '.col-sm-8',
                            validators: {
                                notEmpty: {
                                    message: '请输入菜单显示顺序'
                                },
                                regexp: {
                                    regexp: /\d+/,
                                    message: '显示顺序只能为数字'
                                }
                            }
                        },
                        menu_desc: {
                            row: '.col-sm-8',
                            validators: {
                                notEmpty: {
                                    message: '请输入菜单说明'
                                }
                            }
                        }
                    }
                }
            )
            .on('success.form.fv', function(e) {
                // Prevent form submission
                e.preventDefault();

                // Some instances you can use are
                var $form = $(e.target),        // The form instance
                        fv    = $(e.target).data('formValidation'); // FormValidation instance

                // Use Ajax to submit form data
                $.ajax({
                    url: $form.attr('action'),
                    type: 'POST',
                    data: $form.serialize(),
                    dataType:"json",
                    beforeSend: function () {
                        // 禁用按钮防止重复提交
                        $("#blackSubmit").attr({ disabled: "disabled" });
                    },
                    success: function(data) {
                        if(data==null || data==""){
                            $.alert('系统错误！', '系统提示');
                        }
                        if(data.resultCode=="RESP_SUCCESS"){
                            $.confirm({
                                title: '系统提示',
                                content: "<p class='text-success'>操作成功!</p>",
                                buttons: {
                                    ok: function () {
                                        window.location.href = '${base}user/ztreeMenus'
                                    }
                                }
                            });
                        }else{
                            $.alert(data.resultMsg, '系统提示');
                        }
                    },
                    error : function(data) {
                        $.alert('系统异常', '系统提示');
                    }
                });
            });
    });
</script>
</#escape>