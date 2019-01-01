package com.wisdom.separate.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.wisdom.separate.model.WisdomCard;

public interface WisdomCardMapper extends TransferMapper{
    int deleteByPrimaryKey(Long id);

    int insert(WisdomCard record);

    int insertSelective(WisdomCard record);

    WisdomCard selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WisdomCard record);

    int updateByPrimaryKey(WisdomCard record);
    
    List<WisdomCard> selectCard(Map<String, Object> param); 
    
    int insertCardBatch (@Param("cardList") List<WisdomCard> cardList);
}