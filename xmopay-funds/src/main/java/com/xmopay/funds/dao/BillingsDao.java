package com.xmopay.funds.dao;

import com.xmopay.funds.dto.BillingsDto;

/**
 * com.xmopay.funds.dao
 *
 * @author echo_coco.
 * @date 3:46 PM, 2018/4/27
 */
public interface BillingsDao {

    int insertBillings(BillingsDto billingsDto);
}
