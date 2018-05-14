package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.BillingsDto;

import java.util.Map;

public interface BillingsService {
    SingleResult<PageInfo> getBillingsList(BillingsDto billingsDto);

    SingleResult<Map> getBillingsTotal(BillingsDto billingsDto);
}
