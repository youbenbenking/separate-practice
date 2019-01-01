package com.wisdom.separate.service;


import java.util.List;

import com.wisdom.separate.dao.HistorydataDeleteConfigMapper;
import com.wisdom.separate.dao.HistorydataSyncConfigMapper;
import com.wisdom.separate.dao.TransferMapper;
import com.wisdom.separate.model.HistorydataDeleteConfig;
import com.wisdom.separate.model.HistorydataSyncConfig;
import com.wisdom.separate.vo.ReturnMessage;

/**
 * @author:youb
 * @date:2018/5/1
 */
public interface WisdomTransferService<T> {
    /**
     * 传输数据
     *
     * @param transferMapper            更新数据的接口
     * @param historydataSyncConfigMapper 历史记录接口对象
     * @param historydataSyncConfig       历史记录对象
     */
    void transfer(TransferMapper transferMapper,
    				  HistorydataSyncConfigMapper historydataSyncConfigMapper,
                  HistorydataSyncConfig historydataSyncConfig);


    /**
     * 保存数据--注意含自增主键保存
     *
     * @param transferMapper
     * @param data
     */
    ReturnMessage saveData(TransferMapper transferMapper, T data);

    /**
     * 删除历史记录
     *
     * @param transferMapper
     * @param historydataDeleteConfigMapper 删除数据表接口
     * @param historydataDeleteConfig       删除数据配置信息
     * @param historydataSyncConfigMapper   同步数据表接口
     */
    void deleteData(TransferMapper transferMapper,
    					HistorydataDeleteConfigMapper historydataDeleteConfigMapper,
                    HistorydataDeleteConfig historydataDeleteConfig,
                    HistorydataSyncConfigMapper historydataSyncConfigMapper);

    /**
     * 通过日期删除数据--删除多少天前的数据
     *
     * @param transferMapper
     * @param tistorydataDeleteConfig
     */
    void delDataByDate(TransferMapper transferMapper,
    					   HistorydataDeleteConfigMapper historydataDeleteConfigMapper,
                       HistorydataDeleteConfig historydataDeleteConfig,
                       HistorydataSyncConfigMapper historydataSyncConfigMapper);

    /**
     * 根据主键删除数据
     *
     * @param transferMapper
     * @param data
     * @return
     */
    ReturnMessage deleteDataByPk(TransferMapper transferMapper, T data);

    /**
     * 获取同步数据
     * @param transferMapper
     * @param historydataSyncConfig
     * @return
     */
    List<T> getSyncData(TransferMapper transferMapper,
    				HistorydataSyncConfig historydataSyncConfig);
}
