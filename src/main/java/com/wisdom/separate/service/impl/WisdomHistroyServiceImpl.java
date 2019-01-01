package com.wisdom.separate.service.impl;

import org.springframework.stereotype.Service;

import com.wisdom.separate.aop.MasterSlaveDataSource;
import com.wisdom.separate.dao.TransferMapper;
import com.wisdom.separate.service.RoutingMasterSlave;
import com.wisdom.separate.service.WisdomHistroyService;


/**
 * @author:youb
 * @date:2018/5/28
 */
@Service
public class WisdomHistroyServiceImpl implements WisdomHistroyService {
    /**
     * 获取历史库表主键最大ID
     *
     * @param transferMapper
     * @return
     */
	@MasterSlaveDataSource(value = RoutingMasterSlave.HISTORY)
    @Override
    public long searchMaxId(TransferMapper transferMapper) {
        Long maxId = 0L;
        try {
            maxId = transferMapper.searchMaxId();
        } catch (Exception e) {
            maxId = 0L;
        }
        if (null == maxId) {
            maxId = 0L;
        }

        return maxId;
    }
}
