package com.xmopay.api;

/**
 * com.xmopay.api
 *
 * @author echo_coco.
 * @date 10:14 PM, 2018/5/2
 */
public abstract class BasicApi {

    protected String responseString(String code, String message, String body) {
        StringBuilder stringBuilder = new StringBuilder(4);
        stringBuilder.append("{");
        stringBuilder.append("\"return_code\":\"");
        stringBuilder.append(code);
        stringBuilder.append("\",");
        stringBuilder.append("\"return_message\":\"");
        stringBuilder.append(message);
        if (body != null) {
            stringBuilder.append("\",");
            stringBuilder.append("\"body\":");
            stringBuilder.append(body);
            stringBuilder.append("}");
        } else {
            stringBuilder.append("\"}");
        }
        return stringBuilder.toString();
    }
}
