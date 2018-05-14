package com.xmopay.admincp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmopay.admincp.common.SingleResult;
import com.xmopay.admincp.dao.BillingsDao;
import com.xmopay.admincp.dto.BillingsDto;
import com.xmopay.admincp.service.BillingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BillingsServiceImpl implements BillingsService {

    @Autowired
    private BillingsDao billingsDao;

    @Override
    public SingleResult<PageInfo> getBillingsList(BillingsDto billingsDto) {
        SingleResult<PageInfo> result = new SingleResult<>(false, null);
        PageHelper.startPage(billingsDto.getCurrentPage(), billingsDto.getPageSize());
        List<BillingsDto> lists = billingsDao.getBillingsList(billingsDto);
        PageInfo<BillingsDto> pageInfo = new PageInfo<>(lists);
        if (lists != null) {
            result.setResult(pageInfo);
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    public SingleResult<Map> getBillingsTotal(BillingsDto billingsDto) {
        SingleResult<Map> singleResult = new SingleResult<>(false,null);
        try{
            Map totalMap = billingsDao.getBillingsTotal(billingsDto);
            if(totalMap != null ){
                singleResult.setSuccess(true);
                singleResult.setResult(totalMap);
                return singleResult;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return singleResult;
    }
}
