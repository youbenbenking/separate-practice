package com.wisdom.separate.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wisdom.separate.dao.DicTableMapper;
import com.wisdom.separate.dao.HistorydataDeleteConfigMapper;
import com.wisdom.separate.dao.HistorydataSyncConfigMapper;
import com.wisdom.separate.dao.TransferMapper;
import com.wisdom.separate.enumClass.SyncCleanDataPriorityEunum;
import com.wisdom.separate.model.HistorydataDeleteConfig;
import com.wisdom.separate.service.WisdomCleanDataService;
import com.wisdom.separate.service.WisdomHistroyService;
import com.wisdom.separate.service.WisdomTransferService;


/**
 * @author:youb
 * @date:2018/5/4
 * @desc:清理历史数据
 */
@Service
public class WisdomCleanDataServiceImpl implements WisdomCleanDataService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HistorydataDeleteConfigMapper historydataDeleteConfigMapper;
    @Autowired
    private HistorydataSyncConfigMapper historydataSyncConfigMapper;
    @Autowired
    private WisdomTransferService transferService;
    @Autowired
    private DicTableMapper dicTableMapper;
    @Autowired
    private WisdomHistroyService wisdomHistroyService;

    /**
     * 删除历史数据,支持数据优先级删除
     * 有7 个等级  7 最高, 1 最低
     */
    @Override
    public void cleanData() {
        /***********表名跟mybatis接口映射列表*************/
        Map<String, Object> mapTable = dicTableMapper.getTableMapper();
        if (null == mapTable) {
            return;
        }

        try {
            //某张表数据删除失败，其他表继续删除，表之间不要按顺序删除，但表内部的记录一定要顺序删除，否则数据会删除不干净，切记切记
            List<HistorydataDeleteConfig> tableList = historydataDeleteConfigMapper.getTableConfigList();
            if (null == tableList || tableList.isEmpty()) {
                return;
            }

            //删除数据前，同步历史库表最大ID，缩小删除数据遍历范围
            getHistroyDataMaxId(tableList, mapTable);

            //执行顺序，按优先级（高 正常 低）顺序执行
            /******************************HigthA 7****************************/
            List<HistorydataDeleteConfig> tableListHigthA = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.HigthA.getValue()).collect(Collectors.toList());
            if (null != tableListHigthA && !tableListHigthA.isEmpty()) {
                execute(tableListHigthA, mapTable);
            }

            /******************************HigthB 6****************************/
            List<HistorydataDeleteConfig> tableListHigthB = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.HigthB.getValue()).collect(Collectors.toList());
            if (null != tableListHigthB && !tableListHigthB.isEmpty()) {
                execute(tableListHigthB, mapTable);
            }

            /******************************HigthC 5****************************/
            List<HistorydataDeleteConfig> tableListHigthC = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.HigthC.getValue()).collect(Collectors.toList());
            if (null != tableListHigthC && !tableListHigthC.isEmpty()) {
                execute(tableListHigthC, mapTable);
            }

            /******************************NormalA 4****************************/
            List<HistorydataDeleteConfig> tableListNormalA = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.NormalA.getValue()).collect(Collectors.toList());
            if (null != tableListNormalA && !tableListNormalA.isEmpty()) {
                execute(tableListNormalA, mapTable);
            }

            /******************************NormalB 3****************************/
            List<HistorydataDeleteConfig> tableListNormalB = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.NormalB.getValue()).collect(Collectors.toList());
            if (null != tableListNormalB && !tableListNormalB.isEmpty()) {
                execute(tableListNormalB, mapTable);
            }

            /******************************NormalC 2****************************/
            List<HistorydataDeleteConfig> tableListNormalC = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.NormalC.getValue()).collect(Collectors.toList());
            if (null != tableListNormalC && !tableListNormalC.isEmpty()) {
                execute(tableListNormalC, mapTable);
            }

            /******************************LowA 1****************************/
            List<HistorydataDeleteConfig> tableListLowA = tableList.stream().filter(c ->
                    c.getPriority() == SyncCleanDataPriorityEunum.LowA.getValue()).collect(Collectors.toList());
            if (null != tableListLowA && !tableListLowA.isEmpty()) {
                execute(tableListLowA, mapTable);
            }
        } catch (Exception e) {
        		logger.info("执行WisdomCleanDataServiceImpl.cleanData异常",e);
        }
    }

    /**
     * 获取历史库表最大ID,在删除数据的时候需要知道同步到那个位置，方便缩小查询数据范围，加快删除速度
     *
     * @param tableList
     * @param mapTable
     */
    public void getHistroyDataMaxId(List<HistorydataDeleteConfig> tableList, Map<String, Object> mapTable) {
       
        try {
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

                long maxId = wisdomHistroyService.searchMaxId(wisdomTransferMapper);
                c.setMaxId(maxId);
                historydataDeleteConfigMapper.updateByPrimaryKeySelective(c);
            });
        } catch (Exception e) {
        		logger.info("【异常】执行WisdomCleanDataServiceImpl.getHistroyDataMaxId异常",e);
        }
    }


    /**
     * 执行删除操作
     *
     * @param tableList
     * @param mapTable
     */
    private void execute(List<HistorydataDeleteConfig> tableList, Map<String, Object> mapTable) {
        try {
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
                transferService.delDataByDate(wisdomTransferMapper, historydataDeleteConfigMapper, c, historydataSyncConfigMapper);
            });
        } catch (Exception e) {
            logger.info("执行WisdomCleanDataServiceImpl.execute异常",e);
        }
    }
}
