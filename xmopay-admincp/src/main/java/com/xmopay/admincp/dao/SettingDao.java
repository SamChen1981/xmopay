package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.SettingDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SettingDao {
    /**
     * @param
     * @Description: [后台] 系统管理 - 添加配置
     */
    Integer replaceIntoSettings(SettingDto settingDto);

    /**
     * @param
     * @Description: 获取系统配置表 信息
     * @return
     */
    List<SettingDto> getSettingList();

}
