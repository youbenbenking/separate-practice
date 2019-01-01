package com.wisdom.separate.service;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**动态数据源类
 * @author:youben
 * @date:2018/7/3
 */
public class MasterSlaveRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return MasterSlaveRoutingContextHolder.getRouteStrategy();
    }
}
