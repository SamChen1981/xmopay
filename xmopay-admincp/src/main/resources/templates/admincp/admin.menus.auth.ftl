<#escape x as (x)!>
    <#include "../header.ftl">
<link rel="stylesheet" href="static/plugins/jquery-ztree/css/metroStyle/metroStyle.css" type="text/css">
<#--<link rel="stylesheet" href="sysadmin/public/plugins/jquery-ztree/css/zTreeStyle/zTreeStyle.css" type="text/css">-->
<script type="text/javascript" src="static/plugins/jquery-ztree/js/jquery.ztree.core.min.js"></script>
<script type="text/javascript" src="static/plugins/jquery-ztree/js/jquery.ztree.excheck.min.js"></script>
<#--<script type="text/javascript" src="sysadmin/public/plugins/jquery-ztree/js/jquery.ztree.exedit.js"></script>-->
<script LANGUAGE="Javascript">
    var setting = {
        view: { //表示tree的显示状态
//            addHoverDom: addHoverDom,       // 用于当鼠标移动到节点上时，显示用户自定义控件。务必与 setting.view.removeHoverDom 同时使用
//            removeHoverDom: removeHoverDom, // 用于当鼠标移出节点时，隐藏用户自定义控件。务必与 addHoverDom 同时使用
            selectedMulti: false          //表示禁止多选
        },
        edit: {
            enable: true,
            editNameSelectAll: true
        },
        data: { //表示tree的数据格式
            keep:{
                parent:true,
                leaf:true
            },
            simpleData: {
                enable: true,  //表示使用简单数据模式
                idKey: "id",   //设置之后id为在简单数据模式中的父子节点关联的桥梁
                pIdKey: "pId", //设置之后pid为在简单数据模式中的父子节点关联的桥梁和id互相对应
                rootPId: -1    //pid为null的表示根节点
            }
        },
        check: {  //表示tree的节点在点击时的相关设置
            enable: true, //表示使用简单数据模式
            chkStyle: "checkbox",  //值为checkbox或者radio表示
            chkboxType: { "Y": "ps", "N": "ps" }  //表示父子节点的联动效果  autoCheckTrigger
        },
        callback:{
//            beforeRemove:beforeRemove,//点击删除时触发，用来提示用户是否确定删除
//            beforeEditName: beforeEditName,//点击编辑时触发，用来判断该节点是否能编辑
//            beforeRename:beforeRename,//编辑结束时触发，用来验证输入的数据是否符合要求
//            onRemove:onRemove,//删除节点后触发，用户后台操作
//            onRename:onRename,//编辑后触发，用于操作后台
            onClick:clickNode//点击节点触发的事件
        }
    };
    /**
    function beforeRemove(e,treeId,treeNode){
        return confirm("你确定要删除吗？");
    }
    function onRemove(e,treeId,treeNode){
        if(treeNode.isParent){
            var childNodes = zTree.removeChildNodes(treeNode);
            var paramsArray = new Array();
            for(var i = 0; i < childNodes.length; i++){
                paramsArray.push(childNodes[i].id);
            }
            alert("删除父节点的id为："+treeNode.id+"\r\n他的孩子节点有："+paramsArray.join(","));
            return;
        }
        alert("你点击要删除的节点的名称为："+treeNode.name+"\r\n"+"节点id为："+treeNode.id);
    }
    function beforeEditName(treeId,treeNode){
        if(treeNode.isParent){
            alert("不准编辑非叶子节点！");
            return false;
        }
        return true;
    }
    function beforeRename(treeId,treeNode,newName,isCancel){
        if(newName.length < 3){
            alert("名称不能少于3个字符！");
            return false;
        }
        return true;
    }
    function onRename(e,treeId,treeNode,isCancel){
        alert("修改节点的id为："+treeNode.id+"\n修改后的名称为："+treeNode.name);
    }*/
    function clickNode(event, treeId, treeNode){
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        if(treeNode.checked){
            //取消
            treeObj.checkNode(treeNode, false, false)
        }else{
            //选中
            treeObj.checkNode(treeNode, true, false)
        }
    }

    var zNodes =[
        { id: 0, pId: -1, name: "后台系统菜单", open:true},
        <#if menusList??>
            <#list menusList as pm>
                <#if pm.menuParent == 0>
                    { id:${pm.menuId}, pId:${pm.menuParent}, name:"[${pm.menuId}]${pm.menuName}", open:true},
                <#else>
                    { id:${pm.menuId}, pId:${pm.menuParent}, name:"[${pm.menuId}]${pm.menuName}" },
                </#if>
            </#list>
        </#if>
    ];

    $(document).ready(function(){
        $.fn.zTree.init($("#tree"), setting, zNodes);
        var treeObj = $.fn.zTree.getZTreeObj("tree");
        treeObj.expandAll(true); //true 表示 展开 全部节点
        $("#expandAllBtn").bind("click", {type:"expandAll"}, expandNode);
        $("#collapseAllBtn").bind("click", {type:"collapseAll"}, expandNode);
        <#if myMenusList??>
            <#list myMenusList as me>
                var treeNode = treeObj.getNodeByParam("id", "#{me.menuId}", null);
                treeObj.checkNode(treeNode, true, false);
            </#list>
        </#if>
    });

    //展开树形
    function expandNode(e) {
        var zTree = $.fn.zTree.getZTreeObj("tree"), type = e.data.type;
        if (type == "expandAll") {
            zTree.expandAll(true);
        } else if (type == "collapseAll") {
            zTree.expandAll(false);
        }
    }

    var newCount = 1;
    function addHoverDom(treeId, treeNode) {
        var sObj = $("#" + treeNode.tId + "_span");
        if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) {
            console.log(treeNode.editNameFlag+"----------"+treeNode.isParent);
            return;
        }
        var addStr = "<span class='button add' id='addBtn_" + treeNode.tId + "' title='add node' onfocus='this.blur();'></span>";
        sObj.after(addStr);
        var btn = $("#addBtn_"+treeNode.tId);
        if (btn) btn.bind("click", function(){
            var zTree = $.fn.zTree.getZTreeObj("tree");
            zTree.addNodes(treeNode, {id:(100 + newCount), pId:treeNode.id, name:"new node" + (newCount++)});
            return false;
        });
    }
    function removeHoverDom(treeId, treeNode) {
        $("#addBtn_"+treeNode.tId).unbind().remove();
    }
    function btnSubmit(){
        var treeObj=$.fn.zTree.getZTreeObj("tree");
        //已选择的菜单
        var nodes=treeObj.getCheckedNodes(true);
        var ids="";
        for(var i=0;i<nodes.length;i++){
            if(nodes[i].id == 0) continue;
            ids+=nodes[i].id + ",";
        }
        if(ids!=""){
            ids = ids.substr(0, (ids.length - 1));
        }
        //未选择的菜单
        var nodes_N=treeObj.getCheckedNodes(false);
        var ids_no = "";
        for(var i=0;i<nodes_N.length;i++){
            if(nodes_N[i].id == 0) continue;
            ids_no += nodes_N[i].id + ",";
        }
        if(ids_no!=""){
            ids_no = ids_no.substr(0, (ids_no.length - 1));
        }

        var role_id = "${roleMap.rid}";
        if(role_id==""){
            $.alert("<p class='red'>角色不正确</p>", null, {title: "系统提示"});
            return;
        }
        $btn = $("#blackSubmit").button('loading');
        $.ajax({
            type:"POST",
            dataType:"JSON",
            data:{"menu_ids":ids, "ids_no":ids_no, "role_id":role_id},
            url:"user/addPrivilege",
            beforeSend:function(XMLHttpRequest){
//                $.alert("<p class='red'>数据处理中，请稍候...</p>",{title:'系统提示'});
                // 禁用按钮防止重复提交
                $btn.attr({ disabled: "disabled" });
            },
            success:function(data,textStatus) {
//                Boxy.hideLoading();
                $btn.removeAttr("disabled");
                $btn.button('reset');
                if(data==null || data=="") {
                    $.alert("<p class='red'>"+data.resultMsg+"</p>", "温馨提示");
                    return false;
                }
                if(data.resultCode=="RESP_SUCCESS"){
                    $.alert("<p class='red'>"+data.resultMsg+"</p>", function(){
                        //location.reload();
                    }, {title: "系统提示"});
                    setTimeout(function () {
                        window.location.href = "${base}user/toAuthMenu?rid="+role_id;
                    },1500)
                } else {
                    $.alert("<p class='red'>"+data.resultMsg+"</p>", null, {title: "系统提示"});
                }
            },
            error:function(httpRequest, statusText, errorThrown) {
//                $.hideLoading();
                $.alert("<p class='red'>系统响应异常!</p>", null, {title: "系统提示"});
            }
        });
    }
