package com.xmopay.admincp.service.impl;

import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.AdminAuthDao;
import com.xmopay.admincp.dto.AdminAuthDto;
import com.xmopay.admincp.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by monica on 28/03/2018.
 */
@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    @Autowired
    private AdminAuthDao adminAuthDao;

    /**
    * @param
    * @Description: 删除 权限数据
    * @return
    */
    @Override
    public SingleResult<Integer> deleteAuth(AdminAuthDto adminAuthDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        int n = adminAuthDao.deleteAuth(adminAuthDto);
        if (n > 0) {
            result.setResult(n);
            result.setSuccess(true);
        }
        return result;
    }

    /**
    * @param
    * @Description: 增加权限
    * @return
    */
    @Override
    public SingleResult<Integer> addAuth(AdminAuthDto adminAuthDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try{
            int n = adminAuthDao.insertAuth(adminAuthDto);
            if (n > 0) {
                result.setResult(n);
                result.setSuccess(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
