package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.BillingsDto;

import java.util.List;
import java.util.Map;

public interface BillingsService {
    SingleResult<PageInfo> getBillingsList(BillingsDto billingsDto);

    SingleResult<Map> getBillingsTotal(BillingsDto billingsDto);

    SingleResult<PageInfo> getBillingStatics(Map paramMap);

    SingleResult<Map> getBillingStaticsTotal(Map paramMap);

    SingleResult<List<Map>> exportBillingStatics(Map<String,Object> paramMap);
}
