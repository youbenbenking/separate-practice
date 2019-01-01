package com.wisdom.separate.service.impl;

import com.alibaba.fastjson.JSON;
import com.wisdom.separate.aop.MasterSlaveDataSource;
import com.wisdom.separate.dao.HistorydataDeleteConfigMapper;
import com.wisdom.separate.dao.HistorydataSyncConfigMapper;
import com.wisdom.separate.dao.TransferMapper;
import com.wisdom.separate.model.HistorydataDeleteConfig;
import com.wisdom.separate.model.HistorydataSyncConfig;
import com.wisdom.separate.service.RoutingMasterSlave;
import com.wisdom.separate.service.WisdomTransferService;
import com.wisdom.separate.utils.CalendarUtil;
import com.wisdom.separate.vo.ReturnMessage;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author:youb
 * @date:2018/5/1
 * @desc:数据迁移
 */
@Service
public class WisdomTransferServiceImpl<T> implements WisdomTransferService<T> {
    private static final Logger logger = LoggerFactory.getLogger(WisdomTransferServiceImpl.class);

    private static final String parmFormat = "yyyy-MM-dd HH:mm:ss";;
    
    //防止删除天数配置错误，这里面的表至少配置75天
    private static List<String> safeTableList = new ArrayList<>();

    static {
        //用户表
        safeTableList.add("wisdom_user");
    }

    /**
     * 从线上数据迁移数据到到历史库
     * 1 获取数据在线上正常使用的库，
     * 2 保存数据在历史库
     * 3 当插入成功后，会自动切换到线上数据库
     * 4 更新线上库的同步历史表索引
     *
     * @param transferMapper            更新数据的接口
     * @param historydataSyncConfigMapper 历史记录接口对象
     * @param historydataSyncConfig       历史记录对象
     */
    @Override
    public void transfer(TransferMapper transferMapper,
    						HistorydataSyncConfigMapper historydataSyncConfigMapper,
    						HistorydataSyncConfig historydataSyncConfig) {
        try {
            /*******执行多少个小时******/
            int excuteHour = 6;
            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.add(Calendar.HOUR_OF_DAY, excuteHour);
            
            logger.info(historydataSyncConfig.getTableName() + "开始时间：" + DateFormatUtils.format(new Date(), parmFormat));
            boolean taskTimeEnd = false;
            //同步多少天前的数据
            while (true) {
                if (new Date().after(calendarEnd.getTime())) {
                    taskTimeEnd = true;
                    logger.info(historydataSyncConfig.getTableName() + "数据同步任务到时");
                    break;
                }
                
                //获取线上数据库现有数据(这里getSyncData方法走从库,采用AOP实现,本类中获取代理对象需AopContext.currentProxy()实现)
                List<T> data = ((WisdomTransferService) AopContext.currentProxy()).getSyncData(transferMapper, historydataSyncConfig);
                if (null == data || data.isEmpty()) {
                    break;
                }
               
                /**********保存数据到历史库**************/
                for (T t : data) {
                    ReturnMessage message = ((WisdomTransferService) AopContext.currentProxy()).saveData(transferMapper, t);
                    //Code=000 表示插入成功，只要插入、更新失败，就退出当前表的同步
                    if (!message.getSuccess() || null == message.getData()) {
                        return;
                    }
                    
                    //插入成功，更新索引位置
                    historydataSyncConfig.setPositionIndex((long) message.getData());
                    //Code=000 表示插入成功，Code=111 表示更新成功，插入记录插入数量，更新不记录插入数量
                    if ("000".equalsIgnoreCase(message.getCode())) {
                        long insertCount = historydataSyncConfig.getInsertCount() + 1;
                        //统计插入的数据，更新的数据不统计，作为埋点统计插入量
                        historydataSyncConfig.setInsertCount(insertCount);
                    }
                    historydataSyncConfig.setUpdateTime(new Date());
                    historydataSyncConfigMapper.updateByPrimaryKeySelective(historydataSyncConfig);
                }
            }
            logger.info(historydataSyncConfig.getTableName() + "结束时间：" + DateFormatUtils.format(new Date(), parmFormat));
        } catch (Exception e) {
            logger.error("执行WisdomContactServiceImpl.transfer异常!", e);

        }
    }

