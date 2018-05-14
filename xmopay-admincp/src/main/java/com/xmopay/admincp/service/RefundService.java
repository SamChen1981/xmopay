package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.RefundDto;

public interface RefundService {
    SingleResult<PageInfo> getRefundList(RefundDto refundDto);
}
