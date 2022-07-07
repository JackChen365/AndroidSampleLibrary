package com.github.jackchen.android.sample.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Register {
    /**
     * title for this sample
     */
    String title();

    /**
     * description for this sample
     */
    String desc() default "";

    String path() default "";
}