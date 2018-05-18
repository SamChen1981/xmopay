package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.HandleLogsDao;
import com.xmopay.admincp.dto.HandleLogsDto;
import com.xmopay.admincp.service.HandleLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HandleLogsServiceImpl implements HandleLogsService {

    @Autowired
    HandleLogsDao handleLogsDao;

    @Override
    public SingleResult<Integer> insertHandleLogs(HandleLogsDto handleLogsDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try {
            int line = handleLogsDao.insertHandleLogs(handleLogsDto);
            if (line > 0) {
                result.setResult(line);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SingleResult<PageInfo> getHandleLogsList(HandleLogsDto handleLogsDto) {
        SingleResult<PageInfo> result = new SingleResult<>(false, null);
        PageHelper.startPage(handleLogsDto.getCurrentPage(), handleLogsDto.getPageSize());
        List<HandleLogsDto> handleLogsPageList = handleLogsDao.getHandleLogsList(handleLogsDto);
        PageInfo<HandleLogsDto> pageInfo = new PageInfo<>(handleLogsPageList);
        if (null != handleLogsPageList) {
            result.setResult(pageInfo);
            result.setSuccess(true);
        }
        return result;
    }
}
