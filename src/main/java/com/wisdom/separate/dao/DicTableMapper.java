package com.wisdom.separate.dao;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author:youb
 * @date:2018/5/2
 * @desc:同步数据，表名跟类对象映射
 */
@Service
public class DicTableMapper {
	
	@Autowired
    private WisdomCardMapper wisdomCardMapper;

	/**
     * 表跟类的映射关系
     * key 真实的表名
     * value 表对应的Mapper
     * @return
     */
    public Map<String, Object> getTableMapper() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("wisdom_card", wisdomCardMapper);

        return map;
    }
}
