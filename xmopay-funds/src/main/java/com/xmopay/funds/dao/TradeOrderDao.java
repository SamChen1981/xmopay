package com.xmopay.funds.dao;

import com.xmopay.funds.dto.TradeOrderDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * com.xmopay.funds.dao
 *
 * @author echo_coco.
 * @date 11:35 AM, 2018/4/27
 */
public interface TradeOrderDao {

    public TradeOrderDto getTradeOrderByOrderSnToLock(TradeOrderDto tradeOrderDto);

    public int updateTradeOrderStatus(TradeOrderDto tradeOrderDto);
}
