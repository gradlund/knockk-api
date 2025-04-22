package com.knockk.api.util;

import java.util.Date;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;

/**
 * Tracer for logging
 */
public class Tracer extends AbstractMonitoringInterceptor {

    public Tracer() {
    }

    // If true, will use the dynamic Spring logger
    public Tracer(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }

    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        // Before method invocation display a start method trace lof statement
        String name = createInvocationTraceName(invocation);
        long start = System.currentTimeMillis();
        logger.trace("Knockk Method " + name + " execution started at: " + new Date());
        try {
            // Invoke the method
            return invocation.proceed();
        } finally {
            // Display an end method trace log statement
            long end = System.currentTimeMillis();
            long time = end - start;
            logger.trace("Knockk Method " + name + " execution lasted " + time + " ms");
            logger.trace("Knockk Method " + name + " execution ended at: " + new Date());
            if (time > 20) {
                logger.warn("Knockk Method " + name + " execution longer than 20 ms.");
            }
        }
    }

}
