package com.wisdom.separate.aop;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.wisdom.separate.service.RoutingMasterSlave;


/**切换数据源注解
 * @author:youben
 * @date:2018/6/25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MasterSlaveDataSource {
    RoutingMasterSlave value();
}