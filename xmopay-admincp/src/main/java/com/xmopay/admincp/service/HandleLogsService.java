package com.xmopay.admincp.service;

import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.HandleLogsDto;

public interface HandleLogsService {
    SingleResult<Integer> insertHandleLogs(HandleLogsDto handleLogsDto);

    SingleResult<PageInfo> getHandleLogsList(HandleLogsDto handleLogsDto);
}
