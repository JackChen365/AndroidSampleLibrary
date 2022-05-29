package com.github.jackchen.android.sample.library.component.memory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Created by cz
 * @date 2020-01-29 19:06
 * @email bingo110@126.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SampleMemory {
    boolean value() default true;
}