    /**
     * 保存数据--注意含自增主键保存
     *
     * @param data
     */
    @MasterSlaveDataSource(value = RoutingMasterSlave.HISTORY)
    @Override
    public ReturnMessage saveData(TransferMapper transferMapper, T data) {
        ReturnMessage errorMessage = new ReturnMessage();
        errorMessage.setSuccess(false);
        if (null == transferMapper || null == data) {
            return errorMessage;
        }
        try {
            /**********判断对象是否有ID属性，需要通过iD主键获取对象**************/
            Field field = data.getClass().getDeclaredField("id");
            if (null == field) {
                errorMessage.setMsg("不能找到ID属性");
                return errorMessage;
            }
            /**********把属性设置为可见，否则异常**************/
            field.setAccessible(true);
            /**********获取ID属性值**************/
            long id = (long) field.get(data);
            if (id <= 0) {
                errorMessage.setMsg("ID小于0，不能更新或者插入");
                return errorMessage;
            }
            /**********通过ID查询对象是否存在，存在更新，不存在插入数据**************/
            T t = (T) transferMapper.selectByPrimaryKey(id);
            if (null == t) {
            		transferMapper.insertSelective(data);
                errorMessage.setCode("000");
            } else {
            		transferMapper.updateByPrimaryKeySelective(data);
                errorMessage.setCode("111");
            }

            errorMessage.setData(id);
            errorMessage.setSuccess(true);
        } catch (Exception e) {
            logger.error("执行WisdomContactServiceImpl.saveData异常!", e);
        }

        return errorMessage;
    }

    /**
     * 删除历史数据
     *
     * @param transferMapper
     * @param historydataDeleteConfigMapper 删除数据表接口
     * @param historydataDeleteConfig       删除数据配置信息
     * @param historydataSyncConfigMapper   同步数据表接口
     */
    @Override
    public void deleteData(TransferMapper transferMapper,
    						  HistorydataDeleteConfigMapper historydataDeleteConfigMapper,
                           HistorydataDeleteConfig historydataDeleteConfig,
                           HistorydataSyncConfigMapper historydataSyncConfigMapper) {
        if (null == transferMapper || null == historydataDeleteConfigMapper || null == historydataDeleteConfig || null == historydataSyncConfigMapper) {
            return;
        }

        HistorydataSyncConfig historydataSyncConfig = historydataSyncConfigMapper.getTableConfig(historydataDeleteConfig.getTableName());
        /****************如果同步配置表没有配置需要删除的表，不允许删除；同步后的表才能删除，防止丢失数据********************/
        if (null == historydataSyncConfig) {
            return;
        }
        /**********如果表删除的索引位置大于等于更新表的索引位置，不能删除，因为数据还未同步 这里是获取删除前的检查***************/
        if (historydataDeleteConfig.getPositionIndex() >= historydataSyncConfig.getPositionIndex()) {
            return;
        }
        boolean safeFlag = safeDeleteData(historydataDeleteConfig, historydataSyncConfigMapper);
        if (!safeFlag) {
            return;
        }
        /***************不存在需要删除的数据 返回***********************/
        List<T> deleteData = getDeleteData(transferMapper, historydataDeleteConfig);
        if (null == deleteData || deleteData.isEmpty()) {
            return;
        }
        /**************使用for循环，不能用foreach，只要有失败，一定要退出循环
         *  一定要保证数据顺序删除，否则数据会删除不干净******************/
        for (T t : deleteData) {
            historydataSyncConfig = historydataSyncConfigMapper.getTableConfig(historydataDeleteConfig.getTableName());
            if (null == historydataSyncConfig) {
                break;
            }
            /**********如果表删除的索引位置大于等于更新表的索引位置，不能删除，因为数据还未同步，这里是获取删除后的检查***************/
            if (historydataDeleteConfig.getPositionIndex() >= historydataSyncConfig.getPositionIndex()) {
                break;
            }
            ReturnMessage errorMessage = deleteDataByPk(transferMapper, t);
            if (!errorMessage.success()) {
                break;
            }
            if (null == errorMessage.getData()) {
                break;
            }

            historydataDeleteConfig.setPositionIndex((long) errorMessage.getData());
            //统计删除了多少条数据，方便分析用，作为埋点数据
            historydataDeleteConfig.setDeleteCount(historydataDeleteConfig.getDeleteCount() + 1);
            historydataDeleteConfigMapper.updateByPrimaryKeySelective(historydataDeleteConfig);
        }
    }

