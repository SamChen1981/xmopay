package com.xmopay.funds.dao;

import com.xmopay.funds.dto.PartnerAccountDto;

/**
 * com.xmopay.funds.dao
 *
 * @author echo_coco.
 * @date 3:26 PM, 2018/4/27
 */
public interface PartnerAccountDao {

    PartnerAccountDto getPartnerAccountByPid(String partnerId);

    PartnerAccountDto getPartnerAccountByPidLock(String partnerId);

    int updatePartnerAccount(PartnerAccountDto partnerAccountDto);
}
