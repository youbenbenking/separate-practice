package com.wisdom.separate.service;

import org.springframework.util.Assert;



/**绑定当前线程数据源类
 * 用ThreadLcoal管理当前数据源
 * @author:youben
 * @date:2018/7/3
 */
public class MasterSlaveRoutingContextHolder {
	
    private static final ThreadLocal<RoutingMasterSlave> contextHolder = new ThreadLocal<>();

    private MasterSlaveRoutingContextHolder() {

    }

    public static void setRouteStrategy(RoutingMasterSlave dbType) {
        Assert.notNull(dbType, "dbType cannot be null");
        contextHolder.set(dbType);
    }

    public static RoutingMasterSlave getRouteStrategy() {
        return contextHolder.get();
    }

    public static void clearRouteStrategy() {
        contextHolder.remove();
    }

}
