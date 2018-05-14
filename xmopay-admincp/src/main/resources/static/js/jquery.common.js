/*
 * jQuery JavaScript Extend Library Core v1.3.2
 * http://9mikj.com/
 *
 * Copyright (c) 2009 Jvones
 * Dual licensed under the MIT and GPL licenses.
 * http://docs.jquery.com/License
 *
 * Date: 2009-10-30 17:34:21 -0500 
 * Revision: 6246
 */
//当前浏览器
var userAgent = navigator.userAgent.toLowerCase();
var is_opera = userAgent.indexOf('opera') != -1 && opera.version();
var is_moz = (navigator.product == 'Gecko') && userAgent.substr(userAgent.indexOf('firefox') + 8, 3);
var is_ie = (userAgent.indexOf('msie') != -1 && !is_opera) && userAgent.substr(userAgent.indexOf('msie') + 5, 3);

// //搜索展开层
function toggleSearchDiv() {
    $("#hideSearchDiv").toggle();
}

$(function(){
	/*添加*/
    $("#add").click(function(){
        var addUrl = $("#addurl").val();
        var addTitle = $(this).attr("title");
        ui.box.load(addUrl, { title: addTitle });
    });

	/*编辑*/
    $(".edit").click(function(){
        var editUrl = $("#editurl").val() + "?id=" + $(this).attr("val");
        var editTitle = $(this).attr("title");
        ui.box.load(editUrl, { title: editTitle });
    });

	/* 全选or全不选 */
    $("#checkbox_handle").click(function(){
        var isChecked = $(this).prop("checked"); //($(this).attr("checked") == "checked") ? true : false;
        if (isChecked) {
            $(".checkbox").attr("checked", "checked"); //全选
        } else {
            $(".checkbox").removeAttr("checked"); //取消全选
        }
    });

    // //搜索展开层
    $("#searchHandle").click(function(){
        $("#hideSearchDiv").toggle();
    });

    //搜索展开层
    // $("#searchHandle").click(function(){
    //     var isSearchHidden = $("#isSearch").val();
    //     if(isSearchHidden == 1) {
    //         $("#hideSearchDiv").show();
    //         $("#isSearch").val(1);
    //     }else {
    //         $("#hideSearchDiv").hide();
    //         $("#isSearch").val(0);
    //     }
    // });
    //
    // //搜索隐藏层
    // $("#searchHide").click(function(){
    //     $("#hideSearchDiv").hide();
    //     $("#isSearchHidden").val(1);
    // });
});

/**
 * jquery 扩展函数
 */
jQuery.extend({
	/**
	 * 获取COOKIE
	 * @param {String} sName 
	 */	
	getCookie : function(sName) {
		var aCookie = document.cookie.split("; ");
		for (var i=0; i < aCookie.length; i++){
			var aCrumb = aCookie[i].split("=");
			if (sName == aCrumb[0]) return decodeURIComponent(aCrumb[1]);
		}
		return '';
	},
	
	/**
	 * 设置COOKIE
	 * @param {String} sName 
	 * @param {String} sValue 
	 * @param {String} sExpires 
	 */
	setCookie : function(sName, sValue, sExpires) {
		var sCookie = sName + "=" + encodeURIComponent(sValue);
		if (sExpires != null) sCookie += "; expires=" + sExpires;
		document.cookie = sCookie;
	},
	
	/**
	 * 移除COOKIE
	 * @param {String} sName 
	 */
	removeCookie : function(sName) {
		document.cookie = sName + "=; expires=Fri, 31 Dec 1999 23:59:59 GMT;";
	},
	
	/**
	 *  计算文本剩余字数，默认最大长度为微博最大长度。
	 *  如果已超出最大限制字数，返回负值。
	 * @param {String} text
	 * @param {Number} [max] 可选，最大字数（一个字两个字符）
	 * @return {Number} 
	 */
    calText : function(text, max){
        if(max === undefined)
            max = 140;
        var matcher = text.match(/[^\x00-\xff]/g),
            slen  = (matcher && matcher.length) || 0;
        return Math.floor((max*2 - text.length - slen)/2);
    }, 
    
    /**
     * 以字节为长度计算单位截取字符串，一个字两个字节
     * @param {String} text
     * @param {Number} length
     * @return {String} cutString
     */
    byteCut : function(str, length) {
      var slen = this.byteLen(str);
      if(slen>length){
          // 所有宽字用&&代替
          var c = str.replace(/&/g, " ")
                     .replace(/[^\x00-\xff]/g, "&&");
          // c.slice(0, length)返回截短字符串位
          str = str.slice(0, c.slice(0, length)
                    // 由位宽转为JS char宽
                    .replace(/&&/g, " ")
                    // 除去截了半个的宽位
                    .replace(/&/g, "").length
               );
      }
      return str;
    },
    
    /**
     *  替换源字符串中某段为指定向字符串
     * @param {String} source
     * @param {String} replacement
     * @param {Number} fromIndex
     * @param {Number} toIndex
     * @return {String} newString
     */
    stringReplace : function(source, text, from, to){
        return source.substring(0, from) + text + source.substring(to);
    },     
    
    /**
     * 加入收藏夹 
     * @param {String} title 收藏标题
     * @param {String} url URL地址
     */  
    addBookmark: function(title, url) {
        if (window.sidebar) { window.sidebar.addPanel(title, url, ""); }
        else if (document.all) { window.external.AddFavorite(url, title); }
        else if (window.opera && window.print) { return true; }
    },
    
    /**
     * 加入剪贴板 
     * @param {String} text
     * @param {Int} allowAlert 是否允许Alert提示
     */   
    copyToClipboard: function(txt, allowAlert) {
        if (window.clipboardData) {
            window.clipboardData.clearData();
            window.clipboardData.setData("Text", txt);
        }
        else if (navigator.userAgent.indexOf("Opera") != -1) {
            window.location = txt;
        }
        else if (window.netscape || navigator.userAgent.indexOf("Chrome") != -1) {
            alert("您的浏览器不支持脚本复制,请尝试手动复制");
            return false;
            try {
                netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
            }
            catch (e) {
                alert("您的firefox安全限制限制您进行剪贴板操作，请打开'about:config'将signed.applets.codebase_principal_support'设置为true'之后重试");                
            }
            var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
            if (!clip)
                return;
            var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);
            if (!trans)
                return;
            trans.addDataFlavor('text/unicode');
            var str = {};
            var len = {};
            var str = Components.classes["@mozilla.org/supports-string;1"].createInstance(Components.interfaces.nsISupportsString);
            var copytext = txt;
            str.data = copytext;
            trans.setTransferData("text/unicode", str, copytext.length * 2);
            var clipid = Components.interfaces.nsIClipboard;
            if (!clip)
                return false;
            clip.setData(trans, null, clipid.kGlobalClipboard);
        }
        if (allowAlert) { alert("复制成功!");}
    }  
});
 
