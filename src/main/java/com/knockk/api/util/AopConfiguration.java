package com.knockk.api.util;


import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Encapsulates logging
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfiguration {
    // Setup for the controller and service classes
    // Defines classes to intercept
    @Pointcut("execution(* com.knockk.api..controller..*(..)) || execution (* com.knockk.api..business..*(..)) || execution(* com.knockk.api..data*(..))")
    public void monitor(){

    }

    // Get aninstsance of the Tracer that will be used in the aspect
    @Bean
    public Tracer tracer(){
        return new Tracer(true);
    }

    // Setup the ascpect with the Tracer and reference to the monitor() pointcut
    @Bean
    public Advisor performanceMonitorAdvisor(){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("com.knockk.api.util.AopConfiguration.monitor()");
        return new DefaultPointcutAdvisor(pointcut, tracer());
    }
}
