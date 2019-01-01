package com.wisdom.separate.aspect;
//package com.wisdom.aspect;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Service;
//
//import com.wisdom.aop.RoutingDataSource;
//import com.wisdom.serverProvider.dbHistory.DynamicRoutingContextHolder;
//
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author:binlongwang
// * @date:2018/5/1
// * @desc:数据源切换
// */
//@Service
//@Aspect
//public class DBHistoryPrdAspect {
//    private static final Log logger = LogFactory.getLog(DBHistoryPrdAspect.class);
//
//    /**
//     * 把数据源切换为历史数据源
//     *
//     * @param point
//     */
//    //@Before(value = "execution(* com.toporperty.platform.service..*.*(..))")
//    @Before(value = "@annotation(com.toporperty.platform.aop.RoutingDataSource)")
//    public void beforeDB(JoinPoint point) {
//        Object target = point.getTarget();
//        String methodName = point.getSignature().getName();
//        Class<?> clazz = target.getClass();
//        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
//        try {
//            Method method = clazz.getMethod(methodName, parameterTypes);
//            /**********如果找到数据源注解，则切换成相应的数据源**************/
//            if (method != null && method.isAnnotationPresent(RoutingDataSource.class)) {
//                RoutingDataSource data = method.getAnnotation(RoutingDataSource.class);
//                DynamicRoutingContextHolder.setRouteStrategy(data.value());
//            }
//        } catch (Exception e) {
//            logger.error("数据源切换切面异常", e);
//        }
//    }
//
//    /**
//     * 执行完切入后，把数据源切换成线上数据源
//     *
//     * @param point
//     */
//    @After(value = "@annotation(com.toporperty.platform.aop.RoutingDataSource)")
//    public void afterDB(JoinPoint point) {
//        try {
//            DynamicRoutingContextHolder.clearRouteStrategy();
//        } catch (Exception e) {
//            logger.error("数据源切换切面异常", e);
//        }
//    }
//}