function isValue(param) {
	return (null == param || '' == param || "" == param || "undefined" == param);
}

/* AJAX get方法 */
/**
 * ajaxPage 翻页请求
 * @param url
 * @param showid
 */
function ajaxPage(url, showid) {
	$.ajax({
		type: "get",
		url: url,
		dataType : "html",
		success: function(data){
            $("#"+showid).empty().html(data);
		},
		error: function(){
			alert("系统请求错误响应!");
		}
	});
}

/**
 *	同步分页
 *	传入参数syncPage(第几页,共几页)
 */
function syncPage(pageindex, pagetotal) {
    var strHtml = "";
    var endPage;
    if (pageindex - 1 < 1) {//是第一页的时候
        strHtml += "<span title='首页'>&lt;&lt;</span> ";
        strHtml += "<span title='上一页'>&lt;</span> ";
    } else {
        strHtml += "<a title='首页' href='?pageIndex=1'>&lt;&lt;</a> ";
        strHtml += "<a title='上一页' href='?pageIndex=" + (pageindex-1) + "'>&lt;</a> ";
    }
    if (pageindex != 1) {strHtml += "<a title='第一页' href='?pageIndex=1'>1</a> ";}
    if (pageindex >= 5) {strHtml += "<span>...</span> ";}
    if (pagetotal > pageindex + 2) {
        endPage = pageindex + 2;
    } else {
        endPage = pagetotal;
    }
    for (var i = pageindex - 2; i <= endPage; i++) {
        if (i > 0) {
            if (i == pageindex) {
                strHtml += "<strong title='第" + i + "页'>" + i + "</strong> ";
            } else {
                if (i != 1 && i != pagetotal) {
                    strHtml += "<a title='第" + i + "页' href='?pageIndex="+i+"'>"+i+"</a> ";
                }
            }
        }
    }
    if (pageindex + 3 < pagetotal) {strHtml += "<span>...</span> ";}//从第3个开始就用。。。代替
    if (pageindex != pagetotal) {strHtml += "<a title='第" + pagetotal + "页' href='?pageIndex=" + pagetotal + "'>" + pagetotal + "</a> ";}
    if (pageindex + 1 > pagetotal) {
        strHtml += "<span title='下一页'>&gt;</span> ";
        strHtml += "<span title='末页'>&gt;&gt;</span> ";
    } else {
        strHtml += "<a title='下一页' href='?pageIndex=" + (pageindex+1) + "'>&gt;</a> ";
        strHtml += "<a title='末页' href='?pageIndex=" + pagetotal + "'>&gt;&gt;</a> ";
    }
    return strHtml;
}