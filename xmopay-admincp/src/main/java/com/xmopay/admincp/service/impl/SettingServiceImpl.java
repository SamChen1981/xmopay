package com.xmopay.admincp.service.impl;

import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.SettingDao;
import com.xmopay.admincp.dto.SettingDto;
import com.xmopay.admincp.service.SettingService;
import com.xmopay.common.utils.XmoPayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    SettingDao settingDao;

    @Override
    public SingleResult<Integer> replaceIntoSettings(SettingDto settingDto) {
        SingleResult<Integer> result = new  SingleResult<Integer>(false, null);
        int n = settingDao.replaceIntoSettings(settingDto);
        if(n > 0){
            result.setResult(n);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * @param
     * @Description: 获取 系统配置表 信息
     * @return
     */
    @Override
    public SingleResult<List<SettingDto>> getSettingsList() {
        SingleResult<List<SettingDto>> result = new SingleResult<List<SettingDto>>(false, null);
        List<SettingDto> list = settingDao.getSettingList();
        if(!XmoPayUtils.isNull(list)){
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }
}
