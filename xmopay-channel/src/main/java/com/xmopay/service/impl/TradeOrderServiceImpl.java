package com.xmopay.service.impl;

import com.xmopay.common.utils.XmoPayUtils;
import com.xmopay.dao.TradeOrderDao;
import com.xmopay.dto.TradeOrderDto;
import com.xmopay.service.ITradeOrderService;
import com.xmopay.vo.VoResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * com.xmopay.service.impl
 *
 * @author echo_coco.
 * @date 1:43 PM, 2018/5/2
 */
@Service
public class TradeOrderServiceImpl implements ITradeOrderService {

    @Autowired
    private TradeOrderDao tradeOrderDao;

    @Override
    public VoResult<TradeOrderDto> getOrderInfo(String orderSn) {
        VoResult<TradeOrderDto> result = new VoResult<>("10000", false, "", null);
        try {
            TradeOrderDto tradeOrderDto = tradeOrderDao.getTradeOrderInfo(orderSn);
            if (tradeOrderDto != null && !XmoPayUtils.isEmpty(tradeOrderDto.getPartnerId())) {
                result.setCode("0000");
                result.setData(tradeOrderDto);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public VoResult<Integer> updateTradeOrder(TradeOrderDto tradeOrderDto) {
        VoResult<Integer> result = new VoResult<>("10000", false, "", null);
        try {
            int line = tradeOrderDao.updateTradeOrder(tradeOrderDto);
            if (line > 0) {
                result.setCode("0000");
                result.setData(1);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setErrMessage(e.getMessage());
            result.setSuccess(false);
            result.setData(null);
        }
        return result;
    }
}
