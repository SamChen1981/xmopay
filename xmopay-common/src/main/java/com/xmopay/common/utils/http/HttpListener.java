package com.paycloud.utils.http;

/**
 * Http监听接口
 */
public interface HttpListener {
	
	/**
	 * 成功
	 * @param respBody
	 */
	void onCompleted(String respBody);
	
	/**
	 * 被取消
	 */
	void onCanceled();
	
	/**
	 * 失败
	 * @param e
	 */
	void onFailed(Exception e);
}