package com.qoobot.openadmin.starter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 OpenAdmin 框架注解
 * 通过此注解可以选择性启用框架的不同模块
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OpenAdminRegistrar.class)
public @interface EnableOpenAdmin {

    /**
     * 是否启用安全模块
     * 默认启用
     */
    boolean security() default true;

    /**
     * 是否启用监控模块
     * 默认启用
     */
    boolean monitoring() default true;

    /**
     * 是否启用网关模块
     * 默认不启用
     */
    boolean gateway() default false;

    /**
     * 是否启用配置中心模块
     * 默认启用
     */
    boolean config() default true;

    /**
     * 是否启用管理后台模块
     * 默认启用
     */
    boolean admin() default true;

    /**
     * 指定要启用的模块列表
     * 可以通过模块名称精确控制启用哪些模块
     */
    String[] modules() default {};

    /**
     * 是否启用所有模块
     * 如果设置为 true，则忽略其他单独的模块配置
     */
    boolean allModules() default false;
}