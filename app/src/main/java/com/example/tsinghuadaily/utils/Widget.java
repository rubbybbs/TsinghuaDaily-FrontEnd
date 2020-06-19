package com.example.tsinghuadaily.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Widget {
    Group group() default Group.School;

    Class widgetClass() default void.class;

    String name() default "";

    String docUrl() default "";

    int iconRes() default 0;
}