package com.xmopay.admincp.service;

import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dto.SettingDto;

import java.util.List;

public interface SettingService {

    /**
     * @param
     * @Description:
     */
    SingleResult<Integer> replaceIntoSettings(SettingDto settingDto);

    /**
     * @param
     * @Description: 获取系统配置表 信息
     */
    SingleResult<List<SettingDto>> getSettingsList();

}
