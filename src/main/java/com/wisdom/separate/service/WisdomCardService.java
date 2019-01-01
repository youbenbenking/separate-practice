package com.wisdom.separate.service;


import java.util.List;

import com.wisdom.separate.model.WisdomCard;
import com.wisdom.separate.vo.ReturnMessage;

/**
 * @author:youb
 * @date:2018/5/28
 */
public interface WisdomCardService {
    /**
     * 批量插入数据
     * @param 
     * @return
     */
	public ReturnMessage saveCardInfo();
	/**
     * 查询数据
     * @param 
     * @return
     */
	public List<WisdomCard> getCardInfo();
}
