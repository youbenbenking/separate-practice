package com.wisdom.separate.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wisdom.separate.model.HistorydataDeleteConfig;
import com.wisdom.separate.model.HistorydataSyncConfig;


/**要同步的Dao接口需继承此接口
 * @author:youb
 * @date:2018/5/1
 */
public interface TransferMapper<T> {
    /**
     * 通过主键获取数据
     *
     * @param id
     * @return
     */
    T selectByPrimaryKey(long id);


    /**
     * 根据主键排序获取前N条数据(同步)
     *
     * @param historydataSyncConfig
     * @return
     */
    List<T> getSyncDataByLimit(HistorydataSyncConfig historydataSyncConfig);

    /**
     * 根据主键排序获取前N条数据(删除)
     *
     * @param historydataDeleteConfig
     * @return
     */
    List<T> getDeleteDataByLimit(HistorydataDeleteConfig historydataDeleteConfig);

    /**
     * 保存数据
     *
     * @param t
     */
    int insertSelective(T t);

    /**
     * 更新数据
     *
     * @param t
     */
    int updateByPrimaryKeySelective(T t);

    /**
     * 根据主键删除数据
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(long id);

    /**
     * 通过日期删除数据
     *
     * @param date
     * @return
     */
    int batchDeleteByDate(Date date);

    /**
     * 通过主键集合删除数据
     *
     * @param idList
     * @param date
     * @return
     */
    long batchDeleteByIdList(@Param("idList") List<Long> idList, @Param("date") Date date);

    /**
     * 根据主键删除数据--where id < m
     *
     * @param id
     * @return
     */
    int batchDeleteById(long id);


    /**
     * 查询最大的主键ID
     *
     * @return
     */
    long searchMaxId();

    /**
     * 查询最小的主键ID
     *
     * @return
     */
    long searchMinId();
}
