package com.wisdom.separate.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wisdom.separate.dao.WisdomCardMapper;
import com.wisdom.separate.model.WisdomCard;
import com.wisdom.separate.service.WisdomCardService;
import com.wisdom.separate.vo.ReturnMessage;


/**
 * @author:youb
 * @date:2018/5/28
 */
@Service
public class WisdomCardServiceImpl implements WisdomCardService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	 @Autowired
	 private WisdomCardMapper wisdomCardMapper;
	 
	@Override
	public ReturnMessage saveCardInfo() {
		ReturnMessage message = new ReturnMessage();
		message.setSuccess(true);
		message.setMsg("保存成功!");
		try {	
			Date now = new Date();
			List<WisdomCard> cardList = new ArrayList<>(10000);
			for (int i = 0; i < 10000; i++) {
				WisdomCard wisdomCard = new WisdomCard();
				wisdomCard.setName("发条"+i);
				wisdomCard.setCardNo("66666-"+i);
				wisdomCard.setCreateTime(now);
				wisdomCard.setUpdateTime(now);
				cardList.add(wisdomCard);
			}
		
			//批量插入数据
			int toIndex = 2000;
	        for (int i=0 ; i< cardList.size(); i += 2000){
	            if (i + 2000 > cardList.size()){
	                toIndex = cardList.size() - i;
	            }
	
	            List<WisdomCard> newCardList = cardList.subList(i,i+toIndex);
	            wisdomCardMapper.insertCardBatch(newCardList);
	            
	        }
		}catch(Exception e) {
			logger.error("插入异常", e);
			message.setSuccess(false);
			message.setMsg("保存失败!");
		}
			return message;
	}

	@Override
	public List<WisdomCard> getCardInfo() {
		Map<String, Object> param = new HashMap<>();
		param.put("page", 0);
		param.put("size", 1000);
		
		List<WisdomCard> list = wisdomCardMapper.selectCard(param);
		return list;
	}
    
}
