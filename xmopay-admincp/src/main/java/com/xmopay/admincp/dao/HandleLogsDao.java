package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.HandleLogsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HandleLogsDao {
    Integer insertHandleLogs(HandleLogsDto handleLogsDto);

    List<HandleLogsDto> getHandleLogsList(HandleLogsDto handleLogsDto);
}
