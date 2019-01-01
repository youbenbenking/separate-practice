package com.wisdom.separate.service.impl;

import com.wisdom.separate.dao.DicTableMapper;
import com.wisdom.separate.dao.HistorydataSyncConfigMapper;
import com.wisdom.separate.dao.TransferMapper;
import com.wisdom.separate.enumClass.SyncCleanDataPriorityEunum;
import com.wisdom.separate.model.HistorydataSyncConfig;
import com.wisdom.separate.service.WisdomHistroyService;
import com.wisdom.separate.service.WisdomSyncService;
import com.wisdom.separate.service.WisdomTransferService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author:youb
 * @date:2018/5/2
 * @desc:数据同步
 */
@Service
public class WisdomSyncServiceImpl implements WisdomSyncService {
    private static final Logger logger = LoggerFactory.getLogger(WisdomSyncServiceImpl.class);
    @Autowired
    private WisdomTransferService wisdomTransferService;
    @Autowired
    private HistorydataSyncConfigMapper historydataSyncConfigMapper;
    @Autowired
    private DicTableMapper dicTableMapper;
    @Autowired
    private WisdomHistroyService wisdomHistroyService;
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 30,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    /**
     * 数据迁移,支持数据优先级迁移
     * 有7 个等级  7 最高, 1 最低
     */
    @Override
    public void transferT() {
        /***********表名跟mybatis接口映射列表*************/
        Map<String, Object> mapTable = dicTableMapper.getTableMapper();
        if (null == mapTable || mapTable.isEmpty()) {
            return;
        }

        try {
            /********某张表数据同步失败，其他表继续同步，表之间不要按顺序同步，但表内部的记录一定要顺序同步，否则同步会漏数据，切记切记***/
            List<HistorydataSyncConfig> tableList = historydataSyncConfigMapper.getTableConfigList();
            if (null == tableList || tableList.isEmpty()) {
                return;
            }
            
            //获取历史库表最大ID,在删除数据的时候需要知道同步到那个位置，方便缩小查询数据范围，加快删除速度
            tableList.parallelStream().forEach(c -> {
                /********未配置表名称，不执行删除操作*********/
                if (StringUtils.isEmpty(c.getTableName())) {
                    return;
                }
                TransferMapper wisdomTransferMapper = (TransferMapper) mapTable.get(c.getTableName());
                /********未找到mybatis接口对象，退出本次循环***********/
                if (null == wisdomTransferMapper) {
                    return;
                }

                //查询历史表最大主键
                long maxId = wisdomHistroyService.searchMaxId(wisdomTransferMapper);
                c.setPositionIndex(maxId);
                historydataSyncConfigMapper.updateByPrimaryKeySelective(c);
            });
            
            /***************************执行顺序，按优先级（高 正常 低）顺序执行*******************************/
            /***************************HigthA 7 *******************************/
            List<HistorydataSyncConfig> tableListHightA = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.HigthA.getValue()).collect(Collectors.toList());
            if (null != tableListHightA && !tableListHightA.isEmpty()) {
                execute(tableListHightA, mapTable);
            }
            /***************************HigthA 6 *******************************/
            List<HistorydataSyncConfig> tableListHigthB = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.HigthB.getValue()).collect(Collectors.toList());
            if (null != tableListHigthB && !tableListHigthB.isEmpty()) {
                execute(tableListHigthB, mapTable);
            }

            /***************************HigthA 5 *******************************/
            List<HistorydataSyncConfig> tableListHigthC = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.HigthC.getValue()).collect(Collectors.toList());
            if (null != tableListHigthC && !tableListHigthC.isEmpty()) {
                execute(tableListHigthC, mapTable);
            }

            /***************************NormalA 4 *******************************/
            List<HistorydataSyncConfig> tableListNormalA = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.NormalA.getValue()).collect(Collectors.toList());
            if (null != tableListNormalA && !tableListNormalA.isEmpty()) {
                execute(tableListNormalA, mapTable);
            }

            /***************************NormalA 3*******************************/
            List<HistorydataSyncConfig> tableListNormalB = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.NormalB.getValue()).collect(Collectors.toList());
            if (null != tableListNormalB && !tableListNormalB.isEmpty()) {
                execute(tableListNormalB, mapTable);
            }

            /***************************NormalA 2*******************************/
            List<HistorydataSyncConfig> tableListNormalC = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.NormalC.getValue()).collect(Collectors.toList());
            if (null != tableListNormalC && !tableListNormalC.isEmpty()) {
                execute(tableListNormalC, mapTable);
            }

            /***************************LowA 1*******************************/
            List<HistorydataSyncConfig> tableListLowA = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.LowA.getValue()).collect(Collectors.toList());
            if (null != tableListLowA && !tableListLowA.isEmpty()) {
                execute(tableListLowA, mapTable);
            }
        } catch (Exception ex) {
            logger.error(String.format("执行WisdomContactServiceImpl.transferT异常,异常信息[%s]", ex.toString()));
        }
    }

    /**
     * 执行同步操作
     *
     * @param tableList
     * @param mapTable
     */
    private void execute(List<HistorydataSyncConfig> tableList, Map<String, Object> mapTable) {
        try {
            /**经过验证，使用线程池，同时对多表（11张表）操作，会造成historydataSyncConfig频繁更新，数据库CPU占据20%，
             * 故不要采用线程池，使用流程方式，最大2个线程执行**/
            tableList.parallelStream().forEach(c -> {
                /********未配置表名称，不执行同步操作*********/
                if (StringUtils.isEmpty(c.getTableName())) {
                    return;
                }
                TransferMapper transferMapper = (TransferMapper) mapTable.get(c.getTableName());
                /********未找到mybatis接口对象，退出本次循环***********/
                if (null == transferMapper) {
                    return;
                }
                wisdomTransferService.transfer(transferMapper, historydataSyncConfigMapper, c);
            });
        } catch (Exception ex) {
            logger.error(String.format("%s,异常信息[%s]", "执行WisdomSyncServiceImpl.execute异常", ex.toString()));
        }
    }


}
