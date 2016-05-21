package com.mymapper.annotation;

import java.lang.annotation.*;

/**
 * Created by huang on 3/30/16.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    String tableName();

    String id() default "";
}
