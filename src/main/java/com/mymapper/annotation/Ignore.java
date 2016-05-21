package com.mymapper.annotation;

import java.lang.annotation.*;

/**
 * 使用@Ignore标记的字段， 在根据实体类转换为SQL语句的时候会忽略该字段
 *
 * @author hjk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Ignore {
}
