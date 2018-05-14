package com.xmopay.openapi.service.impl;

import com.xmopay.openapi.common.SingleResult;
import com.xmopay.openapi.dao.TradeOrderDao;
import com.xmopay.openapi.dto.TradeOrderDto;
import com.xmopay.openapi.service.TradeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("tradeOrderService")
public class TradeOrderServiceImpl implements TradeOrderService {

    @Autowired
    private TradeOrderDao tradeOrderDao;

    @Override
    public SingleResult<Integer> insertTradeOrder(TradeOrderDto tradeOrderDto) {
        SingleResult<Integer> result = new SingleResult<>(false, null);
        try {
            int line = tradeOrderDao.insertTradeOrder(tradeOrderDto);
            if (line > 0) {
                result.setResult(line);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public SingleResult<Integer> updateTradeOrder(TradeOrderDto tradeOrderDto) {
        SingleResult<Integer> result = new SingleResult<>(false, 0);
        try {
            int res = tradeOrderDao.updateTradeOrder(tradeOrderDto);
            if (res > 0) {
                result.setResult(res);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(0);
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public SingleResult<TradeOrderDto> getTradeOrderInfo(TradeOrderDto tradeOrderDto) {
        SingleResult<TradeOrderDto> result = new SingleResult<>(false, null);
        try {
            TradeOrderDto tradeOrder = tradeOrderDao.getTradeOrderInfo(tradeOrderDto);
            if (null != tradeOrder) {
                result.setResult(tradeOrder);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            e.printStackTrace();
        }
        return result;
    }
}
