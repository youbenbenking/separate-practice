package com.wisdom.separate.service;

import com.wisdom.separate.dao.TransferMapper;

/**
 * @author:youb
 * @date:2018/5/28
 */
public interface WisdomHistroyService {
    /**
     * 获取库表里最大主键ID
     * @param transferMapper
     * @return
     */
    long searchMaxId(TransferMapper transferMapper);
}
