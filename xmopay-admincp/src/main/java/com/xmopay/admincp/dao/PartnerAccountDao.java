package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.PartnerAccountDto;
import org.apache.ibatis.annotations.Mapper;

import java.sql.SQLException;

@Mapper
public interface PartnerAccountDao {

    int insertPartnerAccount(PartnerAccountDto record);

    /**
     * @param
     * @Description: 根据partner_id获取商户账户表信息
     * @return
     */
    PartnerAccountDto getPartnerAccountByPratnerId(String partnerId);


    /**
     * @param
     * @Description: 根据partner_id修改商户账户表信息
     * @return
     */
    int updatePartnerAccountByPid(PartnerAccountDto partnerAccountDto);

    /**
     * 当前账户扣款
     * @param partnerAccountDto
     * @return
     */
    int updateDebitMoney(PartnerAccountDto partnerAccountDto) throws SQLException;
}
