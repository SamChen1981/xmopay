package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.RefundDao;
import com.xmopay.admincp.dto.RefundDto;
import com.xmopay.admincp.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundDao refundDao;

    @Override
    public SingleResult<PageInfo> getRefundList(RefundDto refundDto) {
        SingleResult<PageInfo> result = new SingleResult<>(false, null);
        PageHelper.startPage(refundDto.getCurrentPage(), refundDto.getPageSize());
        List<RefundDto> refundList   = refundDao.getRefundList(refundDto);
        PageInfo<RefundDto> pageInfo = new PageInfo<>(refundList);
        if(refundList != null){
            result.setResult(pageInfo);
            result.setSuccess(true);
        }
        return result;
    }
}
