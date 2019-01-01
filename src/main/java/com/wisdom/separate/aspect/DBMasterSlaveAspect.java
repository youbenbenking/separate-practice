package com.wisdom.separate.aspect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import com.wisdom.separate.aop.MasterSlaveDataSource;
import com.wisdom.separate.service.MasterSlaveRoutingContextHolder;
import com.wisdom.separate.service.RoutingMasterSlave;

import java.lang.reflect.Method;


/**
 * @author:youben
 * @date:2018/6/25
 * @desc:主库从库切换
 */

/**
 * AspectJ中的切入点匹配的执行点称作连接的（Join Point），在通知方法中可以声明一个JoinPoint类型的参数。通过JoinPoint可以访问连接点的细节。主要方法：
	1.java.lang.Object[] getArgs()：获取连接点方法运行时的入参列表； 
	2.Signature getSignature() ：获取连接点的方法签名对象； 
	3.java.lang.Object getTarget() ：获取连接点所在的目标对象； 
	4.java.lang.Object getThis() ：获取代理对象本身； 
 * @author youben
 *
 */
@Service
@Aspect
public class DBMasterSlaveAspect {
    private static final Log logger = LogFactory.getLog(DBMasterSlaveAspect.class);

    /**
     * 把数据源切换为主库数据源
     *
     * @param point
     */
    @Before(value = "@annotation(com.wisdom.separate.aop.MasterSlaveDataSource)")
    public void beforeDB(JoinPoint point) {
        Object target = point.getTarget();
        String methodName = point.getSignature().getName();
        Class<?> clazz = target.getClass();
        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        try {
            Method method = clazz.getMethod(methodName, parameterTypes);
            /**********如果找到数据源注解，则切换成相应的数据源**************/
            if (method != null && method.isAnnotationPresent(MasterSlaveDataSource.class)) {
                MasterSlaveDataSource data = method.getAnnotation(MasterSlaveDataSource.class);
                MasterSlaveRoutingContextHolder.setRouteStrategy(data.value());
            } else {
                MasterSlaveRoutingContextHolder.setRouteStrategy(RoutingMasterSlave.MASTER);
            }
        } catch (Exception e) {
            logger.error("执行DBMasterSlaveAspect.beforeDB数据源切换切面异常", e);
        }
    }

    /**
     * 执行完切入后，把数据源切换成主库数据源
     * @param point
     */
    @After(value = "@annotation(com.wisdom.separate.aop.MasterSlaveDataSource)")
    public void afterDB(JoinPoint point) {
        Object target = point.getTarget();
        String methodName = point.getSignature().getName();
        Class<?> clazz = target.getClass();

        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        try {
            Method method = clazz.getMethod(methodName, parameterTypes);
            /**********如果找到数据源注解，则切换成相应的数据源**************/
            if (method != null && method.isAnnotationPresent(MasterSlaveDataSource.class)) {
                MasterSlaveRoutingContextHolder.setRouteStrategy(RoutingMasterSlave.MASTER);
            }

            /**********如果不是主库数据源，则切换成主库数据源**************/
            if (null != MasterSlaveRoutingContextHolder.getRouteStrategy()
                    && !MasterSlaveRoutingContextHolder.getRouteStrategy().equals(RoutingMasterSlave.MASTER)) {
                MasterSlaveRoutingContextHolder.clearRouteStrategy();
            }
        } catch (Exception e) {
            logger.error("数据源切换切面异常", e);
        }
    }
}
