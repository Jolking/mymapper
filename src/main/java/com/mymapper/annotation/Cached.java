package com.mymapper.annotation;

import java.lang.annotation.*;

/**
 * Created by huang on 5/21/16.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Cached {
    String value();
}