</script>
<!-- header start -->
<section class="content-header">
    <div class="nav-tabs-custom">
        <ul class="nav nav-tabs">
            <li class="active"> <#if roleMap??><a href="user/toAuthMenu?rid=${roleMap.rid}">菜单权限<#else>添加菜单</#if> </a></li>
            <li><a href="javascript:" onclick="window.parent.top.location.reload();">重新载入菜单</a></li>
        </ul>
    </div>
</section>

<!-- header end -->
<section class="content-body">
    <div class="row form">
        <div class="col-sm-2" style="width:330px;height:500px;float:left;overflow-x:hidden;border:1px solid #27ae60;">
            <span><b>模块菜单：</b>[<a onclick="return false;" title="全部展开" href="javascript:" id="expandAllBtn">展开</a>] [<a onclick="return false;" title="全部关闭" href="javascript:" id="collapseAllBtn">关闭</a>]</span>
            <ul id="tree" class="ztree"></ul>
        </div>
        <div class="col-sm-4">
            <form id="partnersForm" class="form-horizontal" data-fv-framework="bootstrap" method="post" action="" onsubmit="return false;">
                <div class="form-group">
                    <label class="col-sm-3 control-label"> 角色ID：</label>
                    <div class="col-sm-9 form-control-static">
                        ${roleMap.rid}
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 control-label"> 角色名称:</label>
                    <div class="col-sm-9 form-control-static">
                        ${roleMap.roleName}
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-12">
                        <button id="blackSubmit" class="btn btn-primary" onclick="btnSubmit();" data-loading-text="数据处理中">确定</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</section>

    <#include "../footer.ftl">
</#escape>