    /**
     * 根据日期批量删除数据
     *
     * @param wisdomTransferMapper
     * @param wisdomHistorydataDeleteConfigMapper
     * @param wisdomHistorydataDeleteConfig
     */
    @Override
    public void delDataByDate(TransferMapper transferMapper,
    							HistorydataDeleteConfigMapper historydataDeleteConfigMapper,
    							HistorydataDeleteConfig historydataDeleteConfig,
    							HistorydataSyncConfigMapper historydataSyncConfigMapper) {
        if (null == transferMapper || null == historydataDeleteConfig) {
            return;
        }

        boolean safeFlag = safeDeleteData(historydataDeleteConfig, historydataSyncConfigMapper);
        if (!safeFlag) {
            return;
        }

        //最大删除主键的值
        long maxId = 0L;
        //最小删除主键的值
        long minId = transferMapper.searchMinId();
        /***********兼容未取得历史库表的maxId*************/
        if (0L == historydataDeleteConfig.getMaxId() || historydataDeleteConfig.getMaxId() <= minId) {
            maxId = transferMapper.searchMaxId();
        } else {
            maxId = historydataDeleteConfig.getMaxId();
        }

        //每次删除多少数据
        long pageSize = historydataDeleteConfig.getCount();
        boolean taskTimeEnd = false;

        logger.info(historydataDeleteConfig.getTableName() + "开始时间：" + DateFormatUtils.format(new Date(), parmFormat));

        //执行多少个小时
        int excuteHour = 4;
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.HOUR_OF_DAY, excuteHour);
        
        
        //控制删除数据的日期，精确到天，在这个日期前的数据才会被删除
        Date date = CalendarUtil.addDays(new Date(), -historydataDeleteConfig.getDayCount());
        long m = (maxId - minId) / pageSize;
        for (long i = 0; i < m; i++) {
            if (new Date().after(calendarEnd.getTime())) {
                taskTimeEnd = true;
                break;
            }

            long startIndex = (pageSize * i) + minId;
            long endIndex = pageSize * (i + 1) + minId;
            
            List<Long> idList = new ArrayList<>();
            for (long a = startIndex; a < endIndex; a++) {
                idList.add(Long.valueOf(a));
            }
            //视数据量的多少，时间不定
            long finishCount = transferMapper.batchDeleteByIdList(idList, date);
            idList.clear();
            if (0 == finishCount) {
                continue;
            }
            /******统计删除了多少条数据，方便分析用，作为埋点数据*****/
            long deleteCount = historydataDeleteConfig.getDeleteCount() + finishCount;
            historydataDeleteConfig.setDeleteCount(deleteCount);
            historydataDeleteConfig.setUpdateTime(new Date());
            historydataDeleteConfigMapper.updateByPrimaryKeySelective(historydataDeleteConfig);
        }

