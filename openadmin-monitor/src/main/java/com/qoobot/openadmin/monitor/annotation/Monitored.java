package com.qoobot.openadmin.monitor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 监控注解
 * 用于标记需要被监控的业务方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitored {
    /**
     * 业务操作名称
     */
    String value() default "";
    
    /**
     * 是否记录执行时间
     */
    boolean recordTime() default true;
    
    /**
     * 是否记录调用次数
     */
    boolean recordCount() default true;
}