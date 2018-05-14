package com.xmopay.admincp.dao;

import com.xmopay.admincp.dto.RefundDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RefundDao {
    List<RefundDto> getRefundList(RefundDto refundDto);
}
