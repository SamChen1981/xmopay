package com.xmopay.admincp.common;

import com.github.pagehelper.PageInfo;
import com.xmopay.common.utils.XmoPayUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class Pagesutils {

    public static String showAjaxPage(HttpServletRequest request, PageInfo page, String ajaxDiv){

        String pageResult = ""; // 封装分页的内容
        String ajaxUrl = request.getRequestURI();
        if(request.getParameterMap().size() > 0){
            Map requestMap = XmoPayUtils.formRequestMap(request.getParameterMap());
            requestMap.remove("inajax");
            requestMap.remove("ajaxDiv");
            requestMap.remove("pageIndex");
            if(requestMap.size() > 0){
                ajaxUrl += "?inajax=1&" + XmoPayUtils.createLinkString(requestMap);
            }else{
                ajaxUrl += "?inajax=1";
            }
        }else{
            ajaxUrl += "?inajax=1";
        }

        int endPage = 0;
        if("".equals(ajaxDiv)){
            ajaxDiv = "listDiv"; //默认ajax div
        }

        if(page.getTotal() == 0){
            return "";
        }

        if (page.getPageNum() - 1 < 1) {//是第一页的时候
            pageResult += "<li><span>«</span></li>";
        } else {
            pageResult += "<li><a onclick=\"ajaxPage('"+ ajaxUrl + "&pageIndex=" + (page.getPageNum()-1) + "&ajaxDiv=" +ajaxDiv+"','"+ajaxDiv+"')\" href=\"javascript:;\">«</a></li>";
        }

        if (page.getPageNum() != 1) {
            pageResult += "<li><a onclick=\"ajaxPage('"+ ajaxUrl + "&pageIndex=1&ajaxDiv=" +ajaxDiv+"','"+ajaxDiv+"')\" href=\"javascript:;\">1</a></li>";
        }

        if (page.getPageNum() >= 4) {
            pageResult += "<li><span>...</span></li>";
        }

        if (page.getPages() > page.getPageNum() + 2) {
            endPage = page.getPageNum() + 2;
        } else {
            endPage = page.getPages();
        }

        for (int i = page.getPageNum() - 2; i <= endPage; i++) {
            if (i > 0) {
                if (i == page.getPageNum()) {
                    pageResult += "<li class=\"active\"><a href=\"javascript:;\">" + i + "</a></li>";
                } else {
                    if (i != 1 && i != page.getPages()) {
                        pageResult += "<li><a onclick=\"ajaxPage('"+ ajaxUrl + "&pageIndex=" + i + "&ajaxDiv=" +ajaxDiv+"', '"+ajaxDiv+"')\" href=\"javascript:;\">"+i+"</a></li>";
                    }
                }
            }
        }
        if (page.getPageNum() + 3 < page.getPages()) {
            pageResult += "<li><span>...</span></li>";
        }//从第3个开始就用。。。代替

        if (page.getPageNum() != page.getPages()) {
            pageResult += "<li><a onclick=\"ajaxPage('"+ ajaxUrl + "&pageIndex=" + page.getPages() + "&ajaxDiv=" +ajaxDiv+"', '"+ajaxDiv+"')\" href=\"javascript:;\">"+ page.getPages() +"</a></li>";
        }

        if (page.getPageNum() + 1 > page.getPages()) {
            pageResult += "<li><span title='下一页'>»</span></li>";
        } else {
            pageResult += "<li><a onclick=\"ajaxPage('"+ ajaxUrl + "&pageIndex=" + (page.getPageNum()+1) + "&ajaxDiv=" +ajaxDiv+"', '"+ajaxDiv+"')\" href=\"javascript:;\">»</a></li>";
        }

//        pageResult += "<nav class=\"pull-right search-page\">共 "+getTotal()+" 页, 到第 <input size=\"2\"> 页 <a class=\"btn btn-default btn-ssm\" href=\"javascript:;\"> 确定 </a></nav>";
        return pageResult;
    }
}
