package com.wisdom.separate.dao;


import java.util.List;

import com.wisdom.separate.model.HistorydataSyncConfig;

/**
 * @author:youben
 * @date:2018/5/2
 * @desc:数据同步信息记录
 */
public interface HistorydataSyncConfigMapper extends TransferMapper {
    /**
     * 获取同步表记录
     *
     * @return
     */
    List<HistorydataSyncConfig> getTableConfigList();

    /**
     * 获取表配置信息
     *
     * @param tableName
     * @return
     */
    HistorydataSyncConfig getTableConfig(String tableName);

    /**
     * 更新数据
     *
     * @param historydataSyncConfig
     */
    int updateByPrimaryKeySelective(HistorydataSyncConfig historydataSyncConfig);

}
