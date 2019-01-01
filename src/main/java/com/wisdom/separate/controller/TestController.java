package com.wisdom.separate.controller;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wisdom.separate.aop.MasterSlaveDataSource;
import com.wisdom.separate.model.WisdomCard;
import com.wisdom.separate.service.RoutingMasterSlave;
import com.wisdom.separate.service.WisdomCardService;
import com.wisdom.separate.service.WisdomCleanDataService;
import com.wisdom.separate.service.WisdomSyncService;
import com.wisdom.separate.vo.ReturnMessage;

/**
 * Created by youben on 2018/10/10.
 */
@RestController
@RequestMapping(value = "/api/v1")
public class TestController {
		private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
		 @Autowired
		 private WisdomCardService wisdomCardService;
		 @Autowired
		 private WisdomSyncService wisdomSyncService;
		 @Autowired
		 private WisdomCleanDataService wisdomCleanDataService;
	 
	 
	 	@RequestMapping(value = "/saveCardInfo", method = RequestMethod.GET)
		public ReturnMessage saveCardInfo() {
	 		ReturnMessage message = wisdomCardService.saveCardInfo();
			return message;
		}
	 	@RequestMapping("/getCardInfo")
		@MasterSlaveDataSource(value=RoutingMasterSlave.SLAVE)
		public ReturnMessage getCardInfo() {
	 		List<WisdomCard> resultList = wisdomCardService.getCardInfo();
	 		
	 		if (resultList == null || resultList.isEmpty()) {
	 			resultList = new ArrayList<>();
			}
	 		
	 		ReturnMessage message = new ReturnMessage();
	 		message.setSuccess(true);
	 		message.setData(resultList);
			return message;
		}
	 
	    //数据迁移
	    @RequestMapping(value = "/transferData", method = RequestMethod.GET)
	    public String transferData() {
	    		wisdomSyncService.transferT();
	    		return "transferData success";
	    }
	    
	    //数据删除
	    @RequestMapping(value = "/deleteData", method = RequestMethod.GET)
	    public String cleanData() {
	    		wisdomCleanDataService.cleanData();
	    		return "cleanData success";
	    }
	    
	    
}