        logger.info(historydataDeleteConfig.getTableName() + "结束时间：" + DateFormatUtils.format(new Date(), parmFormat));
    }

    /**
     * 删除数据--注意通过主键删除
     *
     * @param transferMapper
     * @param data
     * @return
     */
    @Override
    public ReturnMessage deleteDataByPk(TransferMapper transferMapper, T data) {
        ReturnMessage message = new ReturnMessage();
        message.setSuccess(false);
        if (null == transferMapper) {
            return message;
        }
        try {
            /**********判断对象是否有ID属性，需要通过iD主键获取对象**************/
            Field field = data.getClass().getDeclaredField("id");
            if (null == field) {
            		message.setMsg("不能找到ID属性");
                return message;
            }
            /**********把属性设置为可见，否则异常**************/
            field.setAccessible(true);
            /**********获取ID属性值**************/
            long id = (long) field.get(data);
            if (id <= 0) {
            		message.setMsg("ID小于0，不能更新或者插入");
                return message;
            }
            /**********通过ID删除数据**************/
            int flag = transferMapper.deleteByPrimaryKey(id);
            /*********删除成功**********/
            if (flag > 0) {
            		message.setSuccess(true);
            		message.setData(id);
            }
        } catch (Exception e) {
            logger.error("执行WisdomContactServiceImpl.deleteData异常!", e);
        }

        return message;
    }

    /**
     * 获取需要插入到历史库的数据
     *
     * @param transferMapper
     * @param historydataSyncConfig
     * @return
     */
    @MasterSlaveDataSource(value = RoutingMasterSlave.SLAVE)
    @Override
    public List<T> getSyncData(TransferMapper transferMapper,
    								HistorydataSyncConfig historydataSyncConfig) {
        List<T> list = new ArrayList<>();
        try {
            boolean syncFlag = safeSyncData(historydataSyncConfig);
            if (!syncFlag) {
                return list;
            }
            /***************获取前N天的数据*******************/
            Date before = CalendarUtil.addDays(new Date(), -historydataSyncConfig.getDayCount());
            historydataSyncConfig.setUpdateTime(before);
            list = transferMapper.getSyncDataByLimit(historydataSyncConfig);

        } catch (Exception e) {
            logger.error(String.format("执行WisdomTransferServiceImpl.getData异常!", e));
        }

        return list;
    }

    /**
     * 获取需要删除的数据
     *
     * @param transferMapper
     * @param historydataDeleteConfig
     * @return
     */
    private List<T> getDeleteData(TransferMapper transferMapper,
    									HistorydataDeleteConfig historydataDeleteConfig) {
        if (null == transferMapper || null == historydataDeleteConfig) {
            return new ArrayList<T>();
        }
        /*******控制删除数据的日期，精确到天，在这个日期前的数据才会被删除********/
        Date date = CalendarUtil.addDays(new Date(), -historydataDeleteConfig.getDayCount());
        historydataDeleteConfig.setUpdateTime(date);

        return transferMapper.getDeleteDataByLimit(historydataDeleteConfig);
    }

    /**
     * 删除数据安全限制
     * 1 根据天数
     * 2 如果是特殊的表 比如案件表 需要在程序配置，防止人工操作失误，误删数据
     *
     * @param historydataDeleteConfig
     * @return
     */
    private boolean safeDeleteData(HistorydataDeleteConfig historydataDeleteConfig, HistorydataSyncConfigMapper historydataSyncConfigMapper) {
        if (null == historydataDeleteConfig) {
            return false;
        }

        HistorydataSyncConfig historydataSyncConfig = historydataSyncConfigMapper.getTableConfig(historydataDeleteConfig.getTableName());
        /****************如果同步配置表没有配置需要删除的表，不允许删除；同步后的表才能删除，防止丢失数据********************/
        if (null == historydataSyncConfig) {
            String body = "表名:" + historydataDeleteConfig.getTableName() + ",同步配置表没有配置需要删除的表，不允许删除；同步后的表才能删除，防止丢失数据";
            //发送邮件
            return false;
        }

        //如果表删除的天数大于等于更新表的天数，不能删除，因为数据还未同步 这里是获取删除前的检查,控制删除数据天数大于同步天数14天
        if (historydataDeleteConfig.getDayCount() <= historydataSyncConfig.getDayCount() + 14) {
            String body = "表名:" + historydataSyncConfig.getTableName() + ",删除表配置的天数小于等于同步配置表同步的天数，不允许删除;" +
                    			",删除数据天数应当比同步天数大于14天以上，同步后的表才能删除，防止丢失数据";
          //发送邮件
            return false;
        }

        //安全限制，防止误配置、误操作 ，如果删除的数据小于==180天前，不允许删除
        if (safeTableList.contains(historydataDeleteConfig.getTableName())) {
            if (historydataDeleteConfig.getDayCount() <= 75) {
                safeMsgWarn(historydataDeleteConfig, 75);
                return false;
            }
        }

        //安全限制，防止误配置、误操作 ，如果删除的数据小于==60天前，不允许删除
        if (historydataDeleteConfig.getDayCount() <= 45) {
            safeMsgWarn(historydataDeleteConfig, 45);
            return false;
        }

        return true;
    }

    /**
     * 同步数据安全限制
     * 1 根据天数
     * 2 如果是特殊的表 比如案件表 需要在程序配置，防止人工操作失误，同步数据不能在近期，要不更新的数据不会进入历史库
     *
     * @param wisdomHistorydataSyncConfig
     * @return
     */
    private boolean safeSyncData(HistorydataSyncConfig historydataSyncConfig) {
        if (null == historydataSyncConfig) {
            return false;
        }
        /***************安全限制，防止误配置、误操作 ，如果同步的数据小于==90天前，不允许删除**************/
        if (safeTableList.contains(historydataSyncConfig.getTableName())) {
            if (historydataSyncConfig.getDayCount() <= 90) {
                safeMsgWarn(historydataSyncConfig, 90);
                return false;
            }
        }

        /***************安全限制，防止误配置、误操作 ，如果同步的数据小于==60天前，不允许同步**************/
        if (historydataSyncConfig.getDayCount() <= 29) {
            safeMsgWarn(historydataSyncConfig, 29);
            return false;
        }

        return true;
    }

    /**
     * 删除安全警告信息
     *
     * @param historydataDeleteConfig
     * @return
     */
    private void safeMsgWarn(HistorydataDeleteConfig historydataDeleteConfig, int dayCount) {
        String title = "【警告】删除数据配置";
        String info = String.format("表名:【%s】，配置的删除天数有误，系统只能删前【%s】天的数据，请及时修改",
        				historydataDeleteConfig.getTableName(), dayCount);
        //发送邮件,或者其他操作
        logger.info(String.format("%s，%s", title, info));
    }

    /**
     * 同步安全警告信息
     *
     * @param historydataSyncConfig
     * @return
     */
    private void safeMsgWarn(HistorydataSyncConfig historydataSyncConfig, int dayCount) {
        String title = "【警告】同步数据配置";
        String info = String.format("表名:【%s】，配置的同步天数有误，系统只能同步前【%s】天的数据，请及时修改",
        					historydataSyncConfig.getTableName(), dayCount);
        //发送邮件,或者其他操作
        logger.info(String.format("%s，%s", title, info));
    }
}
