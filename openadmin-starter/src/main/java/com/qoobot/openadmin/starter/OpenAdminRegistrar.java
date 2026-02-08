package com.qoobot.openadmin.starter;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenAdmin 导入选择器
 * 根据 @EnableOpenAdmin 注解的属性动态选择要导入的配置类
 */
public class OpenAdminRegistrar implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        MultiValueMap<String, Object> attributes = importingClassMetadata.getAllAnnotationAttributes(
                EnableOpenAdmin.class.getName(), false);
        
        List<String> imports = new ArrayList<>();
        
        // 基础自动配置总是导入
        imports.add("com.qoobot.openadmin.starter.OpenAdminAutoConfiguration");
        
        // 注意：具体的模块配置类将在后续版本中实现
        // 目前仅支持基础自动配置功能
        
        return imports.toArray(new String[0]);
    }
}