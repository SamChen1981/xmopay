package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.BillingsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BillingsDao {

    /**
     * 获取账单明细表 所有 列表数据
     * @param billingsDto
     * @return
     */
    List<BillingsDto> getBillingsList(BillingsDto billingsDto);

    Map getBillingsTotal(BillingsDto billingsDto);

    List<Map> getBillingStaticsList(Map paramMap);

    Map getBillingStaticsTotal(Map paramMap);
}
