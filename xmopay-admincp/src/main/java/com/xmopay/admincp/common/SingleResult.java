package com.xmopay.admincp.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SingleResult<T> implements Serializable {

    private static final long serialVersionUID = -3284587087945229723L;

    private boolean success;

    private T result;
}
