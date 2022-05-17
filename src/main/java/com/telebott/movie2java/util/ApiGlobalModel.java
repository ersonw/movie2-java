package com.telebott.movie2java.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Swagger扩展注解
 * 用于application/json请求
 * 并使用诸如Map或JSONObject等非具体实体类接收参数时,对参数进行进一步描述
 */
@Target({
        ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiGlobalModel {

    /**
     * 字段集合容器
     *
     * @return Global model
     */
    Class<?> component();

    /**
     * 分隔符
     *
     * @return separator
     */
    String separator() default ",";

    /**
     * 实际用到的字段
     * 可以是字符串数组,也可以是一个字符串 多个字段以分隔符隔开: "id,name"
     * 注意这里对应的是component里的属性名,但swagger显示的字段名实际是属性注解上的name
     *
     * @return value
     */
    String[] value() default {
    };
}