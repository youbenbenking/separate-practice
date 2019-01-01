package com.wisdom.separate.dao;


import java.util.List;

import com.wisdom.separate.model.HistorydataDeleteConfig;

/**
 * @author:youb
 * @date:2018/5/4
 */
public interface HistorydataDeleteConfigMapper  extends TransferMapper{
    /**
     * 获取同步表记录
     *
     * @return
     */
    List<HistorydataDeleteConfig> getTableConfigList();
}
