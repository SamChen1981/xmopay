package com.xmopay.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * com.xmopay.vo
 *
 * @author echo_coco.
 * @date 5:08 PM, 2018/4/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelResponseResult<T> implements Serializable {
    private static final long serialVersionUID = -5550576206009053726L;

    private String code;

    private String msg;

    private T body;

}
