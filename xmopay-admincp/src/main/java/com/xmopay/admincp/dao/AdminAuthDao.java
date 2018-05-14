package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.AdminAuthDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by mimi on 1/05/2018.
 */
@Mapper
public interface AdminAuthDao {



    /**
    * @param
    * @Description: 删除权限数据
    * @return
    */
    int deleteAuth(AdminAuthDto adminAuthDto);

    /**
    * @param
    * @Description: 增加权限
    * @return
    */
    int insertAuth(AdminAuthDto adminAuthDto);
}
