<#escape x as (x)!>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <base href="${base}">
    <meta charset="UTF-8">
    <title>XMOPAY系统管理中心</title>
    <link rel="stylesheet" href="static/plugins/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="static/plugins/font-awesome/css/font-awesome.min.css" />
    <link rel="stylesheet" href="static/plugins/form-validation/css/formValidation.min.css" />
    <link rel="stylesheet" href="static/plugins/jquery-confirm/jquery-confirm.min.css" />
    <link rel="stylesheet" href="static/css/admincp.css?v=2018010122"/>

    <script type="text/javascript" src="static/js/jquery.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="static/js/jquery.common.js" charset="utf-8"></script>
    <script type="text/javascript" src="static/plugins/bootstrap/js/bootstrap.min.js" charset="utf-8"></script>

    <script type="text/javascript" src="static/plugins/jquery-confirm/jquery-confirm.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="static/plugins/form-validation/js/formValidation.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="static/plugins/form-validation/framework/bootstrap.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="static/plugins/form-validation/js/zh_CN.js" charset="utf-8"></script>

    <!--[if lt IE 9]>
    <link rel="stylesheet" href="static/plugins/font-awesome/css/font-awesome-ie7.min.css" />
    <script src="static/plugins/html5shiv.min.js"></script>
    <script src="static/plugins/respond.min.js"></script>
    <![endif]-->

    <script type="text/javascript">
        var menu = ${menus};
        var viewPath = '.';
        if (self != top){
            /* 在框架内，则跳出框架 */
            top.location = self.location;
        }
    </script>
    <script type="text/javascript" src="static/js/admincp.index.js" charset="utf-8"></script>
</head>
<body>
<div id="header" class="header">

    <div class="header-logo"></div>
    <div class="header-nav">
        <div class="header-navmenu">
            <ul id="navlist">
            </ul>
        </div>
    </div>
    <div class="header-panel">
        <div class="header-title">
			<span>
				<strong title="登录名">您好，${users.userName}
                    [<span class="orange" title="角色名称">${roleName!''}</span>]
                </strong>
				<a href="user/logout">退出</a>
			</span>
        </div>
    </div>
</div>
<div id="content">
    <div id="left">
        <div id="leftMenus">
            <dl id="submenu">
                <dt id="submenuTitle"></dt>
            </dl>
            <dl id="history" class="history">
                <dt id="historyText">操作历史</dt>
            </dl>
        </div>
    </div>
    <div id="right">
        <iframe frameborder="0" style="display:none;" width="100%" id="workspace"></iframe>
    </div>
</div>
</body>
</html>
</#escape>